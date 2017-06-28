package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class InviteCommand extends Command implements IUtilCommand {
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args)  {
        try {
            String str = "https://discordapp.com/oauth2/authorize?&client_id=" + DiscordUtil.getApplicationInfo(invoker.getJDA().getToken().substring(4)).getString("id") + "&scope=bot";
            String send = MessageFormat.format(I18n.get(guild).getString("invite"),DiscordUtil.getApplicationInfo(message.getJDA().getToken().substring(4)).getString("name"));
            channel.sendMessage(send + "\n" + str).queue();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpInviteCommand");
    }
}
