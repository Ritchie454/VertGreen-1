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
 */

package vertgreen.command.util;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.Config;
import vertgreen.command.fun.TextCommand;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.commandmeta.MessagingException;
import vertgreen.commandmeta.abs.*;
import vertgreen.feature.I18n;
import vertgreen.perms.PermissionLevel;
import vertgreen.perms.PermsUtil;
import vertgreen.util.DiscordUtil;
import vertgreen.util.TextUtils;
import vertgreen.util.constant.BotConstants;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by napster on 22.03.17.
 * <p>
 * YO DAWG I HEARD YOU LIKE COMMANDS SO I PUT
 * THIS COMMAND IN YO BOT SO YOU CAN SHOW MORE
 * COMMANDS WHILE YOU EXECUTE THIS COMMAND
 * <p>
 * Display available commands
 */
public class CommandsCommand extends Command implements IUtilCommand {

    //design inspiration by Weiss Schnee's bot
    //https://cdn.discordapp.com/attachments/230033957998166016/296356070685671425/unknown.png
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        mainBotHelp(guild, channel, invoker);
    }

    private void mainBotHelp(Guild guild, TextChannel channel, Member invoker) {
        Set<String> commandsAndAliases = CommandRegistry.getRegisteredCommandsAndAliases();
        Set<String> unsortedAliases = new HashSet<>(); //hash set = only unique commands
        for (String commandOrAlias : commandsAndAliases) {
            String mainAlias = CommandRegistry.getCommand(commandOrAlias).name;
            unsortedAliases.add(mainAlias);
        }
        //alphabetical order
        List<String> sortedAliases = new ArrayList<>(unsortedAliases);
        Collections.sort(sortedAliases);

        String fun = "**" + I18n.get(guild).getString("commandsFun") + ":** ";
        String memes = "**" + I18n.get(guild).getString("commandsMemes") + ":**";
        String util = "**" + I18n.get(guild).getString("commandsUtility") + ":** ";
        String mod = "**" + I18n.get(guild).getString("commandsModeration") + ":** ";
        String maint = "**" + I18n.get(guild).getString("commandsMaintenance") + ":** ";
        String owner = "**" + I18n.get(guild).getString("commandsBotOwner") + ":** ";

        for (String alias : sortedAliases) {
            Command c = CommandRegistry.getCommand(alias).command;
            String formattedAlias = "`" + alias + "` ";

            if (c instanceof ICommandRestricted
                    && ((ICommandRestricted) c).getMinimumPerms() == PermissionLevel.BOT_OWNER) {
                owner += formattedAlias;
            } else if (c instanceof TextCommand) {
                memes += formattedAlias;
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
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN_COLOR);
        eb.setTitle("<:botTag:230105988211015680> Commands <:botTag:230105988211015680>");
        eb.addField("Memes", memes.replace("Memes", ""), true);
        eb.addField("Fun", fun.replace("Fun", ""), true);
        eb.addField("Utility", util.replace("Utility", ""), true);
        String mods = "";
        String owners = "";

        if (invoker.hasPermission(Permission.MESSAGE_MANAGE)) {
            eb.addField("Moderation", mod.replace("Moderation", ""), true);
            mods = "\n-Moderation---------------------------------\n" + mod.replace("Moderation", "");
        }

        if (PermsUtil.isUserBotOwner(invoker.getUser())) {
            eb.addField("Maintenance", maint.replace("Maintenance", ""), true);
            eb.addField("Bot Owner", owner.replace("Bot owner", ""), true);
            owners = "\n-Owner--------------------------------------\n" + owner.replace("Bot owner", "");
        }

        eb.addField(MessageFormat.format(I18n.get(guild).getString("commandsMoreHelp"), "`" + Config.CONFIG.getPrefix() + "help <command>`"), "", true);
        channel.sendMessage(eb.build()).queue();
        try {
            String url = TextUtils.postToHastebin("VERTBOT COMMANDS\n--------------------------------------------\n" + "-Fun----------------------------------------\n" + fun.replace("Fun", "") + "\n-Utility------------------------------------\n" + util.replace("Utility", "") + mods + owners, true) + ".vertcmds";
            channel.sendMessage("If you can't see embeds, you can use this handy link instead!\n" + url).queue();
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
