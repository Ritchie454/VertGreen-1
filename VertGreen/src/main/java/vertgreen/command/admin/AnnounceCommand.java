package vertgreen.command.admin;

import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandAdminRestricted;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.List;

/**
 *
 * @author frederik
 */
public class AnnounceCommand extends Command implements ICommandAdminRestricted {

    private static final String HEAD = "__**[BROADCASTED MESSAGE]**__\n";

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        List<GuildPlayer> players = PlayerRegistry.getPlayingPlayers();
        String input = message.getRawContent().substring(args[0].length() + 1);
        String msg = input;

        Message status;
        try {
            status = channel.sendMessage("[0/" + players.size() + "]").complete(true);
        } catch (RateLimitedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            int skipped = 0;
            int sent = 0;
            int i = 0;

            for (GuildPlayer player : players) {
                try {
                    player.getActiveTextChannel().sendMessage(msg).complete(true);
                    sent++;
                } catch (PermissionException | RateLimitedException e) {
                    skipped++;
                }

                if (i % 20 == 0) {
                    status.editMessage("[" + sent + "/" + (players.size() - skipped) + "]").queue();
                }

                i++;
            }

            status.editMessage("[" + sent + "/" + (players.size() - skipped) + "]").queue();
        }).start();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Broadcasts an announcement to GuildPlayer TextChannels.";
    }
}