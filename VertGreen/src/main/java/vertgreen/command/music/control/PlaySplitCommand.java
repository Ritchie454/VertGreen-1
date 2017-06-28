package vertgreen.command.music.control;

import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.IdentifierContext;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class PlaySplitCommand extends Command implements IMusicCommand {


    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if (args.length < 2) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        IdentifierContext ic = new IdentifierContext(args[1], channel, invoker);
        ic.setSplit(true);

        GuildPlayer player = PlayerRegistry.get(guild);
        player.queue(ic);
        player.setPause(false);

        try {
            message.delete().queue();
        } catch (Exception ignored) {

        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} <url>\n#";
        return usage + I18n.get(guild).getString("helpPlaySplitCommand");
    }
}
