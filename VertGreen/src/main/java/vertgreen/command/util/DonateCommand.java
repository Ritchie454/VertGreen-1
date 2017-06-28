package vertgreen.command.util;

import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IUtilCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;


public class DonateCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        channel.sendMessage("You can help fund the bot and donate on patron using this link!\nhttps://www.patreon.com/VertGreen").queue();
    }
    @Override
    public String help(Guild guild) {
        String usage = "<<donate";
        return usage;
    }
}
