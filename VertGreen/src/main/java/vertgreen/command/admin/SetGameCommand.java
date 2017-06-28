package vertgreen.command.admin;

import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.util.log.SLF4JInputStreamErrorLogger;
import vertgreen.util.log.SLF4JInputStreamLogger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import vertgreen.VertGreen;
import vertgreen.util.ExitCodes;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import vertgreen.util.BotConstants;

public class SetGameCommand extends Command implements ICommandOwnerRestricted {
    String Games;
    private static final Logger log = LoggerFactory.getLogger(UpdateCommand.class);
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();
        Games = "with " + message.getRawContent().replace("<<setgame ", "");
        jda.getPresence().setGame(Game.of(Games));
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [branch [repo]]\n#Update the bot by checking out the provided branch from the provided github repo and compiling it.";
    }
}
