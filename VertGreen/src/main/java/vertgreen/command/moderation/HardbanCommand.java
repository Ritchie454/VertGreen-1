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

package vertgreen.command.moderation;

import vertgreen.Config;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IModerationCommand;
import vertgreen.feature.I18n;
import vertgreen.util.ArgumentUtil;
import vertgreen.util.DiscordUtil;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Created by napster on 19.04.17.
 * <p>
 * Ban a user.
 */
//Basically a copy pasta of the softban command. If you change something here make sure to check the related
// moderation commands too.
public class HardbanCommand extends Command implements IModerationCommand {

    private static final Logger log = LoggerFactory.getLogger(HardbanCommand.class);

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        //Ensure we have a search term
        if (args.length == 1) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        Member target = ArgumentUtil.checkSingleFuzzyMemberSearchResult(channel, args[1]);

        if (target == null) return;

        if (!checkHardBanAuthorization(channel, invoker, target)) return;

        target.getGuild().getController().ban(target, 7).queue(
                aVoid -> {
                    TextUtils.replyWithName(channel, invoker, MessageFormat.format(I18n.get(guild).getString("hardbanSuccess"), target.getUser().getName(), target.getUser().getDiscriminator(), target.getUser().getId()));
                },
                throwable -> log.error(MessageFormat.format(I18n.get(guild).getString("modBanFail"), target.getUser()))
        );
    }

    private boolean checkHardBanAuthorization(TextChannel channel, Member mod, Member target) {
        if (mod == target) {
            TextUtils.replyWithName(channel, mod, I18n.get(channel.getGuild()).getString("hardbanFailSelf"));
            return false;
        }

        if (target.isOwner()) {
            TextUtils.replyWithName(channel, mod, I18n.get(channel.getGuild()).getString("hardbanFailOwner"));
            return false;
        }

        if (target == target.getGuild().getSelfMember()) {
            TextUtils.replyWithName(channel, mod, I18n.get(channel.getGuild()).getString("hardbanFailMyself"));
            return false;
        }

        if (!mod.hasPermission(Permission.BAN_MEMBERS, Permission.KICK_MEMBERS) && !mod.isOwner()) {
            TextUtils.replyWithName(channel, mod, I18n.get(channel.getGuild()).getString("modKickBanFailUserPerms"));
            return false;
        }

        if (DiscordUtil.getHighestRolePosition(mod) <= DiscordUtil.getHighestRolePosition(target) && !mod.isOwner()) {
            TextUtils.replyWithName(channel, mod, MessageFormat.format(I18n.get(channel.getGuild()).getString("modFailUserHierarchy"), target.getEffectiveName()));
            return false;
        }

        if (!mod.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            TextUtils.replyWithName(channel, mod, I18n.get(channel.getGuild()).getString("modBanBotPerms"));
            return false;
        }

        if (DiscordUtil.getHighestRolePosition(mod.getGuild().getSelfMember()) <= DiscordUtil.getHighestRolePosition(target)) {
            TextUtils.replyWithName(channel, mod, MessageFormat.format(I18n.get(channel.getGuild()).getString("modFailBotHierarchy"), target.getEffectiveName()));
            return false;
        }

        return true;
    }


    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} <user>\n#";
        return usage + I18n.get(guild).getString("helpHardbanCommand");
    }
}

