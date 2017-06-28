package vertgreen.api;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.audio.PlayerRegistry;
import vertgreen.db.entity.UConfig;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;

public class API {

    private static final Logger log = LoggerFactory.getLogger(API.class);

    private static final int PORT = 1356;

    private API() {}

    public static void start() {
        if(!Config.CONFIG.isRestServerEnabled()) {
            log.warn("Rest server is not enabled. Skipping Spark ignition!");
            return;
        }

        log.info("Igniting Spark API on port: " + PORT);

        Spark.port(PORT);

        Spark.before((request, response) -> {
            log.info(request.requestMethod() + " " + request.pathInfo());
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });

        Spark.get("/stats", (req, res) -> {
            res.type("application/json");

            JSONObject root = new JSONObject();
            JSONArray a = new JSONArray();

            //make a copy to avoid concurrent modification errors
            List<VertGreen> shards = new ArrayList<>(VertGreen.getShards());
            for (VertGreen vg : shards) {
                JSONObject vgStats = new JSONObject();
                vgStats.put("id", vg.getShardInfo().getShardId())
                        .put("guilds", vg.getJda().getGuilds().size())
                        .put("users", vg.getJda().getUsers().size())
                        .put("status", vg.getJda().getStatus());

                a.put(vgStats);
            }

            JSONObject g = new JSONObject();
            g.put("playingPlayers", PlayerRegistry.getPlayingPlayers().size())
                    .put("totalPlayers", PlayerRegistry.getRegistry().size())
                    .put("distribution", Config.CONFIG.getDistribution())
                    .put("guilds", VertGreen.getAllGuilds().size())
                    .put("users", VertGreen.getAllUsersAsMap().size());

            root.put("shards", a);
            root.put("global", g);

            return root;
        });

        Spark.post("/callback", (request, response) -> {
            JSONObject out = new JSONObject();
            JSONObject body = new JSONObject(request.body());

            UConfig uconfig = OAuthManager.handleCallback(body.getString("code"));
            out.put("bearer", uconfig.getBearer())
                    .put("refresh", uconfig.getRefresh())
                    .put("userId", uconfig.getUserId());

            return out;
        });


        /* Exception handling */
        Spark.exception(Exception.class, (e, request, response) -> {
            log.error(request.requestMethod() + " " + request.pathInfo(), e);

            response.body(ExceptionUtils.getStackTrace(e));
            response.type("text/plain");
            response.status(500);
        });
    }

}
