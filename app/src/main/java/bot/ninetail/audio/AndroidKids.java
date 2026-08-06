package bot.ninetail.audio;

import jakarta.annotation.Nonnull;

import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;

import dev.lavalink.youtube.clients.Android;
import dev.lavalink.youtube.clients.ClientConfig;
import dev.lavalink.youtube.clients.ClientConfig.AndroidVersion;
import dev.lavalink.youtube.clients.ClientOptions;

/**
 * The YouTube Kids Android client.
 *
 * @extends Android
 */
public class AndroidKids extends Android {
    /**
     * The version of the YouTube Kids app to identify as.
     */
    @Nonnull
    public static String CLIENT_VERSION = "7.36.1";

    /**
     * The Android version to identify as.
     */
    @Nonnull
    public static AndroidVersion ANDROID_VERSION = AndroidVersion.ANDROID_13;

    /**
     * The base client configuration.
     */
    @Nonnull
    public static ClientConfig BASE_CONFIG = new ClientConfig()
        .withUserAgent(String.format("com.google.android.apps.youtube.kids/%s (Linux; U; Android %s) gzip", CLIENT_VERSION, ANDROID_VERSION.getOsVersion()))
        .withClientName("ANDROID_KIDS")
        .withClientField("clientVersion", CLIENT_VERSION)
        .withClientField("androidSdkVersion", ANDROID_VERSION.getSdkVersion())
        .withUserField("lockedSafetyMode", false);

    /**
     * Creates a new YouTube Kids client that only loads and plays videos.
     */
    public AndroidKids() {
        this(videoOnlyOptions());
    }

    /**
     * Creates a new YouTube Kids client with the given options.
     *
     * @param options The client options.
     */
    public AndroidKids(@Nonnull ClientOptions options) {
        // The boolean suppresses the "ANDROID is broken" warning that the parent client logs,
        // which does not apply to this client.
        super(options, false);
    }

    /**
     * Builds the options that restrict this client to video loading and playback.
     *
     * @return The client options.
     */
    @Nonnull
    private static ClientOptions videoOnlyOptions() {
        ClientOptions options = new ClientOptions();
        options.setSearching(false);
        options.setPlaylistLoading(false);
        return options;
    }

    @Override
    @Nonnull
    protected ClientConfig getBaseClientConfig(@Nonnull HttpInterface httpInterface) {
        return BASE_CONFIG.copy();
    }

    @Override
    @Nonnull
    public String getIdentifier() {
        return BASE_CONFIG.getName();
    }
}
