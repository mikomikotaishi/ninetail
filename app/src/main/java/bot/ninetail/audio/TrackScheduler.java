package bot.ninetail.audio;

import java.util.LinkedList;
import java.util.Queue;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.utilities.Temporal;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class to manage the audio track queue.
 * This class is used to manage the audio track queue.
 * 
 * @extends AudioEventAdapter
 */
public class TrackScheduler extends AudioEventAdapter {
    /**
     * The bot audio instance.
     */
    private final @Nonnull BotAudio botAudio;

    /**
     * The audio player.
     */
    private final @Nonnull AudioPlayer player;

    /**
     * The queue of audio tracks.
     */
    private final @Nonnull Queue<AudioTrack> queue;

    /**
     * Sends a message to the text channel indicating the currently playing track.
     *
     * @param track The track that is currently playing.
     */
    private void sendNowPlayingMessage(@Nonnull AudioTrack track) {
        String trackInfo = String.format("%s (%s)", track.getInfo().title, Temporal.getFormattedTime(track.getInfo().length));
        @Nonnull MessageChannel textChannel = botAudio.getTextChannel();
        textChannel.sendMessage(String.format("Now playing: **%s**", trackInfo)).queue();
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
        String trackInfo = String.format("%s (%s)", track.getInfo().title, Temporal.getFormattedTime(track.getInfo().length));
        if (!player.startTrack(track, true)) {
            Logger.log(LogLevel.INFO, String.format("Queued track: %s", track.getInfo().title));
            @Nonnull MessageChannel textChannel = botAudio.getTextChannel();
            queue.offer(track);
            textChannel.sendMessage(String.format("Queued track: **%s**", trackInfo)).queue();
        } else {
            Logger.log(LogLevel.INFO, "Now playing: " + track.getInfo().title);
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
                Logger.log(LogLevel.INFO, "Current song ended. Beginning next song...");
                player.startTrack(nextTrack, false);
                sendNowPlayingMessage(nextTrack);
            } else {
                Logger.log(LogLevel.INFO, "Song queue empty.");
                @Nonnull MessageChannel textChannel = botAudio.getTextChannel();
                textChannel.sendMessage("Queue is empty. Use `/play` to queue new songs.").queue();
            }
        }
    }

    /**
     * Skips to the next track in the queue.
     */
    public void skip() {
        AudioTrack nextTrack = queue.poll();
        if (nextTrack != null) {
            Logger.log(LogLevel.INFO, "Skipping to next track...");
            player.startTrack(nextTrack, false);
        } else {
            Logger.log(LogLevel.INFO, "No more tracks to skip.");
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
        @Nonnull MessageChannel textChannel = botAudio.getTextChannel();
        textChannel.sendMessage("Disconnecting from voice channel due to 10 minutes of inactivity.").queue();
    }
}