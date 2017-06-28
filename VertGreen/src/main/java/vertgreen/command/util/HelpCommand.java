package vertgreen.command.util;

import vertgreen.Config;
import vertgreen.command.fun.TalkCommand;
import vertgreen.command.music.control.SelectCommand;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandOwnerRestricted;
import vertgreen.commandmeta.abs.IMusicBackupCommand;
import vertgreen.commandmeta.abs.IUtilCommand;
import vertgreen.feature.I18n;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class HelpCommand extends Command implements IMusicBackupCommand, IUtilCommand {

    public static String inviteLink = "https://discord.gg/dWVqUP5";

    private static final Logger log = LoggerFactory.getLogger(HelpCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {

        if (args.length > 1) {
            String commandOrAlias = args[1];
            sendFormattedCommandHelp(guild, channel, invoker, commandOrAlias);
        } else {
            sendGeneralHelp(guild, channel, invoker);
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} OR {0}{1} <command>\n#";
        return usage + I18n.get(guild).getString("helpHelpCommand");
    }

    private static void sendGeneralHelp(Guild guild, TextChannel channel, Member invoker) {
        if (!invoker.getUser().hasPrivateChannel()) {
            try {
                invoker.getUser().openPrivateChannel().complete(true);
            } catch (RateLimitedException e) {
                throw new RuntimeException(e);
            }
        }
        invoker.getUser().getPrivateChannel().sendMessage(getHelpDmMsg(guild)).queue();
        String out = I18n.get(guild).getString("helpSent");
        out += "\n" + MessageFormat.format(I18n.get(guild).getString("helpCommandsPromotion"), "`" + Config.CONFIG.getPrefix() + "commands`");
        TextUtils.replyWithName(channel, invoker, out);
    }

    public static String getFormattedCommandHelp(Guild guild, Command command, String commandOrAlias) {
        String helpStr = command.help(guild);
        String thirdParam = "";
        if (command instanceof TalkCommand)
            thirdParam = guild.getSelfMember().getEffectiveName();
        else if (command instanceof SelectCommand)
            thirdParam = "play";

        return MessageFormat.format(helpStr, Config.CONFIG.getPrefix(), commandOrAlias, thirdParam);
    }

    public static void sendFormattedCommandHelp(Guild guild, TextChannel channel, Member invoker, String commandOrAlias) {

        CommandRegistry.CommandEntry commandEntry = CommandRegistry.getCommand(commandOrAlias);
        if (commandEntry == null) {
            String out = Config.CONFIG.getPrefix() + commandOrAlias + ": " + I18n.get(guild).getString("helpUnknownCommand");
            out += "\n" + MessageFormat.format(I18n.get(guild).getString("helpCommandsPromotion"), "`" + Config.CONFIG.getPrefix() + "commands`");
            TextUtils.replyWithName(channel, invoker, out);
            return;
        }

        Command command = commandEntry.command;

        String out = getFormattedCommandHelp(guild, command, commandOrAlias);

        if (command instanceof ICommandOwnerRestricted)
            out += "\n#" + I18n.get(guild).getString("helpCommandOwnerRestricted");
        out = TextUtils.asMarkdown(out);
        out = I18n.get(guild).getString("helpProperUsage") + out;
        TextUtils.replyWithName(channel, invoker, out);
    }

    public static String getHelpDmMsg(Guild guild) {
        return MessageFormat.format(I18n.get(guild).getString("helpDM"), inviteLink);
    }
}
