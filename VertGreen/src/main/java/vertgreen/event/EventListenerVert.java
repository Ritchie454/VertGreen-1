package vertgreen.event;

import vertgreen.Config;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.commandmeta.abs.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;

public class EventListenerVert extends AbstractEventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListenerVert.class);

    public EventListenerVert() {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            return;
        }

        if (event.getMessage().getContent().length() < Config.CONFIG.getPrefix().length()) {
            return;
        }

        if (event.getMessage().getContent().substring(0, Config.CONFIG.getPrefix().length()).equals(Config.CONFIG.getPrefix())) {
            Command invoked = null;
            try {
                log.info(event.getGuild().getName() + " \t " + event.getAuthor().getName() + " \t " + event.getMessage().getRawContent());
                Matcher matcher = COMMAND_NAME_PREFIX.matcher(event.getMessage().getContent());
                matcher.find();

                invoked = CommandRegistry.getCommand(matcher.group()).command;
            } catch (NullPointerException ignored) {

            }

            if (invoked == null) {
                return;
            }

            CommandManager.prefixCalled(invoked, event.getGuild(), event.getTextChannel(), event.getMember(), event.getMessage());

            try {
                event.getMessage().delete().queue();
            } catch (Exception ignored) {
            }

        }
    }

}
