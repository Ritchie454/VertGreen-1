package vertgreen.db.entity;

import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "guild_config")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="guild_config")
public class GuildConfig implements IEntity, Serializable {

    @Id
    private String guildId;

    @Column(name = "track_announce", nullable = false)
    private boolean trackAnnounce = false;

    @Column(name = "auto_resume", nullable = false)
    private boolean autoResume = false;

    @Column(name = "lang", nullable = false)
    private String lang = "en_US";

    public GuildConfig(String id) {
        this.guildId = id;
    }

    @Override
    public void setId(String id) {
        this.guildId = id;
    }

    public GuildConfig() {
    }

    public String getGuildId() {
        return guildId;
    }

    public boolean isTrackAnnounce() {
        return trackAnnounce;
    }

    public void setTrackAnnounce(boolean trackAnnounce) {
        this.trackAnnounce = trackAnnounce;
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoplay) {
        this.autoResume = autoplay;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    /*@OneToMany
    @JoinColumn(name = "guildconfig")
    private Set<TCConfig> textChannels;

    public GuildConfig(Guild guild) {
        this.guildId = Long.parseLong(guild.getId());

        textChannels = new CopyOnWriteArraySet<>();

        for (TextChannel tc : guild.getTextChannels()) {
            TCConfig tcc = new TCConfig(this, tc);
            textChannels.add(tcc);
        }

        for (TCConfig tcc : textChannels) {
            DatabaseManager.getEntityManager().persist(tcc);
        }
    }

    public Set<TCConfig> getTextChannels() {
        return textChannels;
    }

    public void addTextChannel(TCConfig tcc){
        textChannels.add(tcc);
    }

    public void removeTextChannel(TCConfig tcc){
        textChannels.remove(tcc);
    }

    public long getGuildId() {
        return guildId;
    }
    */

}
