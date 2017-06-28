package vertgreen.util;

import net.dv8tion.jda.core.requests.RestAction;

import java.util.concurrent.*;

public class RestActionScheduler {

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    public static void schedule(RestAction action, long time, TimeUnit unit) {
        SCHEDULER.schedule((Runnable) action::queue, time, unit);
    }

}
