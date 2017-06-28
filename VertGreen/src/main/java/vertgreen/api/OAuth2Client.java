package vertgreen.api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class OAuth2Client {

    private final String clientId;
    private final String secret;
    private final String tokenUrl;
    private final String redirectUrl;

    OAuth2Client(String clientId, String secret, String tokenUrl, String redirectUrl) {
        this.clientId = clientId;
        this.secret = secret;
        this.tokenUrl = tokenUrl;
        this.redirectUrl = redirectUrl;
    }

    TokenGrant grantToken(String code) throws UnirestException {
        JSONObject json = Unirest.post(tokenUrl)
                .field("code", code)
                .field("client_id", clientId)
                .field("client_secret", secret)
                .field("grant_type", "authorization_code")
                .field("redirect_uri", redirectUrl)
                .asJson().getBody().getObject();

        return new TokenGrant(
                json.getString("access_token"),
                json.getString("refresh_token"),
                json.getString("scope"),
                json.getLong("expires_in")
        );
    }

    public TokenGrant refreshToken(String refresh) throws UnirestException {
        JSONObject json = Unirest.post(tokenUrl)
                .field("refresh_token", refresh)
                .field("client_id", clientId)
                .field("client_secret", secret)
                .field("grant_type", "refresh_token")
                .asJson().getBody().getObject();

        // According to the standard, a new token may optionally be given
        if(json.has("refresh_token")){
            refresh = json.getString("refresh_token");
        }

        return new TokenGrant(
                json.getString("access_token"),
                refresh,
                json.getString("scope"),
                json.getLong("expires_in")
        );
    }
}
