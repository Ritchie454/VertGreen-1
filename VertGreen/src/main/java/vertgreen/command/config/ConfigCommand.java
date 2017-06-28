package vertgreen.command.config;

import vertgreen.Config;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IModerationCommand;
import vertgreen.db.EntityReader;
import vertgreen.db.EntityWriter;
import vertgreen.db.entity.GuildConfig;
import vertgreen.feature.I18n;
import vertgreen.util.DiscordUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.EmbedBuilder;
import vertgreen.util.BotConstants;

import java.text.MessageFormat;

public class ConfigCommand extends Command implements IModerationCommand {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if(args.length == 1) {
            printConfig(guild, channel, invoker, message, args);
        } else {
            setConfig(guild, channel, invoker, message, args);
        }
    }

    private void printConfig(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        GuildConfig gc = EntityReader.getGuildConfig(guild.getId());
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        eb.setThumbnail(guild.getIconUrl());
        eb.setFooter(" | Vertbot Configuration options Rev 4", "https://image.flaticon.com/icons/png/128/76/76716.png");
        eb.addField("Configuration for " + guild.getName(), "Use `" + Config.CONFIG.getPrefix() + "config <key> <value>` to adjust a specific value. The configuration is below:", true); 
        eb.addField("track_announce", "" + gc.isTrackAnnounce(), true);
        eb.addField("auto_resume", "" + gc.isAutoResume(), true);
        channel.sendMessage(eb.build()).queue(); 
    }

    private void setConfig(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        if (!invoker.hasPermission(Permission.ADMINISTRATOR)
                && !DiscordUtil.isUserBotOwner(invoker.getUser())){
            channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configNotAdmin"), invoker.getEffectiveName())).queue();
            return;
        }

        if(args.length != 3) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        GuildConfig gc = EntityReader.getGuildConfig(guild.getId());
        String key = args[1];
        String val = args[2];
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(BotConstants.VERTGREEN);
        eb.setThumbnail(guild.getIconUrl());
        eb.setFooter(" | Vertbot Configuration options Rev 4", "https://image.flaticon.com/icons/png/128/76/76716.png");
        switch (key) {
            case "track_announce":
                if (val.equalsIgnoreCase("true") | val.equalsIgnoreCase("false")) {
                    gc.setTrackAnnounce(Boolean.valueOf(val));
                    EntityWriter.mergeGuildConfig(gc);
                    eb.addField("Updated Configuration:", "track_announce" + (I18n.get(guild).getString("configSetTo")) + val , true);
                    channel.sendMessage(eb.build()).queue(); 
                } else {
                    channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configMustBeBoolean"), invoker.getEffectiveName())).queue();
                }
                break;
            case "auto_resume":
                if (val.equalsIgnoreCase("true") | val.equalsIgnoreCase("false")) {
                    gc.setAutoResume(Boolean.valueOf(val));
                    EntityWriter.mergeGuildConfig(gc);
                    eb.addField("Updated Configuration:", "auto_resume" + (I18n.get(guild).getString("configSetTo")) + val , true);
                    channel.sendMessage(eb.build()).queue(); 
                } else {
                    channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configMustBeBoolean"), invoker.getEffectiveName())).queue();
                }
                break;
            default:
                channel.sendMessage(MessageFormat.format(I18n.get(guild).getString("configUnknownKey"), invoker.getEffectiveName())).queue();
                break;
        }
    }

    @Override
    public String help(Guild guild) {
        String usage = "{0}{1} OR {0}{1} <key> <value>\n#";
        return usage + I18n.get(guild).getString("helpConfigCommand");
    }
}
