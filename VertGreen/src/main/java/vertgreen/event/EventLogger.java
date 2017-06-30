package vertgreen.event;

import vertgreen.VertGreen;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogger extends ListenerAdapter {

    public static final Logger log = LoggerFactory.getLogger(EventLogger.class);

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

        log.info(msg);
    }

    @Override
    public void onReady(ReadyEvent event) {
        VertGreen.getInstance(event.getJDA());
        send(new MessageBuilder()
                .append("[:rocket:] Received ready event.")
                .build()
        );
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        send(
                "[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`."
        );
        getTextChannelById("330067321148145675").sendMessage("[:white_check_mark:] Joined guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.").queue();
     
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        send(
                "[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`."
        );
        getTextChannelById("330067321148145675").sendMessage("[:x:] Left guild `" + event.getGuild() + "`. Users: `" + event.getGuild().getMembers().size() + "`.").queue();
    }

    private final Runnable ON_SHUTDOWN = () -> {
        Runtime rt = Runtime.getRuntime();
        if(VertGreen.shutdownCode != VertGreen.UNKNOWN_SHUTDOWN_CODE){
            send("[:door:] Exiting with code " + VertGreen.shutdownCode + ".");
            getTextChannelById("330067321148145675").sendMessage("[:door:] Exiting with code " + VertGreen.shutdownCode + ".").queue();
        } else {
            send("[:door:] Exiting with unknown code.");
            getTextChannelById("330067321148145675").sendMessage("[:door:] Exiting with unknown code.").queue();
        }
    };

}
