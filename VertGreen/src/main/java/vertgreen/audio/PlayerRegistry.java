package vertgreen.audio;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerRegistry {

    private static final HashMap<String, GuildPlayer> REGISTRY = new HashMap<>();
    public static final float DEFAULT_VOLUME = 1f;

    public static void put(String k, GuildPlayer v) {
        REGISTRY.put(k, v);
    }

    public static GuildPlayer get(Guild guild) {
        return get(guild.getJDA(), guild.getId());
    }

    public static GuildPlayer get(JDA jda, String k) {
        GuildPlayer player = REGISTRY.get(k);
        if (player == null) {
            player = new GuildPlayer(jda.getGuildById(k));
            player.setVolume(DEFAULT_VOLUME);
            REGISTRY.put(k, player);
        }

        // Attempt to set the player as a sending handler. Important after a shard revive
        if (jda.getGuildById(k) != null) {
            jda.getGuildById(k).getAudioManager().setSendingHandler(player);
        }

        return player;
    }

    public static GuildPlayer getExisting(Guild guild) {
        return getExisting(guild.getJDA(), guild.getId());
    }

    public static GuildPlayer getExisting(JDA jda, String k) {
        if (REGISTRY.containsKey(k)) {
            return get(jda, k);
        }
        return null;
    }

    public static GuildPlayer remove(String k) {
        return REGISTRY.remove(k);
    }

    public static HashMap<String, GuildPlayer> getRegistry() {
        return REGISTRY;
    }

    public static List<GuildPlayer> getPlayingPlayers() {
        ArrayList<GuildPlayer> plrs = new ArrayList<>();

        for (GuildPlayer plr : REGISTRY.values()) {
            if (plr.isPlaying()) {
                plrs.add(plr);
            }
        }

        return plrs;
    }

    public static void destroyPlayer(Guild g) {
        destroyPlayer(g.getJDA(), g.getId());
    }

    public static void destroyPlayer(JDA jda, String g) {
        GuildPlayer player = getExisting(jda, g);
        if (player != null) {
            player.destroy();
            remove(g);
        }
    }

}
