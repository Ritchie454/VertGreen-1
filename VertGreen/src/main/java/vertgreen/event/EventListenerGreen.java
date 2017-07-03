package vertgreen.event;

import vertgreen.Config;
import vertgreen.audio.GuildPlayer;
import vertgreen.audio.PlayerRegistry;
import vertgreen.command.fun.TalkCommand;
import vertgreen.command.music.control.SkipCommand;
import vertgreen.command.util.HelpCommand;
import vertgreen.commandmeta.CommandManager;
import vertgreen.commandmeta.CommandRegistry;
import vertgreen.commandmeta.abs.Command;
import vertgreen.db.EntityReader;
import vertgreen.feature.I18n;
import vertgreen.feature.togglz.FeatureFlags;
import vertgreen.util.Tuple2;
import vertgreen.util.ratelimit.Ratelimiter;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import net.dv8tion.jda.core.OnlineStatus;

public class EventListenerGreen extends AbstractEventListener {

    private static final Logger log = LoggerFactory.getLogger(EventListenerGreen.class);

    //first string is the users message ID, second string the id of fredboat's message that should be deleted if the
    // user's message is deleted
    public static Map<String, String> messagesToDeleteIfIdDeleted = new HashMap<>();
    private User lastUserToReceiveHelp;

    public EventListenerGreen() {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (FeatureFlags.RATE_LIMITER.isActive()) {
            if (Ratelimiter.getRatelimiter().isBlacklisted(event.getAuthor().getIdLong())) {
                return;
            }
        }

        if (event.getPrivateChannel() != null) {
            log.info("PRIVATE" + " \t " + event.getAuthor().getName() + " \t " + event.getMessage().getRawContent());
            return;
        }

        if (event.getAuthor().equals(event.getJDA().getSelfUser())) {
            log.info(event.getGuild().getName() + " \t " + event.getAuthor().getName() + " \t " + event.getMessage().getRawContent());
            return;
        }

        if (event.getMessage().getContent().length() < Config.CONFIG.getPrefix().length()) {
            return;
        }

        if (event.getMessage().getContent().substring(0, Config.CONFIG.getPrefix().length()).equals(Config.CONFIG.getPrefix())) {
            Command invoked = null;
            log.info(event.getGuild().getName() + " \t " + event.getAuthor().getName() + " \t " + event.getMessage().getRawContent());
            Matcher matcher = COMMAND_NAME_PREFIX.matcher(event.getMessage().getContent());

            if(matcher.find()) {
                String cmdName = matcher.group();
                CommandRegistry.CommandEntry entry = CommandRegistry.getCommand(cmdName);
                if(entry != null) {
                    invoked = entry.command;
                } else {
                    log.info("Unknown command:", cmdName);
                }
            }

            if (invoked == null) {
                return;
            }

            limitOrExecuteCommand(invoked, event);
        } else if (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())) {
            log.info(event.getGuild().getName() + " \t " + event.getAuthor().getName() + " \t " + event.getMessage().getRawContent());
            CommandManager.commandsExecuted++;
            //regex101.com/r/9aw6ai/1/
            String message = event.getMessage().getRawContent().replaceAll("<@!?[0-9]*>", "");
            TalkCommand.talk(event.getMember(), event.getTextChannel(), message);
        }
    }

    /**
     * check the rate limit of user and execute the command if everything is fine
     */
    private void limitOrExecuteCommand(Command invoked, MessageReceivedEvent event) {
        Tuple2<Boolean, Class> ratelimiterResult = new Tuple2<>(true, null);
        if (FeatureFlags.RATE_LIMITER.isActive()) {
            ratelimiterResult = Ratelimiter.getRatelimiter().isAllowed(event.getMember(), invoked, 1, event.getTextChannel());

        }
        if (ratelimiterResult.a)
            CommandManager.prefixCalled(invoked, event.getGuild(), event.getTextChannel(), event.getMember(), event.getMessage());
        else {
            String out = event.getMember().getAsMention() + ": " + I18n.get(event.getGuild()).getString("ratelimitedGeneralInfo");
            if (ratelimiterResult.b == SkipCommand.class) { //we can compare classes with == as long as we are using the same classloader (which we are)
                //add a nice reminder on how to skip more than 1 song
                out += "\n" + MessageFormat.format(I18n.get(event.getGuild()).getString("ratelimitedSkipCommand"), "`" + Config.CONFIG.getPrefix() + "skip n-m`");
            }
            event.getTextChannel().sendMessage(out).queue();
        }

    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (messagesToDeleteIfIdDeleted.containsKey(event.getMessageId())) {
            String msgId = messagesToDeleteIfIdDeleted.remove(event.getMessageId());
            event.getChannel().deleteMessageById(msgId).queue();
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {

        if (FeatureFlags.RATE_LIMITER.isActive()) {
            if (Ratelimiter.getRatelimiter().isBlacklisted(event.getAuthor().getIdLong())) {
                return;
            }
        }

        if (event.getAuthor() == lastUserToReceiveHelp) {
            //Ignore, they just got help! Stops any bot chain reactions
            return;
        }

        if (event.getAuthor().equals(event.getJDA().getSelfUser())) {
            //Don't reply to ourselves
            return;
        }

        event.getChannel().sendMessage(HelpCommand.getHelpDmMsg(null)).queue();
        lastUserToReceiveHelp = event.getAuthor();
    }

    @Override
    public void onReady(ReadyEvent event) {
        super.onReady(event);
        //event.getJDA().getPresence().setGame(Game.of("with Nepgear"));
        
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        //event.getJDA().getPresence().setGame(Game.of("with Nepgear"));

    }

    /* music related */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        GuildPlayer player = PlayerRegistry.getExisting(event.getGuild());

        if (player == null) {
            return;
        }

        //we got kicked from the server while in a voice channel, do nothing and return, because onGuildLeave()
        // should take care of destroying stuff
        if (!event.getGuild().isMember(event.getJDA().getSelfUser())) {
            log.warn("onGuildVoiceLeave called for a guild where we aren't a member. This line should only ever be " +
                    "reached if we are getting kicked from that guild. Investigate if not.");
            return;
        }

        if (player.getHumanUsersInVC().isEmpty()
                && player.getUserCurrentVoiceChannel(event.getGuild().getSelfMember()) == event.getChannelLeft()
                && !player.isPaused()) {
            player.pause();
            player.getActiveTextChannel().sendMessage(I18n.get(event.getGuild()).getString("eventUsersLeftVC")).queue();
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        GuildPlayer player = PlayerRegistry.getExisting(event.getGuild());

        if(player != null
                && player.isPaused()
                && player.getPlayingTrack() != null
                && event.getChannelJoined().getMembers().contains(event.getGuild().getSelfMember())
                && player.getHumanUsersInVC().size() == 1
                && EntityReader.getGuildConfig(event.getGuild().getId()).isAutoResume()
                ) {
            player.getActiveTextChannel().sendMessage(I18n.get(event.getGuild()).getString("eventAutoResumed")).queue();
            player.setPause(false);
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        PlayerRegistry.destroyPlayer(event.getGuild());
    }

}
