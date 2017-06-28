package vertgreen.util.ratelimit;

import vertgreen.VertGreen;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collections;
import java.util.Set;

public class Ratelimit {

    public enum Scope {USER, GUILD}

    private final Long2ObjectOpenHashMap<Rate> limits;
    private final long maxRequests;
    private final long timeSpan;

    //users that can never be limited
    private final Set<Long> userWhiteList;

    //are we limiting the individual user or whole guilds?
    public final Scope scope;

    //class of commands this ratelimiter should be restricted to
    //creative use allows usage of other classes
    private final Class clazz;

    public Class getClazz() {
        return clazz;
    }

    public Ratelimit(Set<Long> userWhiteList, Scope scope, long maxRequests, long milliseconds, Class clazz) {
        this.limits = new Long2ObjectOpenHashMap<>();

        this.userWhiteList = Collections.unmodifiableSet(userWhiteList);
        this.scope = scope;
        this.maxRequests = maxRequests;
        this.timeSpan = milliseconds;
        this.clazz = clazz;
    }

    public boolean isAllowed(Member invoker, int weight) {
        return isAllowed(invoker, weight, null, null);
    }

    public boolean isAllowed(Member invoker, int weight, Blacklist blacklist, TextChannel blacklistOutput) {
        //This gets called real often, right before every command execution. Keep it light, don't do any blocking stuff,
        //ensure whatever you do in here is threadsafe, but minimize usage of synchronized as it adds overhead

        //first of all, ppl that can never get limited or blacklisted, no matter what
        if (userWhiteList.contains(invoker.getUser().getIdLong())) return true;

        //user or guild scope?
        long id;
        if (scope == Scope.GUILD) id = invoker.getGuild().getIdLong();
        else id = invoker.getUser().getIdLong();

        Rate rate = limits.get(id);
        if (rate == null)
            rate = getOrCreateRate(id);

        //synchronize on the individual rate objects since we are about to change and save them
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (rate) {
            long now = System.currentTimeMillis();

            //clear outdated timestamps
            long maxTimeStampsToClear = (now - rate.lastUpdated) * maxRequests / timeSpan;
            long cleared = 0;
            while (rate.timeStamps.size() > 0 && rate.timeStamps.getLong(0) + timeSpan < now && cleared < maxTimeStampsToClear) {
                rate.timeStamps.removeLong(0);
                cleared++;
            }

            rate.lastUpdated = now;
            //ALLOWED?
            if (rate.timeStamps.size() < maxRequests) {
                for (int i = 0; i < weight; i++)
                    rate.timeStamps.add(now);
                //everything is fine, get out of this method
                return true;
            }
        }

        //reaching this point in the code means a rate limit was hit
        //the following code has to handle that

        if (blacklist != null && scope == Scope.USER)
            VertGreen.executor.submit(() -> bannerinoUserino(invoker, blacklist, blacklistOutput));
        return false;
    }

    private void bannerinoUserino(Member invoker, Blacklist blacklist, TextChannel channel) {
        long length = blacklist.hitRateLimit(invoker.getUser().getIdLong());
        if (length <= 0) {
            return; //nothing to do here
        }
        long s = length / 1000;
        String duration = String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        String out = "\uD83D\uDD28 _**BLACKLISTED**_ \uD83D\uDD28 for **" + duration + "**";
        channel.sendMessage(invoker.getAsMention() + ": " + out).queue();
    }

    private synchronized Rate getOrCreateRate(long id) {
        //was one created on the meantime? use that
        Rate result = limits.get(id);
        if (result != null) return result;

        //create, save and return it
        result = new Rate(id);
        limits.put(id, result);
        return result;
    }

    public synchronized void liftLimit(long id) {
        limits.remove(id);
    }

    class Rate {
        //to whom this belongs
        final long id;

        //last time this object was updated
        //useful for keeping track of how many timeStamps should be removed to ensure the limit is enforced
        long lastUpdated;

        //collects the requests
        LongArrayList timeStamps;

        private Rate(long id) {
            this.id = id;
            this.lastUpdated = System.currentTimeMillis();
            this.timeStamps = new LongArrayList();
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }
    }
}
