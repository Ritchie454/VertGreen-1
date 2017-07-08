package vertgreen.event;

import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import vertgreen.VertGreen;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogger extends ListenerAdapter {

    public static final Logger log = LoggerFactory.getLogger(EventLogger.class);

    public JDA jda;
    String msg = "";
    String logChannelId = "332940748905512960";
    public EventLogger() {

    }

    @Override
    public void onReady(ReadyEvent event) {
        msg = "[:rocket:] Received ready event.";
        jda = event.getJDA();
        jda.getTextChannelById(logChannelId).sendMessage(msg).queue();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        msg = "[<:White_check_mark:333308311443341313>] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.";
        jda = event.getJDA();
        jda.getTextChannelById(logChannelId).sendMessage(msg).queue();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        msg = "[<:White_cross_mark:333308311443341313>] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.";
        jda = event.getJDA();
        jda.getTextChannelById(logChannelId).sendMessage(msg).queue();
    }
}
