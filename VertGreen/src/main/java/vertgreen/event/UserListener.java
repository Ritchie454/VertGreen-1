package vertgreen.event;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public abstract class UserListener {
    
    public abstract void onGuildMessageReceived(GuildMessageReceivedEvent event);
    
}
