package vertgreen.event;

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
        jda = event.getJDA();
        jda.getTextChannelById(logChannelId).sendMessage("[:rocket:] Received ready event.").queue();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        jda = event.getJDA();
        jda.getTextChannelById(logChannelId).sendMessage("[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.").queue();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        jda = event.getJDA();
        jda.getTextChannelById(logChannelId).sendMessage("[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.").queue();
    }
}
