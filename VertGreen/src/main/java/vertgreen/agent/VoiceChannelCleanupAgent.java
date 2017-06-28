package vertgreen.agent;

import vertgreen.VertGreen;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoiceChannelCleanupAgent extends Thread {

    private static final Logger log = LoggerFactory.getLogger(VoiceChannelCleanupAgent.class);
    private static final HashMap<String, Long> VC_LAST_USED = new HashMap<>();
    private static final int CLEANUP_INTERVAL_MILLIS = 20000 * 10;
    private static final int UNUSED_CLEANUP_THRESHOLD = 20000 * 60; // Effective when users are in the VC, but the player is not playing

    public VoiceChannelCleanupAgent() {
        super("voice-cleanup");
        setDaemon(true);
        setPriority(4);
    }

    @Override
    public void run() {
        log.info("Started voice-cleanup");
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                cleanup();
                sleep(CLEANUP_INTERVAL_MILLIS);
            } catch (Exception e) {
                log.error("Caught an exception while trying to clean up voice channels!", e);
                try {
                    sleep(1000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }

    private void cleanup(){
        List<Guild> guilds = VertGreen.getAllGuilds();
        log.info("Checking " + guilds.size() + " guilds for stale voice connections.");

        int total = 0;
        int closed = 0;

        for(Guild guild : guilds) {
            if (guild != null
                    && guild.getSelfMember() != null
                    && guild.getSelfMember().getVoiceState() != null
                    && guild.getSelfMember().getVoiceState().getChannel() != null) {

                total++;
                VoiceChannel vc = guild.getSelfMember().getVoiceState().getChannel();

                if (getHumanMembersInVC(vc).size() == 0){
                    closed++;
                    guild.getAudioManager().closeAudioConnection();
                    VC_LAST_USED.remove(vc.getId());
                } else if(isBeingUsed(vc)) {
                    VC_LAST_USED.put(vc.getId(), System.currentTimeMillis());
                } else {
                    // Not being used! But there are users in te VC. Check if we've been here for a while.

                    if(!VC_LAST_USED.containsKey(vc.getId())) {
                        VC_LAST_USED.put(vc.getId(), System.currentTimeMillis());
                    }

                    long lastUsed = VC_LAST_USED.get(vc.getId());

                    if (System.currentTimeMillis() - lastUsed > UNUSED_CLEANUP_THRESHOLD) {
                        closed++;
                        guild.getAudioManager().closeAudioConnection();
                        VC_LAST_USED.remove(vc.getId());
                    }
                }
            }
        }

        log.info("Closed " + closed + " of " + total + " voice connections.");
    }

    private List<Member> getHumanMembersInVC(VoiceChannel vc){
        ArrayList<Member> l = new ArrayList<>();

        for(Member m : vc.getMembers()){
            if(!m.getUser().isBot()){
                l.add(m);
            }
        }

        return l;
    }

    private boolean isBeingUsed(VoiceChannel vc) {
        GuildPlayer guildPlayer = PlayerRegistry.getExisting(vc.getGuild());

        return guildPlayer != null && guildPlayer.isPlaying();
    }

}
