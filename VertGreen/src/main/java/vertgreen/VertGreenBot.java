package vertgreen;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import vertgreen.audio.PlayerRegistry;
import vertgreen.event.EventLogger;
import vertgreen.event.ShardWatchdogListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertGreenBot extends VertGreen {

    private static final Logger log = LoggerFactory.getLogger(VertGreenBot.class);
    private final int shardId;
    private final EventListener listener;

    public VertGreenBot(int shardId) {
        this(shardId, null);
    }

    public VertGreenBot(int shardId, EventListener listener) {
        this.shardId = shardId;
        this.listener = listener;
        log.info("Building shard " + shardId);
        jda = buildJDA();
    }

    private JDA buildJDA() {
        shardWatchdogListener = new ShardWatchdogListener();

        JDA newJda = null;

        try {
            boolean success = false;
            while (!success) {
                JDABuilder builder = new JDABuilder(AccountType.BOT)
                        .addEventListener(new EventLogger())
                        .addEventListener(shardWatchdogListener)
                        .setToken(Config.CONFIG.getBotToken())
                        .setBulkDeleteSplittingEnabled(true)
                        .setEnableShutdownHook(false);

                if(listener != null) {
                    builder.addEventListener(listener);
                } else {
                    log.warn("Starting a shard without an event listener!");
                }

                if (!System.getProperty("os.arch").equalsIgnoreCase("arm")
                        && !System.getProperty("os.arch").equalsIgnoreCase("arm-linux")
                        && !System.getProperty("os.arch").equalsIgnoreCase("darwin")
                        && !System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                    builder.setAudioSendFactory(new NativeAudioSendFactory());
                }
                if (Config.CONFIG.getNumShards() > 1) {
                    builder.useSharding(shardId, Config.CONFIG.getNumShards());
                }
                try {
                    newJda = builder.buildAsync();
                    success = true;
                } catch (RateLimitedException e) {
                    log.warn("Got rate limited while building bot JDA instance! Retrying...", e);
                    Thread.sleep(SHARD_CREATION_SLEEP_INTERVAL);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to start JDA shard " + shardId, e);
        }

        return newJda;
    }

    @Override
    public void revive() {
        log.info("Reviving shard " + shardId);

        try {
            channelsToRejoin.clear();

            PlayerRegistry.getPlayingPlayers().stream().filter(guildPlayer -> guildPlayer.getJda().equals(jda))
                    .forEach(guildPlayer -> channelsToRejoin.add(guildPlayer.getChannel().getId()));
        } catch (Exception ex) {
            log.error("Caught exception while reviving shard " + this, ex);
        }
        
        //remove listeners from decommissioned jda for good memory hygiene
        jda.removeEventListener(shardWatchdogListener);
        jda.removeEventListener(listener);

        jda.shutdown(false);
        jda = buildJDA();
    }

    int getShardId() {
        return shardId;
    }
}
