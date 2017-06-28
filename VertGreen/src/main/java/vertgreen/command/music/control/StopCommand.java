package vertgreen.command.music.control;

import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.AudioTrackContext;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.text.MessageFormat;
import java.util.List;

public class StopCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setCurrentTC(channel);
        List<AudioTrackContext> tracks = player.getRemainingTracks();

        Pair<Boolean, String> pair = player.canMemberSkipTracks(channel, invoker, tracks);
        //skipping allowed
        if(pair.getLeft()) {
            player.stop();
            switch (tracks.size()) {
                case 0:
                    channel.sendMessage(I18n.get(guild).getString("stopAlreadyEmpty")).queue();
                    break;
                case 1:
                    channel.sendMessage(I18n.get(guild).getString("stopEmptyOne")).queue();
                    break;
                default:
                    channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("stopEmptySeveral"), tracks.size())).queue();
                    break;
            }
            player.leaveVoiceChannelRequest(channel, true);
        } else {
            //invoker is not allowed to skip all doz track
            TextUtils.replyWithName(channel, invoker, pair.getRight());
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpStopCommand");
    }
}
