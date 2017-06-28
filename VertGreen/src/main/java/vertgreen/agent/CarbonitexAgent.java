package vertgreen.agent;

import com.mashape.unirest.http.Unirest;
import vertgreen.VertGreen;
import org.slf4j.LoggerFactory;

public class CarbonitexAgent extends Thread {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CarbonitexAgent.class);

    private final String key;

    public CarbonitexAgent(String key) {
        super(CarbonitexAgent.class.getSimpleName());
        this.key = key;
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                synchronized (this) {
                    sendStats();
                    sleep(30 * 60 * 1000);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void sendStats() {
        try {
            final String response = Unirest.post("https://www.carbonitex.net/discord/data/botdata.php")
                    .field("key", key)
                    .field("servercount", VertGreen.getAllGuilds().size())
                    .asString().getBody();
            log.info("Successfully posted the bot data to carbonitex.com: " + response);
        } catch (Exception e) {
            log.error("An error occurred while posting the bot data to carbonitex.com", e);
        }
    }

}
