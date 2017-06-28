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
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.LoggerFactory;

public class BotRestartCommand extends Command implements ICommandOwnerRestricted {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BotRestartCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            channel.sendMessage(TextUtils.prefaceWithName(invoker, "Restarting..")).complete(true);
        } catch (RateLimitedException e) {
            log.warn("Rate limited", e);
        }

        VertGreen.shutdown(ExitCodes.EXIT_CODE_RESTART);
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Restarts the bot.";
    }
}
