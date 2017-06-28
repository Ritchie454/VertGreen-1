package vertgreen.command.music.control;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.VideoSelection;
import vertgreen.audio.queue.AudioTrackContext;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.text.MessageFormat;

public class SelectCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        select(guild, channel, invoker, message, args);
    }

    static void select(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setCurrentTC(channel);
        if (player.selections.containsKey(invoker.getUser().getId())) {
            VideoSelection selection = player.selections.get(invoker.getUser().getId());
            try {
                int i = Integer.valueOf(args[1]);
                if (selection.getChoices().size() < i || i < 1) {
                    throw new NumberFormatException();
                } else {
                    AudioTrack selected = selection.getChoices().get(i - 1);
                    player.selections.remove(invoker.getUser().getId());
                    String msg = MessageFormat.format(I18n.get(guild).getString("selectSuccess"), i, selected.getInfo().title, TextUtils.formatTime(selected.getInfo().length));
                    channel.editMessageById(selection.getOutMsgId(), msg).complete(true);
                    player.queue(new AudioTrackContext(selected, invoker));
                    player.setPause(false);
                    try {
                        message.delete().queue();
                    } catch (PermissionException ignored) {

                    }
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("selectInterval"), selection.getChoices().size())).queue();
            } catch (RateLimitedException e) {
                throw new RuntimeException(e);
            }
        } else {
            channel.sendMessage(I18n.get(guild).getString("selectSelectionNotGiven")).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} n OR {0}{2} n\n#";
        return usage + I18n.get(guild).getString("helpSelectCommand");
    }
}
