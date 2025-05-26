package bot.ninetail.audio;

import java.nio.ByteBuffer;

import jakarta.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * Class to handle sending audio to Discord.
 * This class is used to handle sending audio to Discord.
 * 
 * @implements AudioSendHandler
 */
public class AudioPlayerSendHandler implements AudioSendHandler {
    /**
     * The audio player.
     */
    @Nonnull
    private final AudioPlayer audioPlayer;

    /**
     * The last frame.
     */
    @Nonnull
    private AudioFrame lastFrame;

    /**
     * Creates a new audio player send handler.
     *
     * @param audioPlayer The audio player.
     */
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    /**
     * Determines whether the audio player can provide audio.
     *
     * @return Whether the audio player can provide audio.
     */
    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    /**
     * Provides 20ms of audio.
     *
     * @return The audio.
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    /**
     * Determines whether the audio player is using Opus.
     *
     * @return Whether the audio player is using Opus.
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}
