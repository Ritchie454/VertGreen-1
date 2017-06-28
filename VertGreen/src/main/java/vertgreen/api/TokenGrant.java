package vertgreen.api;

public class TokenGrant {

    private final String bearer;
    private final String refresh;
    private final String scope;
    private final long expireEpoch;

    TokenGrant(String bearer, String refresh, String scope, long expireSecs) {
        this.bearer = bearer;
        this.refresh = refresh;
        this.scope = scope;
        this.expireEpoch = (System.currentTimeMillis() / 1000) + expireSecs;
    }

    public String getBearer() {
        return bearer;
    }

    public String getRefresh() {
        return refresh;
    }

    public String getScope() {
        return scope;
    }

    public long getExpirationTime() {
        return expireEpoch;
    }

}