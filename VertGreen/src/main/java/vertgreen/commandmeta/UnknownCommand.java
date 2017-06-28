package vertgreen.commandmeta;

import vertgreen.commandmeta.abs.ICommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * @author frederik
 */
public class UnknownCommand implements ICommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {}

    @Override
    public String help(Guild guild) {
        return I18n.get(guild).getString("helpUnknownCommand");
    }
}
