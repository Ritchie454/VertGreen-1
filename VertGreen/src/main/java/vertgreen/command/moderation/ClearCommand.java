package vertgreen.command.moderation;

import vertgreen.commandmeta.MessagingException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IModerationCommand;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.List;

public class ClearCommand extends Command implements IModerationCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();

        if (!invoker.hasPermission(channel, Permission.MESSAGE_MANAGE) && !DiscordUtil.isUserBotOwner(invoker.getUser())) {
            TextUtils.replyWithName(channel, invoker, " You must have Manage Messages to do that!");
            return;
        }
        
        MessageHistory history = new MessageHistory(channel);
        List<Message> msgs;
        try {
            msgs = history.retrievePast(50).complete(true);

            ArrayList<Message> myMessages = new ArrayList<>();

            for (Message msg : msgs) {
                if(msg.getAuthor().equals(jda.getSelfUser())){
                    myMessages.add(msg);
                }
            }

            if(myMessages.isEmpty()){
                throw new MessagingException("No messages found.");
            } else if(myMessages.size() == 1) {
                myMessages.get(0).delete().complete(true);
                channel.sendMessage("Deleted one message.").queue();
            } else {

                if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
                    throw new MessagingException("I must have the `Manage Messages` permission to delete my own messages in bulk.");
                }

                channel.deleteMessages(myMessages).complete(true);
                channel.sendMessage("Deleted **" + myMessages.size() + "** messages.").queue();
            }
        } catch (RateLimitedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpClearCommand");
    }
}
