package vertgreen.command.music.seeking;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.audio.queue.AudioTrackContext;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

public class RewindCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.getExisting(guild);

        if(player == null || player.isQueueEmpty()) {
            TextUtils.replyWithName(channel, invoker, I18n.get(guild).getString("queueEmpty"));
            return;
        }

        if(args.length == 1) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        long t;
        try {
            t = TextUtils.parseTimeString(args[1]);
        } catch (IllegalStateException e){
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        AudioTrackContext atc = player.getPlayingTrack();
        AudioTrack at = atc.getTrack();

        //Ensure bounds
        t = Math.max(0, t);
        t = Math.min(atc.getEffectivePosition(), t);

        at.setPosition(at.getPosition() - t);
        channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("rewSuccess"), player.getPlayingTrack().getEffectiveTitle(), TextUtils.formatTime(t))).queue();
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} [[hh:]mm:]ss\n#";
        String example = " {0}{1} 30";
        return usage + I18n.get(guild).getString("helpRewindCommand") + example;
    }
}
