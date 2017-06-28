package vertgreen.command.maintenance;

import vertgreen.VertGreen;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;
import java.text.MessageFormat;

public class MemoryCommand extends Command implements IMaintenanceCommand {
    Long TotMem;
    Long FreeMem;
    Long MaxMem;
    Long CurrMem;
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {        
        eb = new EmbedBuilder();
        getMemory();
        testMemory();
        channel.sendMessage(eb.build()).queue();
    }
    
    
    private void getMemory(){
        TotMem = Runtime.getRuntime().totalMemory() / 1000000;
        FreeMem = Runtime.getRuntime().freeMemory() / 1000000;
        MaxMem = Runtime.getRuntime().maxMemory() / 1000000;
        CurrMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
        eb.addField("<:stafftools:314348604095594498> Memory Stats", "Reserved memory: " + TotMem + "MB\n" + "-> Of which is used: " + CurrMem + "MB\n" + "-> Of which is free: " + FreeMem + "MB\n" + "Max reservable: " + MaxMem + "MB\n", true);
    }
    
    private void testMemory(){
        if (CurrMem > 750) {
            eb.setFooter("Warning, High memory usage!", "https://cdn.discordapp.com/emojis/313956276893646850.png");
            eb.setColor(BotConstants.VERTRED);
        } else if (CurrMem > 500) {
            eb.setFooter("Moderate memory usage", "https://cdn.discordapp.com/emojis/313956277220802560.png");
            eb.setColor(BotConstants.VERTYELLOW);
        } else {
            eb.setFooter("Low memory usage", "https://cdn.discordapp.com/emojis/313956277808005120.png");
            eb.setColor(BotConstants.VERTGREEN);
        }
    }
    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
