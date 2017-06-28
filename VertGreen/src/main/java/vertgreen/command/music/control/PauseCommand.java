package vertgreen.command.music.control;

import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class PauseCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setCurrentTC(channel);
        if (player.isQueueEmpty()) {
            channel.sendMessage(I18n.get(guild).getString("playQueueEmpty")).queue();
        } else if (player.isPaused()) {
            channel.sendMessage(I18n.get(guild).getString("pauseAlreadyPaused")).queue();
        } else {
            player.pause();
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("pauseSuccess"), Config.CONFIG.getPrefix())).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpPauseCommand");
    }
}
