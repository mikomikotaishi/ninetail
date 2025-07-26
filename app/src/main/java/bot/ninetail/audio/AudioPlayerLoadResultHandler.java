package bot.ninetail.audio;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * Class to handle loading audio player results.
 * This class is used to handle loading audio player results.
 * 
 * @implements AudioLoadResultHandler
 */
public class AudioPlayerLoadResultHandler implements AudioLoadResultHandler {
    /**
     * The text channel.
     */
    @Nonnull
    private final MessageChannel textChannel;

    /**
     * The audio player.
     */
    @Nonnull
    private final AudioPlayer player;

    /**
     * The track scheduler.
     */
    @Nonnull
    private final TrackScheduler scheduler;

    /**
     * Creates a new audio player load result handler.
     *
     * @param textChannel The text channel.
     * @param player The audio player.
     * @param scheduler The track scheduler.
     */
    public AudioPlayerLoadResultHandler(@Nonnull MessageChannel textChannel, @Nonnull AudioPlayer player, @Nonnull TrackScheduler scheduler) {
        this.textChannel = textChannel;
        this.player = player;
        this.scheduler = scheduler;
    }

    /**
     * Handles a loaded audio track.
     *
     * @param track The loaded audio track.
     */
    @Override
    public void trackLoaded(@Nonnull AudioTrack track) {
        Logger.log(LogLevel.INFO, "Loading track: %s", track.getInfo().title);
        textChannel.sendMessage(String.format("Added to queue: **%s**", track.getInfo().title)).queue();
        scheduler.queue(track);
    }

    /**
     * Handles a loaded audio playlist.
     *
     * @param playlist The loaded audio playlist.
     */
    @Override
    public void playlistLoaded(@Nonnull AudioPlaylist playlist) {
        Logger.log(LogLevel.INFO, "Loading playlist: %s", playlist.getName());
        if (playlist.isSearchResult()) {
            AudioTrack firstTrack = playlist.getTracks().get(0);
            textChannel.sendMessage(String.format("Added to queue: **%s**", firstTrack.getInfo().title)).queue();
            scheduler.queue(firstTrack);
        } else {
            for (AudioTrack track: playlist.getTracks()) {
                scheduler.queue(track);
            }
            textChannel.sendMessage(String.format("Added **%d** tracks to the queue.", playlist.getTracks().size())).queue();
        }
    }

    /**
     * Handles no matches found.
     */
    @Override
    public void noMatches() {
        textChannel.sendMessage("No matches found!").queue();
    }

    /**
     * Handles loading failed.
     *
     * @param exception The exception that occurred.
     */
    @Override
    public void loadFailed(FriendlyException exception) {
        textChannel.sendMessage("Failed to load track: " + exception.getMessage()).queue();
    }
}