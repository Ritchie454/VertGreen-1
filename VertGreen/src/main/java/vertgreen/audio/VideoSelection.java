package vertgreen.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;

public class VideoSelection {

    private final List<AudioTrack> choices;
    private final String outMsgId;

    public VideoSelection(List<AudioTrack> choices, Message outMsg) {
        this.choices = choices;
        this.outMsgId = outMsg.getId();
    }

    public List<AudioTrack> getChoices() {
        return choices;
    }

    public String getOutMsgId() {
        return outMsgId;
    }

}
