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

    private final String logChannelId;
    public JDA jda;

    public EventLogger(String logChannelId) {
        this.logChannelId = "330067321148145675";
        Runtime.getRuntime().addShutdownHook(new Thread(ON_SHUTDOWN, EventLogger.class.getSimpleName() + " shutdownhook"));

    }

    private void send(String msg) {
        jda.getTextChannelById(logChannelId).sendMessage(msg).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        VertGreen.getInstance(event.getJDA());
        send("[:rocket:] Received ready event.");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        send("[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.");

    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        send("[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.");
    }

    private final Runnable ON_SHUTDOWN = () -> {
        Runtime rt = Runtime.getRuntime();
        if(VertGreen.shutdownCode != VertGreen.UNKNOWN_SHUTDOWN_CODE){
            send("[:door:] Exiting with code " + VertGreen.shutdownCode + ".");
        } else {
            send("[:door:] Exiting with unknown code.");
        }
    };
}
