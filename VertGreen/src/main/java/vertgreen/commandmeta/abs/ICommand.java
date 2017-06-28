package vertgreen.commandmeta.abs;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public interface ICommand {

    public abstract void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args);

    /**
     * @param guild Guild where the help is going to be posted to for i18n the help string; null value should be fine for a default language
     * @return an unformatted help string: convention {0} = prefix, {1} = command, fill these in by the running bot, more parameters can be present
     */
    String help(Guild guild);
}
