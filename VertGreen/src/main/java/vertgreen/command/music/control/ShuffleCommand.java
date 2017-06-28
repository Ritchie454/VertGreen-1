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

public class ShuffleCommand extends Command implements IMusicCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildPlayer player = PlayerRegistry.get(guild);
        player.setShuffle(!player.isShuffle());

        if (player.isShuffle()) {
            channel.sendMessage(I18n.get(guild).getString("shuffleOn")).queue();
        } else {
            channel.sendMessage(I18n.get(guild).getString("shuffleOff")).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpShuffleCommand");
    }
}
