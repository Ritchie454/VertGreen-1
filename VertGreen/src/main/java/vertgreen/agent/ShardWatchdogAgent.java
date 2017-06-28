package vertgreen.agent;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.event.ShardWatchdogListener;
import vertgreen.util.DistributionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShardWatchdogAgent extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ShardWatchdogAgent.class);
    private static final int INTERVAL_MILLIS = 10000; // 10 secs
    private static final int ACCEPTABLE_SILENCE = getAcceptableSilenceThreshold();

    private boolean shutdown = false;

    public ShardWatchdogAgent() {
        super(ShardWatchdogAgent.class.getSimpleName());
    }

    @Override
    public void run() {
        log.info("Started shard watchdog");

        //noinspection InfiniteLoopStatement
        while (!shutdown) {
            try {
                inspect();
                sleep(INTERVAL_MILLIS);
            } catch (Exception e) {
                log.error("Caught an exception while trying kill dead shards!", e);
                try {
                    sleep(1000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }

    private void inspect() throws InterruptedException {
        List<VertGreen> shards = VertGreen.getShards();

        for(VertGreen shard : shards) {
            if (shutdown) break;
            ShardWatchdogListener listener = shard.getShardWatchdogListener();

            long diff = System.currentTimeMillis() - listener.getLastEventTime();

            if(diff > ACCEPTABLE_SILENCE) {
                if (listener.getEventCount() < 100) {
                    log.warn("Did not revive shard " + shard.getShardInfo() + " because it did not receive enough events since construction!");
                } else {
                    log.warn("Reviving shard " + shard.getShardInfo() + " after " + (diff / 1000) +
                            " seconds of no events. Last event received was " + listener.getLastEvent());
                    shard.revive();
                    sleep(5000);
                }
            }
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    private static int getAcceptableSilenceThreshold() {
        if(Config.CONFIG.getDistribution() == DistributionEnum.DEVELOPMENT) {
            return Integer.MAX_VALUE;
        }

        return Config.CONFIG.getNumShards() != 1 ? 30 * 1000 : 600 * 1000; //30 seconds or 10 minutes depending on shard count
    }
}
