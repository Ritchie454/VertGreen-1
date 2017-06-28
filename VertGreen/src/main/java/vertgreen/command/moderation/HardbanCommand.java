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

        Member target = ArgumentUtil.checkSingleFuzzySearchResult(channel, args[1]);

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

