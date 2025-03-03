package bot.ninetail.audio;

import java.util.LinkedList;
import java.util.Queue;

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
    private final BotAudio botAudio;

    /**
     * The audio player.
     */
    private final AudioPlayer player;

    /**
     * The queue of audio tracks.
     */
    private final Queue<AudioTrack> queue;

    /**
     * Sends a message to the text channel indicating the currently playing track.
     *
     * @param track The track that is currently playing.
     */
    private void sendNowPlayingMessage(AudioTrack track) {
        String trackInfo = String.format("%s (%s)", track.getInfo().title, Temporal.getFormattedTime(track.getInfo().length));
        MessageChannel textChannel = botAudio.getTextChannel();
        if (textChannel != null)
            textChannel.sendMessage(String.format("Now playing: **%s**", trackInfo)).queue();
    }

    /**
     * Creates a new track scheduler.
     *
     * @param botAudio The bot audio instance.
     * @param player The audio player.
     */
    public TrackScheduler(BotAudio botAudio, AudioPlayer player) {
        this.botAudio = botAudio;
        this.player = player;
        this.queue = new LinkedList<>();
    }

    /**
     * Queues an audio track.
     *
     * @param track The track to queue.
     */
    public void queue(AudioTrack track) {
        String trackInfo = String.format("%s (%s)", track.getInfo().title, Temporal.getFormattedTime(track.getInfo().length));
        if (!player.startTrack(track, true)) {
            System.out.println(String.format("Queued track: %s", track.getInfo().title));
            MessageChannel textChannel = botAudio.getTextChannel();
            queue.offer(track);
            if (textChannel != null)
                textChannel.sendMessage(String.format("Queued track: **%s**", trackInfo)).queue();
        } else {
            System.out.println("Now playing: " + track.getInfo().title);
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
                System.out.println("Current song ended. Beginning next song...");
                player.startTrack(nextTrack, false);
                sendNowPlayingMessage(nextTrack);
            } else {
                System.out.println("Song queue empty.");
                MessageChannel textChannel = botAudio.getTextChannel();
                if (textChannel != null)
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
            System.out.println("Skipping to next track...");
            player.startTrack(nextTrack, false);
        } else {
            System.out.println("No more tracks to skip.");
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
}