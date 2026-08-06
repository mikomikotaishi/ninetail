package bot.ninetail.audio;

import java.util.List;
import java.util.regex.Pattern;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Utility class for turning a user-supplied query into an identifier that the audio player
 * manager can resolve.
 */
@UtilityClass
public final class AudioIdentifier {
    /**
     * Matches the host of a YouTube Kids link.
     */
    @Nonnull
    private static final Pattern YOUTUBE_KIDS_HOST = Pattern.compile(
        "^(?:https?://)?(?:www\\.|m\\.)?youtubekids\\.com/", Pattern.CASE_INSENSITIVE
    );

    /**
     * The host that YouTube Kids links are rewritten to.
     */
    @Nonnull
    private static final String YOUTUBE_HOST = "https://www.youtube.com/";

    /**
     * The prefixes that let a user pick which platform to search, rather than the default one.
     */
    @Nonnull
    private static final List<String> SEARCH_PREFIXES = List.of(
        "ytsearch:",    // YouTube
        "ytmsearch:",   // YouTube Music
        "scsearch:"     // SoundCloud
        // Bandcamp has a "bcsearch:" prefix of its own, but its search no longer returns
        // anything, so it is left out. Bandcamp links are unaffected.
    );

    /**
     * The prefix used to search when the user does not ask for a particular platform.
     */
    @Nonnull
    private static final String DEFAULT_SEARCH_PREFIX = "ytsearch:";

    /**
     * Converts a query into an identifier to load.
     * Links are used as they are, a query that names a platform to search is kept, and anything
     * else is treated as a search on the default platform.
     *
     * @param query The query supplied by the user.
     * @return The identifier to load.
     */
    @Nonnull
    public static String fromQuery(@Nonnull String query) {
        if (query.startsWith("http")) {
            return normalizeUrl(query);
        }

        for (String prefix: SEARCH_PREFIXES) {
            if (query.regionMatches(true, 0, prefix, 0, prefix.length())) {
                // Rebuilt from the known prefix so that it is matched regardless of how the user
                // capitalized it.
                return String.format("%s%s", prefix, query.substring(prefix.length()));
            }
        }

        return String.format("%s%s", DEFAULT_SEARCH_PREFIX, query);
    }

    /**
     * Rewrites links that the YouTube source cannot recognize into their equivalent on the main
     * YouTube domain.
     *
     * YouTube Kids serves ordinary YouTube videos under a domain of its own, which the source
     * does not match against, so those links would otherwize resolve to nothing at all.
     *
     * @param url The URL supplied by the user.
     * @return The equivalent URL on a recognized domain, or the original URL if no rewrite applies.
     */
    @Nonnull
    private static String normalizeUrl(@Nonnull String url) {
        return YOUTUBE_KIDS_HOST.matcher(url).replaceFirst(YOUTUBE_HOST);
    }
}
