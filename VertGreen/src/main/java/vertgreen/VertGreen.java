package vertgreen;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.agent.CarbonitexAgent;
import vertgreen.agent.DBConnectionWatchdogAgent;
import vertgreen.agent.ShardWatchdogAgent;
import vertgreen.api.API;
import vertgreen.api.OAuthManager;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.MusicPersistenceHandler;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.commandmeta.init.MainCommandInitializer;
import vertgreen.commandmeta.init.MusicCommandInitializer;
import vertgreen.db.DatabaseManager;
import vertgreen.event.EventListenerGreen;
import vertgreen.event.EventListenerVert;
import vertgreen.event.ShardWatchdogListener;
import vertgreen.feature.I18n;
import vertgreen.util.DistributionEnum;
import vertgreen.util.log.SimpleLogToSLF4JAdapter;
import frederikam.jca.JCA;
import frederikam.jca.JCABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import vertgreen.util.BotConstants;

public abstract class VertGreen {

    private static final Logger log = LoggerFactory.getLogger(VertGreen.class);
    
    static final int SHARD_CREATION_SLEEP_INTERVAL = 5100;

    private static final ArrayList<VertGreen> shards = new ArrayList<>();
    public static JCA jca;
    public static final long START_TIME = System.currentTimeMillis();
    public static final int UNKNOWN_SHUTDOWN_CODE = -991023;
    public static int shutdownCode = UNKNOWN_SHUTDOWN_CODE;//Used when specifying the intended code for shutdown hooks
    static EventListenerGreen listenerBot;
    static EventListenerVert listenerSelf;
    ShardWatchdogListener shardWatchdogListener = null;
    private static AtomicInteger numShardsReady = new AtomicInteger(0);

    //For when we need to join a revived shard with it's old GuildPlayers
    final ArrayList<String> channelsToRejoin = new ArrayList<>();

    //unlimited threads = http://i.imgur.com/H3b7H1S.gif
    //use this executor for various small async tasks
    public final static ExecutorService executor = Executors.newCachedThreadPool();

    JDA jda;
    private static VertGreenClient vgClient;

    private static ShardWatchdogAgent shardWatchdogAgent;
    private static DBConnectionWatchdogAgent dbConnectionWatchdogAgent;

    private static DatabaseManager dbManager;
    private boolean hasReadiedOnce = false;

    public static void main(String[] args) throws LoginException, IllegalArgumentException, InterruptedException, IOException, UnirestException {
        Runtime.getRuntime().addShutdownHook(new Thread(ON_SHUTDOWN, "Vert main shutdownhook"));
        
        log.info("\n\n" +
                "__       __           _      ______\n" +
                "\\ \\     / /          | |    / _____\\\n" +
                " \\ \\   / / ___  _ __ | |_  / /   ___  _ __   ___   ___  _ ___\n" +
                "  \\ \\ / / / _ \\| '__|| __|| |   |__ || '__| / _ \\ / _ \\| '_  |\n" +
                "   \\ v / |  __/| |   | |_  \\ \\____/ /| |   |  __/|  __/| | | |\n" +
                "    \\_/   \\___||_|    \\__|  \\______/ |_|    \\___| \\___||_| |_|\n\n" +
                "Distribution: " + BotConstants.RELEASE + "\n" +
                "Version: " + BotConstants.VERSION + "\n");

        I18n.start();

        //Attach log adapter
        SimpleLog.addListener(new SimpleLogToSLF4JAdapter());

        //Make JDA not print to console, we have Logback for that
        SimpleLog.LEVEL = SimpleLog.Level.OFF;

        int scope;
        try {
            scope = Integer.parseInt(args[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            log.warn("Invalid scope, defaulting to scopes 0x111");
            scope = 0x111;
        }

        log.info("Starting with scopes:"
                + "\n\tMain: " + ((scope & 0x100) == 0x100)
                + "\n\tMusic: " + ((scope & 0x010) == 0x010)
                + "\n\tSelf: " + ((scope & 0x001) == 0x001));

        log.info("JDA version:\t" + JDAInfo.VERSION);

        Config.loadDefaultConfig(scope);

        try {
            API.start();
        } catch (Exception e) {
            log.warn("Failed to ignite Spark, API unavailable", e);
        }

        if (!Config.CONFIG.getJdbcUrl().equals("")) {
            dbManager = new DatabaseManager(Config.CONFIG.getJdbcUrl(), null, Config.CONFIG.getHikariPoolSize());
            dbManager.startup();
            dbConnectionWatchdogAgent = new DBConnectionWatchdogAgent(dbManager);
            dbConnectionWatchdogAgent.start();
        } else if (Config.CONFIG.getNumShards() > 2) {
            log.warn("No JDBC URL and more than 2 shard found! Initializing the SQLi DB is potentially dangerous too. Skipping...");
        } else {
            log.warn("No JDBC URL found, skipped database connection, falling back to internal SQLite db.");
            dbManager = new DatabaseManager("jdbc:sqlite:vertgreen.db", "org.hibernate.dialect.SQLiteDialect",
                    Config.CONFIG.getHikariPoolSize());
            dbManager.startup();
        }


        try {
            if (!Config.CONFIG.getOauthSecret().equals("")) {
                OAuthManager.start(Config.CONFIG.getBotToken(), Config.CONFIG.getOauthSecret());
            } else {
                log.warn("No oauth secret found, skipped initialization of OAuth2 client");
            }
        } catch (Exception e) {
            log.warn("Failed to start OAuth2 client", e);
        }

        //Initialise event listeners
        listenerBot = new EventListenerGreen();
        listenerSelf = new EventListenerVert();

        //Commands
        if(Config.CONFIG.getDistribution() == DistributionEnum.DEVELOPMENT
                || Config.CONFIG.getDistribution() == DistributionEnum.MAIN)
            MainCommandInitializer.initCommands();

        if(Config.CONFIG.getDistribution() == DistributionEnum.DEVELOPMENT
                || Config.CONFIG.getDistribution() == DistributionEnum.MUSIC
                || Config.CONFIG.getDistribution() == DistributionEnum.PATRON)
            MusicCommandInitializer.initCommands();

        log.info("Loaded commands, registry size is " + CommandRegistry.getSize());

        //Check MAL creds
        executor.submit(VertGreen::hasValidMALLogin);

        //Check imgur creds
        executor.submit(VertGreen::hasValidImgurCredentials);

        //Initialise JCA
        executor.submit(VertGreen::loadJCA);

        /* Init JDA */

        if ((Config.CONFIG.getScope() & 0x110) != 0) {
            initBotShards(listenerBot);
        }

        if ((Config.CONFIG.getScope() & 0x001) != 0) {
            log.warn("Selfbot support has been removed.");
            //fbClient = new VertGreenClient();
        }

        if (Config.CONFIG.getDistribution() == DistributionEnum.MUSIC && Config.CONFIG.getCarbonKey() != null) {
            CarbonitexAgent carbonitexAgent = new CarbonitexAgent(Config.CONFIG.getCarbonKey());
            carbonitexAgent.setDaemon(true);
            carbonitexAgent.start();
        }

        shardWatchdogAgent = new ShardWatchdogAgent();
        shardWatchdogAgent.setDaemon(true);
        shardWatchdogAgent.start();
    }

    private static boolean loadJCA() {
        boolean result = true;
        try {
            if (!Config.CONFIG.getCbUser().equals("") && !Config.CONFIG.getCbKey().equals("")) {
                log.info("Starting CleverBot");
                jca = new JCABuilder().setKey(Config.CONFIG.getCbKey()).setUser(Config.CONFIG.getCbUser()).buildBlocking();
            } else {
                log.warn("Credentials not found for cleverbot authentication. Skipping...");
                result = false;
            }
        } catch (Exception e) {
            log.warn("Error when starting JCA", e);
            result = false;
        }
        return result;
    }

    private static boolean hasValidMALLogin() {
        if ("".equals(Config.CONFIG.getMalUser()) || "".equals(Config.CONFIG.getMalPassword())) {
            log.warn("MAL credentials not found. MAL related commands will not be available.");
            return false;
        }
        try {
            HttpResponse<String> response = Unirest.get("https://myanimelist.net/api/account/verify_credentials.xml")
                    .basicAuth(Config.CONFIG.getMalUser(), Config.CONFIG.getMalPassword())
                    .asString();
            int responseStatus = response.getStatus();
            if (responseStatus == 200) {
                log.info("MAL login successful");
                return true;
            } else {
                log.warn("MAL login failed with " + responseStatus + ": " + response.getBody());
            }
        } catch (UnirestException e) {
            log.warn("MAL login failed, it seems to be down.", e);
        }
        return false;
    }

    private static boolean hasValidImgurCredentials() {
        if ("".equals(Config.CONFIG.getImgurClientId())) {
            log.warn("Imgur credentials not found. Commands relying on Imgur will not work properly.");
            return false;
        }
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://api.imgur.com/3/credits")
                    .header("Authorization", "Client-ID " + Config.CONFIG.getImgurClientId())
                    .asJson();
            int responseStatus = response.getStatus();


            if (responseStatus == 200) {
                JSONObject data = response.getBody().getObject().getJSONObject("data");
                //https://api.imgur.com/#limits
                //at the time of the introduction of this code imgur offers daily 12500 and hourly 500 GET requests for open source software
                //hitting the daily limit 5 times in a month will blacklist the app for the rest of the month
                //we use 3 requests per hour (and per restart of the bot), so there should be no problems with imgur's rate limit
                int hourlyLimit = data.getInt("UserLimit");
                int hourlyLeft = data.getInt("UserRemaining");
                long seconds = data.getLong("UserReset") - (System.currentTimeMillis() / 1000);
                String timeTillReset = String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
                int dailyLimit = data.getInt("ClientLimit");
                int dailyLeft = data.getInt("ClientRemaining");
                log.info("Imgur credentials are valid. " + hourlyLeft + "/" + hourlyLimit +
                        " requests remaining this hour, resetting in " + timeTillReset + ", " +
                        dailyLeft + "/" + dailyLimit + " requests remaining today.");
                return true;
            } else {
                log.warn("Imgur login failed with " + responseStatus + ": " + response.getBody());
            }
        } catch (UnirestException e) {
            log.warn("Imgur login failed, it seems to be down.", e);
        }
        return false;
    }

    private static void initBotShards(EventListener listener) {
        for(int i = 0; i < Config.CONFIG.getNumShards(); i++){
            try {
                shards.add(i, new VertGreenBot(i, listener));
            } catch (Exception e) {
                log.error("Caught an exception while starting shard " + i + "!", e);
                numShardsReady.getAndIncrement();
            }
            try {
                Thread.sleep(SHARD_CREATION_SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException("Got interrupted while setting up bot shards!", e);
            }
        }

        log.info(shards.size() + " shards have been constructed");

    }

    public void onInit(ReadyEvent readyEvent) {
        if (!hasReadiedOnce) {
            numShardsReady.incrementAndGet();
            hasReadiedOnce = false;
        }

        log.info("Received ready event for " + VertGreen.getInstance(readyEvent.getJDA()).getShardInfo().getShardString());
        getTextChannelById("330067321148145675").sendMessage(":rocket: Received ready event for " + VertGreen.getInstance(readyEvent.getJDA()).getShardInfo().getShardString()).queue();
        int ready = numShardsReady.get();
        if (ready == Config.CONFIG.getNumShards()) {
            log.info("All " + ready + " shards are ready.");
            MusicPersistenceHandler.reloadPlaylists();
        }

        //Rejoin old channels if revived
        channelsToRejoin.forEach(vcid -> {
            VoiceChannel channel = jda.getVoiceChannelById(vcid);
            if(channel == null) return;
            GuildPlayer player = PlayerRegistry.get(channel.getGuild());
            if(player == null) return;

            AudioManager am = channel.getGuild().getAudioManager();
            am.openAudioConnection(channel);
            am.setSendingHandler(player);
        });

        channelsToRejoin.clear();
    }

    //Shutdown hook
    private static final Runnable ON_SHUTDOWN = () -> {
        int code = shutdownCode != UNKNOWN_SHUTDOWN_CODE ? shutdownCode : -1;

        shardWatchdogAgent.shutdown();
        if (dbConnectionWatchdogAgent != null) dbConnectionWatchdogAgent.shutdown();

        try {
            MusicPersistenceHandler.handlePreShutdown(code);
        } catch (Exception e) {
            log.error("Critical error while handling music persistence.", e);
        }

        for(VertGreen vg : shards) {
            vg.getJda().shutdown(false);
        }

        try {
            Unirest.shutdown();
        } catch (IOException ignored) {}

        executor.shutdown();
        dbManager.shutdown();
    };

    public static void shutdown(int code) {
        log.info("Shutting down with exit code " + code);
        shutdownCode = code;

        System.exit(code);
    }

    public static EventListenerGreen getListenerBot() {
        return listenerBot;
    }

    public static EventListenerVert getListenerSelf() {
        return listenerSelf;
    }

    /* Sharding */

    public JDA getJda() {
        return jda;
    }

    public static List<VertGreen> getShards() {
        return shards;
    }

    public static List<Guild> getAllGuilds() {
        ArrayList<Guild> list = new ArrayList<>();

        for (VertGreen vg : shards) {
            list.addAll(vg.getJda().getGuilds());
        }

        return list;
    }

    public static Map<String, User> getAllUsersAsMap() {
        HashMap<String, User> map = new HashMap<>();

        for (VertGreen vg : shards) {
            for (User usr : vg.getJda().getUsers()) {
                map.put(usr.getId(), usr);
            }
        }

        return map;
    }

    public static TextChannel getTextChannelById(String id) {
        for (VertGreen vg : shards) {
            for (TextChannel channel : vg.getJda().getTextChannels()) {
                if(channel.getId().equals(id)) return channel;
            }
        }

        return null;
    }

    public static VoiceChannel getVoiceChannelById(String id) {
        for (VertGreen vg : shards) {
            for (VoiceChannel channel : vg.getJda().getVoiceChannels()) {
                if(channel.getId().equals(id)) return channel;
            }
        }

        return null;
    }

    public static VertGreenClient getClient() {
        return vgClient;
    }

    public static VertGreen getInstance(JDA jda) {
        if(jda.getAccountType() == AccountType.CLIENT) {
            return vgClient;
        } else {
            int sId = jda.getShardInfo() == null ? 0 : jda.getShardInfo().getShardId();

            for(VertGreen vg : shards) {
                if(((VertGreenBot) vg).getShardId() == sId) {
                    return vg;
                }
            }
        }

        throw new IllegalStateException("Attempted to get instance for JDA shard that is not indexed");
    }

    public static VertGreen getInstance(int id) {
        return shards.get(id);
    }

    public static JDA getFirstJDA(){
        return shards.get(0).getJda();
    }

    public ShardInfo getShardInfo() {
        int sId = jda.getShardInfo() == null ? 0 : jda.getShardInfo().getShardId();

        if(jda.getAccountType() == AccountType.CLIENT) {
            return new ShardInfo(0, 1);
        } else {
            return new ShardInfo(sId, Config.CONFIG.getNumShards());
        }
    }

    public abstract void revive();

    public ShardWatchdogListener getShardWatchdogListener() {
        return shardWatchdogListener;
    }

    @SuppressWarnings("WeakerAccess")
    public class ShardInfo {

        int shardId;
        int shardTotal;

        ShardInfo(int shardId, int shardTotal) {
            this.shardId = shardId;
            this.shardTotal = shardTotal;
        }

        public int getShardId() {
            return this.shardId;
        }

        public int getShardTotal() {
            return this.shardTotal;
        }

        public String getShardString() {
            return String.format("[%02d / %02d]", this.shardId, this.shardTotal);
        }

        @Override
        public String toString() {
            return getShardString();
        }
    }

    public static DatabaseManager getDbManager() {
        return dbManager;
    }
}
