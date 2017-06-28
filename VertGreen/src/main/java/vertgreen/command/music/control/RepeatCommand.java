package vertgreen.command.music.control;

import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.RepeatMode;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class RepeatCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);

        if (args.length < 2) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        RepeatMode desiredRepeatMode;
        String userInput = args[1];
        switch (userInput) {
            case "off":
            case "out":
                desiredRepeatMode = RepeatMode.OFF;
                break;
            case "single":
            case "one":
            case "track":
                desiredRepeatMode = RepeatMode.SINGLE;
                break;
            case "all":
            case "list":
            case "queue":
                desiredRepeatMode = RepeatMode.ALL;
                break;
            case "help":
            default:
                String command = args[0].substring(Config.CONFIG.getPrefix().length());
                HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
                return;
        }

        player.setRepeatMode(desiredRepeatMode);

        switch (desiredRepeatMode) {
            case OFF:
                channel.sendMessage(I18n.get(guild).getString("repeatOff")).queue();
                break;
            case SINGLE:
                channel.sendMessage(I18n.get(guild).getString("repeatOnSingle")).queue();
                break;
            case ALL:
                channel.sendMessage(I18n.get(guild).getString("repeatOnAll")).queue();
                break;
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} single|all|off\n#";
        return usage + I18n.get(guild).getString("helpRepeatCommand");
    }
}
