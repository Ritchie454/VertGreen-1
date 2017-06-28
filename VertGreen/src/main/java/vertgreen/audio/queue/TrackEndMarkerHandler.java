package vertgreen.audio.queue;

import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;
import vertgreen.audio.AbstractPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackEndMarkerHandler implements TrackMarkerHandler {

    private static final Logger log = LoggerFactory.getLogger(TrackEndMarkerHandler.class);

    private final AbstractPlayer player;
    private final AudioTrackContext track;

    public TrackEndMarkerHandler(AbstractPlayer player, AudioTrackContext track) {
        this.player = player;
        this.track = track;
    }

    @Override
    public void handle(MarkerState state) {
        log.info("Stopping track " + track.getEffectiveTitle() + " because of end state: " + state);
        if (player.getPlayingTrack() != null && player.getPlayingTrack().getId() == track.getId()) {
            //if this was ended because the track finished instead of skipped, we need to transfer that info
            //state == STOPPED if the user skips it
            //state == REACHED if the tracks runs out by itself
            if (state.equals(MarkerState.REACHED))
                player.splitTrackEnded();
            else
                player.skip();
        }
    }
}
