package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.command.music.control.SkipCommand;
import vertgreen.command.music.control.PlayCommand;
import vertgreen.command.music.control.PauseCommand;
import vertgreen.command.music.control.StopCommand;
import vertgreen.command.music.control.PlaySplitCommand;
import vertgreen.command.music.control.JoinCommand;
import vertgreen.command.music.control.ReshuffleCommand;
import vertgreen.command.music.control.LeaveCommand;
import vertgreen.command.music.control.VolumeCommand;
import vertgreen.command.music.control.UnpauseCommand;
import vertgreen.command.music.control.SelectCommand;
import vertgreen.command.music.control.RepeatCommand;
import vertgreen.command.music.control.ShuffleCommand;
import vertgreen.command.music.info.ExportCommand;
import vertgreen.command.music.info.ListCommand;
import vertgreen.command.music.info.NowplayingCommand;
import vertgreen.command.music.seeking.ForwardCommand;
import vertgreen.command.music.seeking.RestartCommand;
import vertgreen.command.music.seeking.RewindCommand;
import vertgreen.command.music.seeking.SeekCommand;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMusicCommand;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.util.*;
import vertgreen.commandmeta.MessagingException;
import vertgreen.util.TextUtils;

public class MusicHelpCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        String Help = "";
        //aggregate all commands and the aliases they may be called with
        Map<Class<? extends Command>, List<String>> commandToAliases = new HashMap<>();
        Set<String> commandsAndAliases = CommandRegistry.getRegisteredCommandsAndAliases();
        for (String commandOrAlias : commandsAndAliases) {
            Command command = CommandRegistry.getCommand(commandOrAlias).command;

            List<String> aliases = commandToAliases.get(command.getClass());
            if (aliases == null) aliases = new ArrayList<>();
            aliases.add(commandOrAlias);
            commandToAliases.put(command.getClass(), aliases);
        }

        //sum up existing music commands & sort them in a presentable way
        List<Command> sortedComms = new ArrayList<>();
        for (List<String> as : commandToAliases.values()) {
            Command c = CommandRegistry.getCommand(as.get(0)).command;
            if (c instanceof IMusicCommand)
                sortedComms.add(c);
        }
        Collections.sort(sortedComms, new MusicCommandsComparator());
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("🎵 Vert's Music Commands 🎵");
        eb.setColor(BotConstants.VERTGREEN);
        
        //create help strings for each music command and its main alias
        List<String> musicComms = new ArrayList<>();
        for (Command command : sortedComms) {
            String mainAlias = commandToAliases.get(command.getClass()).get(0);
            mainAlias = CommandRegistry.getCommand(mainAlias).name;
            String formattedHelp = HelpCommand.getFormattedCommandHelp(guild, command, mainAlias);
            musicComms.add(formattedHelp);
            eb.addField(mainAlias, formattedHelp, true);
            Help = Help + mainAlias + "\n" + formattedHelp + "\n-----------------------\n";
        }
        
        //output the resulting help, splitting it in several messages if necessary
        String out = "🎵 Vert's Music Commands 🎵\n";
        for (String s : musicComms) {
            if (out.length() + s.length() >= 1990) {
                out = "";
            }
            out += s + "\n";
        }
        
        channel.sendMessage(eb.build()).queue();
        try {
            String comurl = TextUtils.postToHastebin(Help, true) + ".vertmusiccmds";
            channel.sendMessage("If you can't see embeds, you can use this handy link instead!\n" + comurl).queue();
        }
        catch (UnirestException ex) {
            throw new MessagingException("Export Failed");
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpMusicHelpCommand");
    }

    /**
     * Sort the commands in a sensible way to display them to the user
     */
    static class MusicCommandsComparator implements Comparator<Command> {

        @Override
        public int compare(Command o1, Command o2) {
            return getCommandRank(o1) - getCommandRank(o2);
        }

        /**
         * a container of smelly code
         * http://stackoverflow.com/a/2790215
         */
        private static int getCommandRank(Command c) {

            int result;

            if (c instanceof PlayCommand) {
                result = 10050;
            } else if (c instanceof ListCommand) {
                result = 10100;
            } else if (c instanceof NowplayingCommand) {
                result = 10150;
            } else if (c instanceof SkipCommand) {
                result = 10200;
            } else if (c instanceof StopCommand) {
                result = 10250;
            } else if (c instanceof PauseCommand) {
                result = 10300;
            } else if (c instanceof UnpauseCommand) {
                result = 10350;
            } else if (c instanceof JoinCommand) {
                result = 10400;
            } else if (c instanceof LeaveCommand) {
                result = 10450;
            } else if (c instanceof RepeatCommand) {
                result = 10500;
            } else if (c instanceof ShuffleCommand) {
                result = 10550;
            } else if (c instanceof ReshuffleCommand) {
                result = 10560;
            } else if (c instanceof ForwardCommand) {
                result = 10600;
            } else if (c instanceof RewindCommand) {
                result = 10650;
            } else if (c instanceof SeekCommand) {
                result = 10700;
            } else if (c instanceof RestartCommand) {
                result = 10750;
            } else if (c instanceof ExportCommand) {
                result = 10800;
            } else if (c instanceof PlaySplitCommand) {
                result = 10850;
            } else if (c instanceof SelectCommand) {
                result = 10900;
            } else if (c instanceof VolumeCommand) {
                result = 10970;
            } else {
                //everything else
                //newly added commands will land here, just add them to the giant if construct above to assign them a fixed place
                result = 10999;
            }
            return result;
        }
    }
}
