package vertgreen.event;

import vertgreen.VertGreen;
import vertgreen.util.TextUtils;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.regex.Pattern;

public abstract class AbstractEventListener extends ListenerAdapter {

    static final Pattern COMMAND_NAME_PREFIX = Pattern.compile("(\\w+)");
    private final HashMap<String, UserListener> userListener = new HashMap<>();
//
    AbstractEventListener() {

    }

    @Override
    public void onReady(ReadyEvent event) {
        VertGreen.getInstance(event.getJDA()).onInit(event);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        UserListener listener = userListener.get(event.getAuthor().getId());
        if (listener != null) {
            try{
            listener.onGuildMessageReceived(event);
            } catch(Exception ex){
                TextUtils.handleException(ex, event.getChannel(), event.getMember());
            }
        }
    }

    public void putListener(String id, UserListener listener) {
        userListener.put(id, listener);
    }

    public void removeListener(String id) {
        userListener.remove(id);
    }
}
