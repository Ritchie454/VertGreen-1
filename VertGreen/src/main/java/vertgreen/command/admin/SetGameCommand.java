package vertgreen.command.admin;

import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

public class SetGameCommand extends Command implements ICommandOwnerRestricted {
    String Games;
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();
        Games = "with " + message.getRawContent().replace("<<setgame ", "");
        jda.getPresence().setGame(Game.of(Games));
        channel.sendMessage("Set game to: `" + Games + "`").queue();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [branch [repo]]\n#Update the bot by checking out the provided branch from the provided github repo and compiling it.";
    }
}
