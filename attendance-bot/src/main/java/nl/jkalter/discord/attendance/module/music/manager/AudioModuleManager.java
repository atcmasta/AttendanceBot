package nl.jkalter.discord.attendance.module.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.author.IAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AudioModuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioModuleManager.class);

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private final TrackScheduler trackScheduler;
    private final TrackResultScheduler trackResultScheduler;
    private final AudioProvider provider;
    private final AudioPlayer player;
    private VoiceConnection connection;

    public AudioModuleManager() {
        // This is an optimization strategy that Discord4J can utilize.
        // It is not important to understand
        playerManager.getConfiguration()
                .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);

        // Create an AudioPlayer so Discord4J can receive audio data
        player = playerManager.createPlayer();

        // The track scheduler
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);

        // The handler for track results
        trackResultScheduler = new TrackResultScheduler(trackScheduler);

        // We will be creating LavaPlayerAudioProvider in the next step
        provider = new LavaPlayerAudioProvider(player);
    }

    public void loadItem(String url) {
        LOGGER.debug("Loading url {}", url);
        playerManager.loadItem(url, trackResultScheduler);
    }

    public void joinVoice(IMessageCreateEventFacade event) {
        final IAuthor member = event.getAuthor();
        if (member != null) {
            final VoiceState voiceState = member.getVoiceState();
            if (voiceState != null) {
                LOGGER.debug("Found a voice state {}", voiceState);
                final VoiceChannel channel = voiceState.getChannel().block();
                if (channel != null && connection == null) {
                    LOGGER.debug("Joining voice channel {}", channel);

                    // join returns a VoiceConnection which would be required if we were
                    // adding disconnection features, but for now we are just ignoring it.
                    connection = channel.join(spec -> spec.setProvider(provider)).block();
                }
            }
        }
    }

    public List<AudioTrack> getQueue() {
        LOGGER.debug("Returning the queue");
        return trackScheduler.getQueue();
    }

    public void playNextItem() {
        LOGGER.debug("Playing next item");
        trackScheduler.nextTrack();
    }

    public void togglePauseItem() {
        LOGGER.debug("Toggling playback pause");
        trackScheduler.togglePauseTrack();
    }

    public void leaveVoice() {
        LOGGER.debug("Disconnecting the voice connection.");
        connection.disconnect();
        connection = null;
    }
}
