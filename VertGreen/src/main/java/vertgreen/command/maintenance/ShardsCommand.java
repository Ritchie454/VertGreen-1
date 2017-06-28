package vertgreen.command.maintenance;

import vertgreen.VertGreen;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

public class ShardsCommand extends Command implements IMaintenanceCommand {
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        getPlayerInfo(guild);
        channel.sendMessage(eb.build()).queue();
    }

    private void getPlayerInfo(Guild guild){
        eb.addField("<:hypesquad:314068430854684672> Shard Info","Sharding: " + VertGreen.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n" + "Players playing: " + PlayerRegistry.getPlayingPlayers().size() + "\n" + "Known servers: " + VertGreen.getAllGuilds().size() + "\n" + "Known users in servers: " + VertGreen.getAllUsersAsMap().size() + "\n" , true);
        if (PlayerRegistry.getPlayingPlayers().isEmpty()){
            eb.setColor(BotConstants.VERTYELLOW);
            eb.setFooter(" | Player currently not playing", "http://www.debscrossstitch.co.uk/ekmps/shops/debscrossstitch/images/bright-blue-square-aperture-card-envelope-4-x-6-a6-bright-blue-square-aperture-4-x-6-5-x-cards-envelopes-1.85-2050-p.jpg");
        }
        else {
            eb.setColor(BotConstants.VERTGREEN);
            eb.setFooter(" | Player currently playing", "http://www.iconsdb.com/icons/preview/royal-blue/play-xxl.png");
        }
    }
    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
