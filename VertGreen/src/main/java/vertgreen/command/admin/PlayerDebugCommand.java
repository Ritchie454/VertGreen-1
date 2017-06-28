package vertgreen.command.admin;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDebugCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        JSONArray a = new JSONArray();
        
        for(GuildPlayer gp : PlayerRegistry.getRegistry().values()){
            JSONObject data = new JSONObject();
            data.put("name", gp.getGuild().getName());
            data.put("id", gp.getGuild().getId());
            data.put("users", gp.getChannel().getMembers().toString());
            data.put("isPlaying", gp.isPlaying());
            data.put("isPaused", gp.isPaused());
            data.put("songCount", gp.getSongCount());
            
            a.put(data);
        }
        
        try {
            channel.sendMessage(TextUtils.postToHastebin(a.toString(), true)).queue();
        } catch (UnirestException ex) {
            Logger.getLogger(PlayerDebugCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show debug information about the music player of this guild.";
    }
}
