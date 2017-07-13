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
 *
 */

package vertgreen.command.maintenance;

import vertgreen.Config;
import vertgreen.VertGreen;
import vertgreen.commandmeta.abs.Command;
import vertgreen.commandmeta.abs.IMaintenanceCommand;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ShardsCommand extends Command implements IMaintenanceCommand {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        getShards(channel, args);
    }

    public void getShards(TextChannel channel, String args[]){
        MessageBuilder mb = null;
        List<MessageBuilder> builders = new ArrayList<>();
        final int SHARDS_PER_MESSAGE = 30;
        //do a full report? or just a summary
        boolean full = false;
        if (args.length > 1 && ("full".equals(args[1]) || "all".equals(args[1]))) {
            full = true;
        }

        //make a copy to avoid concurrent modification errors
        List<VertGreen> shards = new ArrayList<>(VertGreen.getShards());
        int borkenShards = 0;
        int healthyGuilds = 0;
        LongOpenHashSet healthyUsers = new LongOpenHashSet(VertGreen.getExpectedUserCount());
        for (VertGreen fb : shards) {
            if (fb.getJda().getStatus() == JDA.Status.CONNECTED && !full) {
                healthyGuilds += fb.getJda().getGuilds().size();
                fb.getJda().getUsers().parallelStream().mapToLong(ISnowflake::getIdLong).forEach(healthyUsers::add);
            } else {
                if (borkenShards % SHARDS_PER_MESSAGE == 0) {
                    mb = new MessageBuilder()
                            .append("```diff\n");
                    builders.add(mb);
                }
                mb.append(fb.getJda().getStatus() == JDA.Status.CONNECTED ? "+" : "-")
                        .append(" ")
                        .append(fb.getShardInfo().getShardString())
                        .append(" ")
                        .append(fb.getJda().getStatus())
                        .append(" -- Guilds: ")
                        .append(String.format("%04d", fb.getJda().getGuilds().size()))
                        .append(" -- Users: ")
                        .append(fb.getJda().getUsers().size())
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
