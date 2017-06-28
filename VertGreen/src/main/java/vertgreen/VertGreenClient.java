package vertgreen;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertGreenClient extends VertGreen {

    private static final Logger log = LoggerFactory.getLogger(VertGreen.class);

    VertGreenClient () {
        try {
            boolean success = false;
            while (!success) {
                try {
                    jda = new JDABuilder(AccountType.CLIENT)
                            .addEventListener(listenerSelf)
                            .setToken(null)//todo: remove
                            .setEnableShutdownHook(false)
                            .buildAsync();

                    success = true;
                } catch (RateLimitedException e) {
                    log.warn("Got rate limited while building client JDA instance! Retrying...", e);
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            log.error("Failed to start JDA client", e);
        }
    }

    @Override
    public void revive() {
        throw new NotImplementedException("Client shards can't be revived");
    }
}
