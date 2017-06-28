package vertgreen.audio.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;

public class SplitAudioTrackContext extends AudioTrackContext {

    private final long startPos;
    private final long endPos;
    private final String title;

    public SplitAudioTrackContext(AudioTrack at, Member member, long startPos, long endPos, String title) {
        super(at, member);
        this.startPos = startPos;
        this.endPos = endPos;
        this.title = title;
    }

    @Override
    public long getEffectiveDuration() {
        return endPos - startPos;
    }

    @Override
    public long getEffectivePosition() {
        return track.getPosition() - startPos;
    }

    @Override
    public void setEffectivePosition(long position) {
        track.setPosition(startPos + position);
    }

    @Override
    public String getEffectiveTitle() {
        return title;
    }

    @Override
    public long getStartPosition() {
        return startPos;
    }

    @Override
    public AudioTrackContext makeClone() {
        AudioTrack track = getTrack().makeClone();
        track.setPosition(startPos);
        return new SplitAudioTrackContext(track, getMember(), startPos, endPos, title);
    }
}
