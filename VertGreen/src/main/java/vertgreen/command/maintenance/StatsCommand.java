/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package vertgreen.command.maintenance;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.audio.PlayerRegistry;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import vertgreen.util.GitRepoState;
import vertgreen.util.constant.BotConstants;
import net.dv8tion.jda.core.JDAInfo;

import java.awt.*;
import java.text.MessageFormat;

public class StatsCommand extends Command implements IMaintenanceCommand {
    long totalSecs;
    int days;
    int hours;
    int mins;
    int secs;
    EmbedBuilder eb;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        String msg = message.getContent().replace(Config.CONFIG.getPrefix() + "stats", "");
        eb = new EmbedBuilder();
        switch (msg){
            case "":
                getStats(guild);
                getMemory();
                getShardInfo(guild);
                getPing(guild);
                getVersion(guild);
                break;
            case ".memory":
                getMemory();
                break;
            case ".version":
                getVersion(guild);
                break;
            case ".connection":
                getPing(guild);
                break;
            case ".shards":
                getShardInfo(guild);
                break;
            default:
                eb.setTitle("Invalid syntax!");
                eb.setColor(Color.RED);
                break;
        }
        channel.sendMessage(eb.build()).queue();
    }

    private void getStats(Guild guild){
        totalSecs = (System.currentTimeMillis() -VertGreen.START_TIME) / 1000;
        days = (int) (totalSecs / (60 * 60 * 24));
        hours = (int) ((totalSecs / (60 * 60)) % 24);
        mins = (int) ((totalSecs / 60) % 60);
        secs = (int) (totalSecs % 60);
        String str = MessageFormat.format(
                I18n.get(guild).getString("statsParagraph"),
                days, hours, mins, secs, CommandManager.commandsExecuted.get() - 1)
                + "\n";
        eb.addField("Stats for this bot", (MessageFormat.format(I18n.get(guild).getString("statsRate"), str, (float) (CommandManager.commandsExecuted.get() - 1) / ((float) totalSecs / (float) (60 * 60)))), true);
    }

    @Override
    public String help(Guild guild) {
        return ("{0}{1} <Stat Type>\n#Show some statistics about this bot.\n```\nSpecific Details can be viewed using the following syntax:\n```Markdown\n"
                + "[.memory]:­\n#Show memory information\n"
                + "[.version]:­\n#Show version information\n"
                + "[.connection]:­\n#Show connection information\n"
                + "[.shards]:­\n#Show shard information\n"
                + "\nOr just do {0}{1} for generic information");
    }


    public void getVersion(Guild guild){
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        eb.addField("<:partner:314068430556758017> Version Info", "JDA responses total: " + guild.getJDA().getResponseTotal() + "\n" + "JDA version: " + JDAInfo.VERSION + "\n" + "Lavaplayer version: " + PlayerLibrary.VERSION + "\n", true);
        GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();
        eb.setFooter("Rev: " + gitRepoState.describe, "https://cdn.discordapp.com/emojis/314068430787706880.png");
    }

    public void getPing(Guild guild){
        JDA jda = guild.getJDA();
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        String status = "Status: " + jda.getStatus() + "\n";
        String ping = "Ping: " + jda.getPing() + "ms\n";
        String guildn = "Guild: " + guild.getName() +"\n";
        eb.addField("<:online:313956277808005120> Connection Info" , status + ping + guildn, true);
    }

    public void getMemory(){
        Long TotMem = Runtime.getRuntime().totalMemory() / 1000000;
        Long FreeMem = Runtime.getRuntime().freeMemory() / 1000000;
        Long MaxMem = Runtime.getRuntime().maxMemory() / 1000000;
        Long CurrMem = TotMem - FreeMem;
        if (CurrMem > 750) {
            eb.setFooter("Warning, High memory usage!", "https://cdn.discordapp.com/emojis/313956276893646850.png");
            eb.setColor(Color.RED);
        } else if (CurrMem > 500) {
            eb.setFooter("Moderate memory usage", "https://cdn.discordapp.com/emojis/313956277220802560.png");
            eb.setColor(Color.YELLOW);
        } else {
            eb.setFooter("Low memory usage", "https://cdn.discordapp.com/emojis/313956277808005120.png");
            eb.setColor(Color.GREEN);
        }
        eb.addField("<:stafftools:314348604095594498> Memory Info", "Reserved memory: " + TotMem + "MB\n" + "-> Of which is used: " + CurrMem + "MB\n" + "-> Of which is free: " + FreeMem + "MB\n" + "Max reservable: " + MaxMem + "MB\n", true);
    }

    public void getShardInfo(Guild guild){
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        eb.addField("<:hypesquad:314068430854684672> Shard Info","Sharding: " + VertGreen.getInstance(guild.getJDA()).getShardInfo().getShardString() + "\n" + "Players playing: " + PlayerRegistry.getPlayingPlayers().size() + "\n" + "Known servers: " + VertGreen.getAllGuilds().size() + "\n" + "Known users in servers: " + VertGreen.countAllUniqueUsers() + "\n" , true);
    }
}
