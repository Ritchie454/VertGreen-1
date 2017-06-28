package vertgreen.command.admin;

import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.util.ExitCodes;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author frederik
 */
public class ExitCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendMessage(TextUtils.prefaceWithName(invoker, "Shutting down..")).queue();
        VertGreen.shutdown(ExitCodes.EXIT_CODE_NORMAL);
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Shut down the bot.";
    }
}