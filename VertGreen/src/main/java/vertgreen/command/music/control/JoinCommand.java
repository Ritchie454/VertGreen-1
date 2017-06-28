package vertgreen.command.music.control;

import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;

public class JoinCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        VoiceChannel vc = player.getUserCurrentVoiceChannel(invoker);
        player.setCurrentTC(channel);
        try {
            player.joinChannel(vc);
            if (vc != null) {
                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("joinJoining"), vc.getName()))
                        .queue();
            }
        } catch (IllegalStateException ex) {
            if(vc != null) {
                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("joinErrorAlreadyJoining"), vc.getName()))
                        .queue();
            } else {
                throw ex;
            }
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpJoinCommand");
    }
}
