package nl.jkalter.discord.attendance.module.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.player = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            LOGGER.debug("Queuing playback of track {}", track);
            if (queue.offer(track)) {
                LOGGER.trace("Queued playback of track {}", track);
            }
        } else {
            LOGGER.debug("Starting playing of track {}", track);
        }
    }

    public void nextTrack() {
        if (queue.size() > 0) {
            LOGGER.debug("Playing the next track");
            player.startTrack(queue.poll(), false);
        } else {
            LOGGER.debug("The queue is empty, playback has stopped");
            player.stopTrack();
        }
    }

    public void togglePauseTrack() {
        LOGGER.debug("Setting playback to {}", !player.isPaused());
        player.setPaused(!player.isPaused());
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        LOGGER.debug("Track {} has ended with reason {}", track, endReason);
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onEvent(AudioEvent event) {
        LOGGER.debug("Received event {}", event);
        super.onEvent(event);
    }

    public List<AudioTrack> getQueue() {
        List<AudioTrack> tracks = new ArrayList<>();
        queue.forEach(audioTrack -> tracks.add(audioTrack.makeClone()));
        return tracks;
    }
}
