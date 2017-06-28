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

public class UnpauseCommand extends Command implements IMusicCommand {

    private static final JoinCommand JOIN_COMMAND = new JoinCommand();

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setCurrentTC(channel);
        if (player.isQueueEmpty()) {
            channel.sendMessage(I18n.get(guild).getString("unpauseQueueEmpty")).queue();
        } else if (!player.isPaused()) {
            channel.sendMessage(I18n.get(guild).getString("unpausePlayerNotPaused")).queue();
        } else if (player.getHumanUsersInVC().isEmpty() && player.isPaused() && guild.getAudioManager().isConnected()) {
            channel.sendMessage(I18n.get(guild).getString("unpauseNoUsers")).queue();
        } else if(!guild.getAudioManager().isConnected()) {
            // When we just want to continue playing, but the user is not in a VC
            JOIN_COMMAND.onInvoke(guild, channel, invoker, message, new String[0]);
            if(guild.getAudioManager().isConnected() || guild.getAudioManager().isAttemptingToConnect()) {
                player.play();
                channel.sendMessage(I18n.get(guild).getString("unpauseSuccess")).queue();
            }
        } else {
            player.play();
            channel.sendMessage(I18n.get(guild).getString("unpauseSuccess")).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpUnpauseCommand");
    }
}
