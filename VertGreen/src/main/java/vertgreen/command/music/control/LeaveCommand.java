package vertgreen.command.music.control;

import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaveCommand extends Command implements IMusicCommand {

    private static final Logger log = LoggerFactory.getLogger(LeaveCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        try {
            GuildPlayer player = PlayerRegistry.get(guild);
            player.setCurrentTC(channel);
            player.leaveVoiceChannelRequest(channel, false);
        } catch (Exception e) {
            log.error("Something caused us to not properly leave a voice channel!", e);
            guild.getAudioManager().closeAudioConnection();
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpLeaveCommand");
    }
}
