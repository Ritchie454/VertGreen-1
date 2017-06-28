package vertgreen.db.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_config")
public class UConfig implements IEntity {

    @Id
    private String userId;
    private String bearer;
    private String refresh;
    private long bearerexpiration;

    public String getBearer() {
        return bearer;
    }

    public String getRefresh() {
        return refresh;
    }

    public String getUserId() {
        return userId;
    }

    public long getBearerExpiration() {
        return bearerexpiration;
    }

    public UConfig(String id) {
        this.userId = id;
    }

    @Override
    public void setId(String id) {
        this.userId = id;
    }

    public UConfig() {
    }

    public UConfig setBearer(String bearer) {
        this.bearer = bearer;
        return this;
    }

    public UConfig setRefresh(String refresh) {
        this.refresh = refresh;
        return this;
    }

    public UConfig setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UConfig setBearerExpiration(long bearerExpiration) {
        this.bearerexpiration = bearerExpiration;
        return this;
    }
}