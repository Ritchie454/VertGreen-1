package vertgreen.command.maintenance;

import net.dv8tion.jda.core.JDA;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vertgreen.VertGreen;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

public class StatusCommand extends Command implements IMaintenanceCommand {
    String status;
    String ping;
    String shard;
    String guildn;
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        JDA jda = guild.getJDA();
        eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        status = "Status: " + jda.getStatus() + "\n"; 
        if (invoker.getUser().getId().equals("320192251479195648")){
            ping = "Ping: " + jda.getPing() + "ms\n"; 
            }
        else {
            ping = "Ping:  OWNER RESTRICTED\n";
            }
        shard = "Shard: " + VertGreen.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n"; 
        guildn = "Guild: " + guild.getName() +"\n";
        eb.addField("<:online:313956277808005120> All systems green <:online:313956277808005120>" , status + ping + guildn + shard, true);
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String help(Guild guild) {
        return "Test the bots status";
    }
}
