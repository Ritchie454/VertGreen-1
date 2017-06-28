package vertgreen.command.admin;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.ICommandAdminRestricted;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author frederik
 */
public class ReviveCommand extends Command implements ICommandAdminRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {

        int shardId;
        try {
            if (args[1].equals("guild")) {
                long guildId = Long.valueOf(args[2]);
                //https://discordapp.com/developers/docs/topics/gateway#sharding
                shardId = (int) ((guildId >> 22) % Config.CONFIG.getNumShards());
            } else
                shardId = Integer.parseInt(args[1]);

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            String command = args[0].substring(Config.CONFIG.getPrefix().length());
            HelpCommand.sendFormattedCommandHelp(guild, channel, invoker, command);
            return;
        }

        channel.sendMessage(TextUtils.prefaceWithName(invoker, " Reviving shard " + shardId)).queue();
        try {
            VertGreen.getInstance(shardId).revive();
        } catch (IndexOutOfBoundsException e) {
            channel.sendMessage(TextUtils.prefaceWithName(invoker, " No such shard: " + shardId)).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} <shardId> OR {0}{1} guild <guildId>\n#Revive the specified shard, or the shard of the specified guild.";
    }
}
