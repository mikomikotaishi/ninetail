package bot.ninetail.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidVr;
import dev.lavalink.youtube.clients.Music;
import dev.lavalink.youtube.clients.Web;
import dev.lavalink.youtube.clients.WebEmbedded;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * Class to manage the bot audio.
 * This class is used to manage the bot audio.
 */
public class BotAudio {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(BotAudio.class.getName());

    /**
     * Map of guild IDs to bot audio instances.
     */
    @Nonnull
    private static final Map<Long, BotAudio> instances = new HashMap<>();

    /**
     * Timeout for inactivity in milliseconds (10 * 60 * 1000).
     */
    private static final long INACTIVITY_TIMEOUT = 600000;

    /**
     * The audio player manager.
     */
    @Nonnull
    private final AudioPlayerManager manager;

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
     * The audio manager.
     */
    @Nonnull
    private AudioManager audioManager;

    /**
     * The text channel.
     */
    @Nonnull
    private MessageChannel textChannel;

    /**
     * The voice channel.
     */
    @Nonnull
    private AudioChannel voiceChannel;

    /**
     * Whether the bot audio is activated.
     */
    private boolean activated = false;

    /**
     * The last active time.
     */
    private long lastActiveTime;

    /**
     * Scheduled executor service for inactivity checking.
     */
    private static ScheduledExecutorService inactivityChecker;

    static {
        inactivityChecker = Executors.newSingleThreadScheduledExecutor();
        inactivityChecker.scheduleAtFixedRate(() -> {
            for (BotAudio audio: instances.values()) {
                boolean shouldDisconnect = !audio.isPlaying() && (System.currentTimeMillis() - audio.lastActiveTime > INACTIVITY_TIMEOUT);
                if (audio.isActive() && shouldDisconnect) {
                    String channelName = audio.getVoiceChannel().getName();
                    String guildName = audio.getVoiceChannel().getGuild().getName();
                    String guildId = audio.getVoiceChannel().getGuild().getId();
                    
                    audio.disconnect("Auto-disconnected from %s of server %s (%s) due to 10 minutes of inactivity", 
                        channelName, guildName, guildId
                    );
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private BotAudio() {
        this.manager = new DefaultAudioPlayerManager();
        this.manager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // The default clients, with the YouTube Kids client added behind them to pick up the
        // videos that are marked as made for kids, which the others all refuse to serve.
        this.manager.registerSourceManager(new YoutubeAudioSourceManager(
            true, new Music(), new AndroidVr(), new AndroidKids(), new Web(), new WebEmbedded()
        ));
        this.manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        this.manager.registerSourceManager(new BandcampAudioSourceManager());
        this.manager.registerSourceManager(new TwitchStreamAudioSourceManager());
        // Registered last, as it accepts any remaining link and would otherwise take the links
        // belonging to the sources above.
        this.manager.registerSourceManager(new HttpAudioSourceManager());
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(this, player);
        player.addListener(scheduler);
        player.setVolume(100);
        this.lastActiveTime = System.currentTimeMillis();
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
     * Disconnects the bot from a guild's voice channel, if it is connected to one.
     * Unlike the other operations here, this does not create an instance for a guild that does
     * not already have one.
     *
     * @param guildId The guild ID to disconnect from.
     */
    public static void disconnectIfActive(long guildId) {
        BotAudio audio = instances.get(guildId);
        if (audio != null && audio.isActive()) {
            audio.disconnect("Disconnected from guild %s as the bot was disabled there", guildId);
        }
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
     * Checks if the bot audio is currently playing.
     * 
     * @return Whether the bot audio is playing.
     */
    public boolean isPlaying() {
        return player.getPlayingTrack() != null && !player.isPaused();
    }

    /**
     * Updates the last active time to the current time, marking it as active.
     */
    public void markActive() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * Activates the bot audio.
     */
    public void activate() {
        activated = true;
    }

    /**
     * Updates the last active timestamp when an audio command is used.
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * Disconnects the bot audio with a reason.
     *
     * @param reason The reason for disconnection.
     */
    public void disconnect(String reason) {
        LOGGER.log(System.Logger.Level.INFO, reason);
        disconnect();
    }

    /**
     * Disconnects the bot audio with a reason.
     *
     * @param reason The (unformatted) reason for disconnection.
     * @param args The arguments to format the reason with.
     */
    public void disconnect(String reason, Object... args) {
        LOGGER.log(System.Logger.Level.INFO, reason, args);
        disconnect();
    }

    /**
     * Disconnects the bot audio.
     */
    public void disconnect() {
        activated = false;
        scheduler.clear();
        player.stopTrack();
        if (audioManager != null) {
            audioManager.closeAudioConnection();
        }
        textChannel = null;
        voiceChannel = null;
    }
}
