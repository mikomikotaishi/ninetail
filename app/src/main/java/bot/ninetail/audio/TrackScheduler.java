package bot.ninetail.audio;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.LinkedList;
import java.util.Queue;

import jakarta.annotation.Nonnull;

import bot.ninetail.util.TemporalFormatting;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

/**
 * Class to manage the audio track queue.
 * This class is used to manage the audio track queue.
 * 
 * @extends AudioEventAdapter
 */
public class TrackScheduler extends AudioEventAdapter {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(TrackScheduler.class.getName());

    /**
     * The bot audio instance.
     */
    @Nonnull
    private final BotAudio botAudio;

    /**
     * The audio player.
     */
    @Nonnull
    private final AudioPlayer player;

    /**
     * The queue of audio tracks.
     */
    @Nonnull
    private final Queue<AudioTrack> queue;

    /**
     * Sends a message to the text channel indicating the currently playing track.
     *
     * @param track The track that is currently playing.
     */
    private void sendNowPlayingMessage(@Nonnull AudioTrack track) {
        String trackInfo = String.format("%s (%s)", track.getInfo().title, TemporalFormatting.getFormattedTime(track.getInfo().length));
        botAudio.getTextChannel().sendMessage(String.format("Now playing: **%s**", trackInfo)).queue();
    }

    /**
     * Creates a new track scheduler.
     *
     * @param botAudio The bot audio instance.
     * @param player The audio player.
     */
    public TrackScheduler(@Nonnull BotAudio botAudio, @Nonnull AudioPlayer player) {
        this.botAudio = botAudio;
        this.player = player;
        this.queue = new LinkedList<>();
    }

    /**
     * Queues an audio track.
     *
     * @param track The track to queue.
     */
    public void queue(@Nonnull AudioTrack track) {
        String trackInfo = String.format("%s (%s)", track.getInfo().title, TemporalFormatting.getFormattedTime(track.getInfo().length));
        if (!player.startTrack(track, true)) {
            LOGGER.log(Level.INFO, "Queued track: {0}", track.getInfo().title);
            queue.offer(track);
            botAudio.getTextChannel().sendMessage(String.format("Queued track: **%s**", trackInfo)).queue();
        } else {
            LOGGER.log(Level.INFO, "Now playing: {0}", track.getInfo().title);
            botAudio.markActive();
            sendNowPlayingMessage(track);
        }
    }

    /**
     * Begins playing the next track in the queue.
     *
     * @param player The audio player.
     * @param track The track that just ended.
     * @param endReason The reason the track ended.
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            AudioTrack nextTrack = queue.poll();
            if (nextTrack != null) {
                LOGGER.log(Level.INFO, "Current song ended. Beginning next song...");
                player.startTrack(nextTrack, false);
                botAudio.markActive();
                sendNowPlayingMessage(nextTrack);
            } else {
                LOGGER.log(Level.INFO, "Song queue empty.");
                botAudio.getTextChannel().sendMessage("Queue is empty. Use `/play` to queue new songs.").queue();
            }
        }
    }

    /**
     * Skips to the next track in the queue.
     */
    public void skip() {
        AudioTrack nextTrack = queue.poll();
        if (nextTrack != null) {
            LOGGER.log(Level.INFO, "Skipping to next track...");
            player.startTrack(nextTrack, false);
        } else {
            LOGGER.log(Level.INFO, "No more tracks to skip.");
            player.stopTrack();
        }
    }

    /**
     * Clears the queue.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Gets the queue of audio tracks.
     *
     * @return The queue of audio tracks.
     */
    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    /**
     * Handles an automatic bot disconnection.
     */
    public void autoDisconnect() {
        botAudio.getTextChannel().sendMessage("Disconnecting from voice channel due to 10 minutes of inactivity.").queue();
    }
}