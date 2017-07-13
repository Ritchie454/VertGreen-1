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

package vertgreen.event;

import vertgreen.VertGreen;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.JDA;
import vertgreen.util.DiscordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogger extends ListenerAdapter {

    public static final Logger log = LoggerFactory.getLogger(EventLogger.class);
    String msg;
    private final String logChannelId;
    private VertGreen shard;

    public EventLogger(String logChannelId) {
        this.logChannelId = logChannelId;
        Runtime.getRuntime().addShutdownHook(new Thread(ON_SHUTDOWN, EventLogger.class.getSimpleName() + " shutdownhook"));
    }

    private void send(Message msg) {
        send(msg.getRawContent());
    }

    private void send(String msg) {
        /*JDA jda = shard.getJda(); //do a null check if you ever uncomment this code again
        DiscordUtil.sendShardlessMessage(jda, logChannelId,
                VertGreen.getInstance(jda).getShardInfo().getShardString()
                + " "
                + msg
        );
        log.info(msg);*/
    }

    @Override
    public void onReady(ReadyEvent event) {
        VertGreen.getInstance(event.getJDA());
        /*send(new MessageBuilder()
                .append("[:rocket:] Received ready event.")
                .build()
        );*/
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        msg = "[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.";
        event.getJDA().getTextChannelById("332940748905512960").sendMessage(msg).queue();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        msg = "[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.";
        event.getJDA().getTextChannelById("332940748905512960").sendMessage(msg).queue();
    }

    private final Runnable ON_SHUTDOWN = () -> {
        Runtime rt = Runtime.getRuntime();
        JDA jda = shard.getJda();
        if(VertGreen.shutdownCode != VertGreen.UNKNOWN_SHUTDOWN_CODE){
            msg = "[:door:] Exiting with code " + VertGreen.shutdownCode + ".";
        } else {
            msg = "[:door:] Exiting with unknown code.";
        }
        jda.getTextChannelById("285472208686546946").sendMessage(msg).queue();
    };

}
