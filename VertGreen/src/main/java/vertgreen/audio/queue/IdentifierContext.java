package vertgreen.audio.queue;

import vertgreen.VertGreen;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class IdentifierContext {

    public final VertGreen shard;
    public final String identifier;
    private final String textChannel;
    private final String user;
    private final String guild;
    private boolean quiet = false;
    private boolean split = false;
    private long position = 0L;

    public IdentifierContext(String identifier, TextChannel textChannel, Member member) {
        this.shard = VertGreen.getInstance(textChannel.getJDA());
        this.identifier = identifier;
        this.textChannel = textChannel.getId();
        this.user = member.getUser().getId();
        this.guild = member.getGuild().getId();
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public long getPosition() {
        return position;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public TextChannel getTextChannel() {
        return shard.getJda().getTextChannelById(textChannel);
    }

    public Member getMember() {
        JDA jda = shard.getJda();
        return jda.getGuildById(guild).getMemberById(user);
    }
}
