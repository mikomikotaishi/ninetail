package bot.ninetail.audio;

import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import dev.lavalink.youtube.YoutubeAudioSourceManager;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * Class to manage the bot audio.
 * This class is used to manage the bot audio.
 */
public class BotAudio {
    /**
     * Map of guild IDs to bot audio instances.
     */
    private static final Map<Long, BotAudio> instances = new HashMap<>();

    /**
     * The audio player manager.
     */
    private final AudioPlayerManager manager;

    /**
     * The audio player.
     */
    private final AudioPlayer player;

    /**
     * The track scheduler.
     */
    private final TrackScheduler scheduler;

    /**
     * The audio manager.
     */
    private AudioManager audioManager;

    /**
     * The text channel.
     */
    private MessageChannel textChannel;

    /**
     * The voice channel.
     */
    private AudioChannel voiceChannel;

    /**
     * Whether the bot audio is activated.
     */
    private boolean activated = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private BotAudio() {
        this.manager = new DefaultAudioPlayerManager();
        this.manager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        this.manager.registerSourceManager(new YoutubeAudioSourceManager(true));
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(this, player);
        player.addListener(scheduler);
        player.setVolume(100);
    }

    /**
     * Gets the bot audio instance for a guild.
     *
     * @param guildId The guild ID.
     * @return The bot audio instance.
     */
    public static BotAudio getInstance(long guildId) {
        return instances.computeIfAbsent(guildId, k -> new BotAudio());
    }

    /**
     * Sets the audio manager.
     *
     * @param audioManager The audio manager.
     */
    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    /**
     * Sets the text channel.
     *
     * @param textChannel The text channel.
     */
    public void setTextChannel(MessageChannel textChannel) {
        this.textChannel = textChannel;
    }

    /**
     * Sets the voice channel.
     *
     * @param voiceChannel The voice channel.
     */
    public void setVoiceChannel(AudioChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    /**
     * Gets the audio player.
     *
     * @return The audio player.
     */
    public AudioPlayer getAudioPlayer() {
        return player;
    }

    /**
     * Gets the audio player manager.
     *
     * @return The audio player manager.
     */
    public AudioPlayerManager getAudioPlayerManager() {
        return manager;
    }

    /**
     * Gets the track scheduler.
     *
     * @return The track scheduler.
     */
    public TrackScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Gets the text channel.
     *
     * @return The text channel.
     */
    public MessageChannel getTextChannel() {
        return textChannel;
    }

    /**
     * Gets the voice channel.
     *
     * @return The voice channel.
     */
    public AudioChannel getVoiceChannel() {
        return voiceChannel;
    }

    /**
     * Checks if the bot audio is active.
     *
     * @return Whether the bot audio is active.
     */
    public boolean isActive() {
        return activated;
    }

    /**
     * Activates the bot audio.
     */
    public void activate() {
        activated = true;
    }

    /**
     * Disconnects the bot audio.
     */
    public void disconnect() {
        activated = false;
        scheduler.clear();
        player.stopTrack();
        if (audioManager != null)
            audioManager.closeAudioConnection();
        textChannel = null;
        voiceChannel = null;
    }
}