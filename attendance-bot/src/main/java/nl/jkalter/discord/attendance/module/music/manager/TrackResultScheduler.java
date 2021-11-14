package nl.jkalter.discord.attendance.module.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TrackResultScheduler implements AudioLoadResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackResultScheduler.class);

    private final TrackScheduler scheduler;

    public TrackResultScheduler(final TrackScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        LOGGER.debug("Audio track loaded {}", track);
        // LavaPlayer found an audio source for us to play
        scheduler.queue(track);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
        LOGGER.debug("Play list loaded {}", playlist);
        // LavaPlayer found multiple AudioTracks from some playlist
        playlist.getTracks().forEach(this::trackLoaded);
    }

    @Override
    public void noMatches() {
        LOGGER.debug("No matches found.");
        // LavaPlayer did not find any audio to extract
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        LOGGER.debug("Loading failed.", exception);
        // LavaPlayer could not parse an audio source for some reason
    }


}