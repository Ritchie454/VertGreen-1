package vertgreen.audio.queue;

import java.util.List;

public abstract class AbstractTrackProvider implements ITrackProvider {

    private RepeatMode repeatMode = RepeatMode.OFF;
    private boolean shuffle = false;

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public List<AudioTrackContext> getAsListOrdered() {
        return getAsList();
    }
    
}
