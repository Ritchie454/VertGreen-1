package vertgreen.command.music.seeking;

import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class RestartCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.getExisting(guild);

        if (player != null && !player.isQueueEmpty()) {
            player.getPlayingTrack().getTrack().setPosition(player.getPlayingTrack().getStartPosition());
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("restartSuccess"), player.getPlayingTrack().getEffectiveTitle())).queue();
        } else {
            TextUtils.replyWithName(channel, invoker, I18n.get(guild).getString("queueEmpty"));
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpRestartCommand");
    }
}
