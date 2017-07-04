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
import net.dv8tion.jda.core.EmbedBuilder;

public class InviteCommand extends Command implements IUtilCommand {
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args)  {
        EmbedBuilder eb = new EmbedBuilder();
        try {
            eb.setAuthor("Invite Link for " + DiscordUtil.getApplicationInfo(message.getJDA().getToken().substring(4)).getString("name"), "https://kurozu.me/VertGreen", message.getJDA().getSelfUser().getAvatarUrl());
            eb.addField("Invite the bot", "https://kurozu.me/VertGreen", true);
            eb.addField("Join the Support server", "https://kurozu.me/VertSupport", true);
            channel.sendMessage(eb.build()).queue();
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
