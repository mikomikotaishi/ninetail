package bot.ninetail.commands.imageboard;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.GyateBooruClient;
import bot.ninetail.core.logger.*;
import bot.ninetail.structures.commands.ApiCommand;
import bot.ninetail.util.RandomNumberGenerator;
import bot.ninetail.util.TextFormat;
import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve an image from Gyate Booru.
 * 
 * @implements ApiCommand
 */
@UtilityClass
public final class GyateBooru implements ApiCommand {
    /**
     * The Gyate Booru client.
     */
    @Nonnull
    private static final GyateBooruClient gyatebooruClient = new GyateBooruClient();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Gyate Booru command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        if (gyatebooruClient.getApiKey() == null) {
            Logger.log(LogLevel.WARN, "Failed to invoke Gyate Booru command due to missing API token.");
            event.reply("Sorry, Gyate Booru API token was not provided, I cannot retrieve anything.").queue();
            return;
        }
        String tag1 = event.getOption("tag1").getAsString();
        String tag2 = event.getOption("tag2") != null ? event.getOption("tag2").getAsString() : null;
        try {
            Logger.log(LogLevel.INFO, "Attempting to retrieve posts for tags: %s%s", tag1, (tag2 != null ? ", " + tag2 : ""));
            JsonArray posts = gyatebooruClient.getPosts(tag1, tag2);
            if (posts.isEmpty()) {
                event.reply(String.format("No posts found for tags: %s%s.", 
                    TextFormat.markdownVerbatim(tag1), 
                    tag2 != null ? " and " + TextFormat.markdownVerbatim(tag2) : "")
                ).queue();
                return;
            }
            int randomIndex = RandomNumberGenerator.generateRandomNumber(posts.size());
            JsonObject post = posts.getJsonObject(randomIndex);
            if (!post.containsKey("file_url")) {
                event.reply("The selected post does not have a valid image URL. Try again.").queue();
                return;
            }
            String imageUrl = post.getString("file_url");
            event.reply(imageUrl).queue();
        } catch (IOException e) {
            Logger.log(LogLevel.WARN, "Failed to retrieve posts for tags: %s%s", tag1, (tag2 != null ? ", " + tag2 : ""));
            event.reply(String.format("Error retrieving posts for tags: %s%s.", 
                TextFormat.markdownVerbatim(tag1), 
                tag2 != null ? " and " + TextFormat.markdownVerbatim(tag2) : "")
            ).queue();
        } catch (InterruptedException e) {
            Logger.log(LogLevel.WARN, "Interrupted while retrieving posts for tags: %s%s", tag1, (tag2 != null ? ", " + tag2 : ""));
            event.reply(String.format("Interrupted while retrieving posts for tags: %s%s.", 
                TextFormat.markdownVerbatim(tag1), 
                tag2 != null ? " and " + TextFormat.markdownVerbatim(tag2) : "")
            ).queue();
        }
    }
}
