package vertgreen.audio.queue;

import java.util.List;

public interface ITrackProvider {
    
    AudioTrackContext provideAudioTrack(boolean skipped);
    
    AudioTrackContext getNext();

    List<AudioTrackContext> getAsList();

    List<AudioTrackContext> getAsListOrdered();
    
    boolean isEmpty();
    
    void add(AudioTrackContext track);
    
    void clear();

    boolean remove(AudioTrackContext atc);

    AudioTrackContext removeAt(int i);

    List<AudioTrackContext> getInRange(int startIndex, int endIndex);
    
}
