package vertgreen.util.ratelimit;

import vertgreen.db.EntityReader;
import vertgreen.db.EntityWriter;
import vertgreen.db.entity.BlacklistEntry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Blacklist {

    //this holds progressively increasing lengths of blacklisting in milliseconds
    private static final List<Long> blacklistLevels;

    static {
        List<Long> levels = new ArrayList<>();
        levels.add(1000L * 60);                     //one minute
        levels.add(1000L * 600);                    //ten minutes
        levels.add(1000L * 3600);                   //one hour
        levels.add(1000L * 3600 * 24);              //24 hours
        levels.add(1000L * 3600 * 24 * 7);          //a week

        blacklistLevels = Collections.unmodifiableList(levels);
    }

    private final long rateLimitHitsBeforeBlacklist;

    private final Long2ObjectOpenHashMap<BlacklistEntry> blacklist;

    //users that can never be blacklisted
    private final Set<Long> userWhiteList;


    public Blacklist(Set<Long> userWhiteList, long rateLimitHitsBeforeBlacklist) {
        this.blacklist = new Long2ObjectOpenHashMap<>();
        //load blacklist from database
        for (BlacklistEntry ble : EntityReader.loadBlacklist()) {
            blacklist.put(ble.id, ble);
        }

        this.rateLimitHitsBeforeBlacklist = rateLimitHitsBeforeBlacklist;
        this.userWhiteList = Collections.unmodifiableSet(userWhiteList);
    }

    public boolean isBlacklisted(long id) {

        //first of all, ppl that can never get blacklisted no matter what
        if (userWhiteList.contains(id)) return false;

        BlacklistEntry blEntry = blacklist.get(id);
        if (blEntry == null) return false;     //blacklist entry doesn't even exist
        if (blEntry.level < 0) return false;   //blacklist entry exists, but id hasn't actually been blacklisted yet

        //id was a blacklisted, but it has run out
        if (System.currentTimeMillis() > blEntry.blacklistedTimestamp + (getBlacklistTimeLength(blEntry.level)))
            return false;
        return true;
    }

    public long hitRateLimit(long id) {
        //update blacklist entry of this id
        long blacklistingLength = 0;
        BlacklistEntry blEntry = blacklist.get(id);
        if (blEntry == null)
            blEntry = getOrCreateBlacklistEntry(id);

        //synchronize on the individual blacklist entries since we are about to change and save them
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (blEntry) {
            long now = System.currentTimeMillis();

            //is the last ratelimit hit a long time away (1 hour)? then reset the ratelimit hits
            if (now - blEntry.rateLimitReachedTimestamp > 60 * 60 * 1000) {
                blEntry.rateLimitReached = 0;
            }
            blEntry.rateLimitReached++;
            blEntry.rateLimitReachedTimestamp = now;
            if (blEntry.rateLimitReached >= rateLimitHitsBeforeBlacklist) {
                //issue blacklist incident
                blEntry.level++;
                if (blEntry.level < 0) blEntry.level = 0;
                blEntry.blacklistedTimestamp = now;
                blEntry.rateLimitReached = 0; //reset these for the next time

                blacklistingLength = getBlacklistTimeLength(blEntry.level);
            }
            //persist it
            //if this turns up to be a performance bottleneck, have an agent run that persists the blacklist occasionally
            EntityWriter.mergeBlacklistEntry(blEntry);
            return blacklistingLength;
        }
    }

    private synchronized BlacklistEntry getOrCreateBlacklistEntry(long id) {
        //was one created in the meantime? use that
        BlacklistEntry result = blacklist.get(id);
        if (result != null) return result;

        //create and return it
        result = new BlacklistEntry(id);
        blacklist.put(id, result);
        return result;
    }

    public synchronized void liftBlacklist(long id) {
        blacklist.remove(id);
        EntityWriter.deleteBlacklistEntry(id);
    }

    private long getBlacklistTimeLength(int blacklistLevel) {
        if (blacklistLevel < 0) return 0;
        return blacklistLevel >= blacklistLevels.size() ? blacklistLevels.get(blacklistLevels.size() - 1) : blacklistLevels.get(blacklistLevel);
    }
}
