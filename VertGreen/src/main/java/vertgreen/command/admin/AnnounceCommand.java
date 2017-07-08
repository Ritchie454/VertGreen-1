package vertgreen.command.admin;

import net.dv8tion.jda.core.entities.*;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandAdminRestricted;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.List;

public class AnnounceCommand extends Command implements ICommandAdminRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        List<User> owners = new ArrayList<>();
        String input = message.getRawContent().substring(args[0].length() + 1);
        String msg = input;

        Message status;
        try {
            status = channel.sendMessage("Sending Messages...").complete(true);
        } catch (RateLimitedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            try {
                for (Guild g : VertGreen.getAllGuilds()) {
                    owners.add(g.getOwner().getUser());
                }
                for (User o : owners) {
                    o.openPrivateChannel().complete(true);
                    o.getPrivateChannel().sendMessage(msg).queue();
                }
            } catch (Exception e) {
            }
            status.editMessage("Messages Sent!\nAll known server owners have been notified~").queue();
        }).start();
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Broadcasts an announcement to GuildPlayer TextChannels.";
    }
}