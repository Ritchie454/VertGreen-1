package vertgreen.db.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "blacklist")
public class BlacklistEntry implements IEntity {

    //id of the user or guild that this blacklist entry belongs to
    @Id
    public long id;

    //blacklist level that the user or guild is on
    //this should increase every time progressively
    @Column(name = "level", nullable = false)
    public int level;

    //keeps track of how many times a user or guild reached the rate limit on the current blacklist level
    @Column(name = "rate_limit_reached", nullable = false)
    public int rateLimitReached;

    //when was the ratelimit hit the last time?
    @Column(name = "rate_limit_timestamp", nullable = false)
    @ColumnDefault("0") //tells hibernate ddl how to fill this by default with a zero
    public long rateLimitReachedTimestamp;

    //time when the id was blacklisted
    @Column(name = "blacklisted_timestamp", nullable = false)
    public long blacklistedTimestamp;

    public BlacklistEntry(long id) {
        this.id = id;
        this.level = -1;
        this.rateLimitReached = 0;
        this.blacklistedTimestamp = System.currentTimeMillis();
    }

    @Override
    public void setId(String id) {
        this.id = Long.valueOf(id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    //Boilerplate code below

    public BlacklistEntry() {
    }

    public long getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRateLimitReached() {
        return rateLimitReached;
    }

    public void setRateLimitReached(int rateLimitReached) {
        this.rateLimitReached = rateLimitReached;
    }

    public long getRateLimitReachedTimestamp() {
        return rateLimitReachedTimestamp;
    }

    public void setRateLimitReachedTimestamp(long rateLimitReachedTimestamp) {
        this.rateLimitReachedTimestamp = rateLimitReachedTimestamp;
    }

    public long getBlacklistedTimestamp() {
        return blacklistedTimestamp;
    }

    public void setBlacklistedTimestamp(long blacklistedTimestamp) {
        this.blacklistedTimestamp = blacklistedTimestamp;
    }
}
