package vertgreen.util;

import com.mashape.unirest.http.Unirest;
import vertgreen.Config;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YoutubeAPI {

    private static final Logger log = LoggerFactory.getLogger(YoutubeAPI.class);

    private YoutubeAPI() {
    }

    private static YoutubeVideo getVideoFromID(String id) {
        JSONObject data = null;
        try {
            data = Unirest.get("https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet&fields=items(id,snippet/title,contentDetails/duration)")
                    .queryString("id", id)
                    .queryString("key", Config.CONFIG.getRandomGoogleKey())
                    .asJson()
                    .getBody()
                    .getObject();

            YoutubeVideo vid = new YoutubeVideo();
            vid.id = data.getJSONArray("items").getJSONObject(0).getString("id");
            vid.name = data.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title");
            vid.duration = data.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration");

            return vid;
        } catch (JSONException ex) {
            System.err.println(data);
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static YoutubeVideo getVideoFromID(String id, boolean verbose) {
        if(verbose){
            JSONObject data = null;
            String gkey = Config.CONFIG.getRandomGoogleKey();
            try {
                data = Unirest.get("https://www.googleapis.com/youtube/v3/videos?part=contentDetails,snippet")
                        .queryString("id", id)
                        .queryString("key", gkey)
                        .asJson()
                        .getBody()
                        .getObject();

                YoutubeVideo vid = new YoutubeVideo();
                vid.id = data.getJSONArray("items").getJSONObject(0).getString("id");
                vid.name = data.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title");
                vid.duration = data.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration");
                vid.description = data.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("description");
                vid.channelId = data.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("channelId");
                vid.channelTitle = data.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("channelTitle");

                return vid;
            } catch (JSONException ex) {
                log.error(data != null ? data.toString() : null);

                log.error("API key used ends with: " + gkey.substring(20));

                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return getVideoFromID(id);
        }
    }

}
