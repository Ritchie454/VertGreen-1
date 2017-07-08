package vertgreen.command.admin;

import java.io.IOException;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.util.ExitCodes;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.LoggerFactory;

public class BotRestartCommand extends Command implements ICommandOwnerRestricted {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BotRestartCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        Runtime rt = Runtime.getRuntime();
        String started;
        try {
            channel.sendMessage("Launching new process...").queue();
            rt.exec("./start.sh");
            started = "true";
        } catch (IOException e) {
            log.warn("Unable to start new process", e);
            channel.sendMessage("```\nWarning!\n Unable to start new process..\n```").queue();
            started = "false";
        }
        if (started.equals("true")){
            channel.sendMessage("Killing old process...").queue();
            VertGreen.shutdown(ExitCodes.EXIT_CODE_RESTART);
        } else if (started.equals("false")){
            channel.sendMessage("Aborted restart process...").queue();
        }

        
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Restarts the bot.";
    }
}
