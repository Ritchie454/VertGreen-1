package vertgreen.command.maintenance;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import vertgreen.VertGreen;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.text.MessageFormat;
import vertgreen.util.GitRepoState;

public class StatsCommand extends Command implements IMaintenanceCommand {
    Long TotMem;
    Long FreeMem;
    Long MaxMem;
    Long CurrMem;
    long totalSecs;
    int days;
    int hours;
    int mins;
    int secs;     
    EmbedBuilder eb;
    
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        getStats(guild);
        getMemory();
        getPlayerInfo(guild);
        getVersion(guild);
        channel.sendMessage(eb.build()).queue();
    }
    
    private void getMemory(){
        TotMem = Runtime.getRuntime().totalMemory() / 1000000;
        FreeMem = Runtime.getRuntime().freeMemory() / 1000000;
        MaxMem = Runtime.getRuntime().maxMemory() / 1000000;
        CurrMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
        eb.addField("<:stafftools:314348604095594498> Memory Stats", "Reserved memory: " + TotMem + "MB\n" + "-> Of which is used: " + CurrMem + "MB\n" + "-> Of which is free: " + FreeMem + "MB\n" + "Max reservable: " + MaxMem + "MB\n", true);
    }
    
    private void getPlayerInfo(Guild guild){
        eb.addField("<:hypesquad:314068430854684672> Shard Info","Sharding: " + VertGreen.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n" + "Players playing: " + PlayerRegistry.getPlayingPlayers().size() + "\n" + "Known servers: " + VertGreen.getAllGuilds().size() + "\n" + "Known users in servers: " + VertGreen.getAllUsersAsMap().size() + "\n" , true);
    }
    
    private void getVersion(Guild guild){         
        eb.addField("<:partner:314068430556758017> Version Info", "Distribution: " + BotConstants.RELEASE + "\n" + "Bot Version:" + BotConstants.VERSION + "\n" + "JDA responses total: " + guild.getJDA().getResponseTotal() + "\n" + "JDA version: " + JDAInfo.VERSION + "\n" + "Lavaplayer verion: " + PlayerLibrary.VERSION + "\n", true);
        GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();
        eb.setFooter("Rev: " + gitRepoState.describe, "https://cdn.discordapp.com/emojis/314068430787706880.png");
    }
    
    private void getStats(Guild guild){
        totalSecs = (System.currentTimeMillis() -VertGreen.START_TIME) / 1000;
        days = (int) (totalSecs / (60 * 60 * 24));
        hours = (int) ((totalSecs / (60 * 60)) % 24);
        mins = (int) ((totalSecs / 60) % 60);
        secs = (int) (totalSecs % 60);       
        String str = MessageFormat.format(
                I18n.get(guild).getString("statsParagraph"),
                days, hours, mins, secs, CommandManager.commandsExecuted - 1)
                + "\n";
        eb.addField("Stats for this bot", (MessageFormat.format(I18n.get(guild).getString("statsRate"), str, (float) (CommandManager.commandsExecuted - 1) / ((float) totalSecs / (float) (60 * 60)))), true);
    }
    
    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
