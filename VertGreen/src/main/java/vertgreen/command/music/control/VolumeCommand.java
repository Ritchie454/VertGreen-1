package vertgreen.command.music.control;

import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.MessagingException;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.feature.I18n;
import vertgreen.util.RestActionScheduler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class VolumeCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {

        if(Config.CONFIG.getDistribution().volumeSupported()) {

            GuildPlayer player = PlayerRegistry.get(guild);
            try {
                float volume = Float.parseFloat(args[1]) / 100;
                volume = Math.max(0, Math.min(1.5f, volume));

                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("volumeSuccess"), Math.floor(player.getVolume() * 100), Math.floor(volume * 100))).queue();

                player.setVolume(volume);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                throw new MessagingException(MessageFormat.format(I18n.get(guild).getString("volumeSyntax"), 100 * PlayerRegistry.DEFAULT_VOLUME, Math.floor(player.getVolume() * 100)));
            }
        } else {
            channel.sendMessage(I18n.get(guild).getString("volumeApology")).queue(message1 -> RestActionScheduler.schedule(
                            message1.delete(),
                            2,
                            TimeUnit.MINUTES
                    ));
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} <0-150>\n#";
        return usage + I18n.get(guild).getString("helpVolumeCommand");
    }
}
