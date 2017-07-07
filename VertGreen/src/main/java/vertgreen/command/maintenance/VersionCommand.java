package vertgreen.command.maintenance;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import vertgreen.VertGreen;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import vertgreen.util.GitRepoState;

public class VersionCommand extends Command implements IMaintenanceCommand {
    EmbedBuilder eb;

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {       
        eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        getVersion(guild);
        channel.sendMessage(eb.build()).queue();
    }
    
    private void getVersion(Guild guild){         
        eb.addField("<:partner:314068430556758017> Version Info", "Distribution: " + BotConstants.RELEASE + "\n" + "Bot Version: " + BotConstants.VERSION + "\n" + "JDA responses total: " + guild.getJDA().getResponseTotal() + "\n" + "JDA version: " + JDAInfo.VERSION + "\n" + "Lavaplayer version: " + PlayerLibrary.VERSION + "\n", true);
        GitRepoState gitRepoState = GitRepoState.getGitRepositoryState();
        eb.setFooter("Rev: " + gitRepoState.describe, "https://cdn.discordapp.com/emojis/314068430787706880.png");
    }
    @Override
    public String help(Guild guild) {
        return "{0}{1}\n#Show some statistics about this bot.";
    }
}
