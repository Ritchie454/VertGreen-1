package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import vertgreen.commandmeta.abs.*;
import vertgreen.Config;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import vertgreen.util.BotConstants;
import java.text.MessageFormat;
import java.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.TextUtils;
import vertgreen.commandmeta.MessagingException;

public class CommandsCommand extends Command implements IUtilCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {

        mainBotHelp(guild, channel, invoker);
    }

    private void mainBotHelp(Guild guild, TextChannel channel, Member invoker) {
        Set<String> commandsAndAliases = CommandRegistry.getRegisteredCommandsAndAliases();
        Set<String> unsortedAliases = new HashSet<>(); //hash set = only unique commands
        commandsAndAliases.stream().map((commandOrAlias) -> CommandRegistry.getCommand(commandOrAlias).name).forEachOrdered((mainAlias) -> {
            unsortedAliases.add(mainAlias);
        });
        //alphabetical order
        List<String> sortedAliases = new ArrayList<>(unsortedAliases);
        Collections.sort(sortedAliases);

        String fun = I18n.get(guild).getString("commandsFun");
        String util = I18n.get(guild).getString("commandsUtility");
        String mod = I18n.get(guild).getString("commandsModeration");
        String maint = I18n.get(guild).getString("commandsMaintenance");
        String owner = I18n.get(guild).getString("commandsBotOwner");
        String user = "User";
        String image = "Image";

        for (String alias : sortedAliases) {
            Command c = CommandRegistry.getCommand(alias).command;
            String formattedAlias = alias + "  ";

            if (c instanceof ICommandOwnerRestricted) {
                owner += formattedAlias;
            } else {
                //overlap is possible in here, that's ok
                if (c instanceof IFunCommand) {
                    fun += formattedAlias;
                }
                if (c instanceof IUtilCommand) {
                    util += formattedAlias;
                }
                if (c instanceof IModerationCommand) {
                    mod += formattedAlias;
                }
                if (c instanceof IMaintenanceCommand) {
                    maint += formattedAlias;
                }
                if (c instanceof IUserCommand){
                    user += formattedAlias;
                }
                if (c instanceof IImageCommand){
                    image += formattedAlias;
                }
            }
        }

       EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        eb.setTitle("<:botTag:230105988211015680> Commands <:botTag:230105988211015680>");
        eb.addField("Fun", fun.replace("Fun", ""), true);
        eb.addField("Utility", util.replace("Utility", ""), true);
        eb.addField("User", user.replace("User", ""), true);
        eb.addField("Images", image.replace("Image", ""), true);
        String mods = "";
        String owners = "";
        if (invoker.hasPermission(Permission.MESSAGE_MANAGE)) {
            eb.addField("Moderation", mod.replace("Moderation", ""), true);
            mods = "\n-Moderation---------------------------------\n" + mod.replace("Moderation", "");
        }

        if (DiscordUtil.isUserBotOwner(invoker.getUser())) {
            eb.addField("Maintenance", maint.replace("Maintenance", ""), true);
            eb.addField("Bot Owner", owner.replace("Bot owner", ""), true);
            owners = "\n-Owner--------------------------------------\n" + owner.replace("Bot owner", "");
        }
        eb.addField(MessageFormat.format(I18n.get(guild).getString("commandsMoreHelp"), "`" + Config.CONFIG.getPrefix() + "help <command>`"), "", true);
        channel.sendMessage(eb.build()).queue();
        try {
        String comurl = TextUtils.postToHastebin("VERTBOT COMMANDS\n--------------------------------------------\n" + "-Fun----------------------------------------\n" + fun.replace("Fun", "") + "\n-Utility------------------------------------\n" + util.replace("Utility", "") + "-User---------------------------------------\n" + user.replace("User", "") + "\n-Images-------------------------------------\n" + image.replace("images", "") + mods + owners, true) + ".vertcmds";
        channel.sendMessage("If you can't see embeds, you can use this handy link instead!\n" + comurl).queue();
        }
        catch (UnirestException ex) {
            throw new MessagingException("Export Failed");
        }
     }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1}\n#";
        return usage + I18n.get(guild).getString("helpCommandsCommand");
    }
}
