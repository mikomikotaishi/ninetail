package bot.ninetail.commands.imageboard;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.Rule34Client;
import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.ApiCommand;
import bot.ninetail.utilities.RandomNumberGenerator;
import bot.ninetail.utilities.TextFormat;
import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve an image from Rule34.
 * 
 * @implements ApiCommand
 */
@UtilityClass
public final class Rule34 implements ApiCommand {
    /**
     * The Rule34 client.
     */
    @Nonnull
    private static final Rule34Client rule34Client = new Rule34Client();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, "Danbooru command invoked by %s (%s) of guild %s (%s)", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        if (rule34Client.getApiKey() == null) {
            Logger.log(LogLevel.WARN, "Failed to invoke Rule34 command due to missing API token.");
            event.reply("Sorry, Rule34 API token was not provided, I cannot retrieve anything.").queue();
            return;
        }
        String tag1 = event.getOption("tag1").getAsString();
        String tag2 = event.getOption("tag2") != null ? event.getOption("tag2").getAsString() : null;
        try {
            Logger.log(LogLevel.INFO, "Attempting to retrieve posts for tags: %s%s", tag1, (tag2 != null ? ", " + tag2 : ""));
            JsonArray posts = rule34Client.getPosts(tag1, tag2);
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