package vertgreen.command.maintenance;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ShardListCommand extends Command implements IMaintenanceCommand {

    private static final int SHARDS_PER_MESSAGE = 30;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        MessageBuilder mb = null;
        List<MessageBuilder> builders = new ArrayList<>();

        //do a full report? or just a summary
        boolean full = false;
        if (args.length > 1 && ("full".equals(args[1]) || "all".equals(args[1]))) {
            full = true;
        }

        //make a copy to avoid concurrent modification errors
        List<VertGreen> shards = new ArrayList<>(VertGreen.getShards());
        int borkenShards = 0;
        int healthyGuilds = 0;
        final HashSet<String> healthyUsers = new HashSet<>();
        for (VertGreen vg : shards) {
            if (vg.getJda().getStatus() == JDA.Status.CONNECTED && !full) {
                healthyGuilds += vg.getJda().getGuilds().size();
                vg.getJda().getUsers().forEach((User u) -> healthyUsers.add(u.getId()));
            } else {
                if (borkenShards % SHARDS_PER_MESSAGE == 0) {
                    mb = new MessageBuilder()
                            .append("```diff\n");
                    builders.add(mb);
                }
                mb.append(vg.getJda().getStatus() == JDA.Status.CONNECTED ? "+" : "-")
                        .append(" ")
                        .append(vg.getShardInfo().getShardString())
                        .append(" ")
                        .append(vg.getJda().getStatus())
                        .append(" -- Guilds: ")
                        .append(String.format("%04d", vg.getJda().getGuilds().size()))
                        .append(" -- Users: ")
                        .append(vg.getJda().getUsers().size())
                        .append("\n");
                borkenShards++;
            }
        }

        //healthy shards summary, contains sensible data only if we aren't doing a full report
        if (!full) {
            channel.sendMessage("```diff\n+ "
                    + (shards.size() - borkenShards) + "/" + Config.CONFIG.getNumShards() + " shards are " + JDA.Status.CONNECTED
                    + " -- Guilds: " + healthyGuilds + " -- Users: " + healthyUsers.size() + "\n```").queue();
        }

        //detailed shards
        for(MessageBuilder builder : builders){
            builder.append("```");
            channel.sendMessage(builder.build()).queue();
        }
    }

    @Override
    public String help(Guild guild) {
        return "{0}{1} [full]\n#Show information about the shards of the bot as a summary or in a detailed report.";
    }
}
