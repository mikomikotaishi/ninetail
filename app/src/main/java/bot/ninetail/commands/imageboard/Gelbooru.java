package bot.ninetail.commands.imageboard;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.GelbooruClient;
import bot.ninetail.structures.commands.ApiCommand;
import bot.ninetail.util.RandomNumberGenerator;
import bot.ninetail.util.TextFormat;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to retrieve an image from Gelbooru.
 * 
 * @implements ApiCommand
 */
@UtilityClass
public final class Gelbooru implements ApiCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Gelbooru.class.getName());

    /**
     * The Gelbooru client.
     */
    @Nonnull
    private static final GelbooruClient gelbooruClient = new GelbooruClient();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Gelbooru command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        if (gelbooruClient.getApiKey() == null) {
            LOGGER.log(Level.WARNING, "Failed to invoke Gelbooru command due to missing API token.");
            event.reply("Sorry, Gelbooru API token was not provided, I cannot retrieve anything.").queue();
            return;
        }

        java.util.List<String> tagsList = new ArrayList<>();
        for (int i = 1; i <= 25; ++i) {
            OptionMapping option = event.getOption("tag" + i);
            if (option != null) {
                tagsList.add(option.getAsString());
            }
        }
        
        String[] tagsArray = tagsList.toArray(new String[0]);
        String tagsDisplay = String.join(", ", tagsList);
        
        try {
            LOGGER.log(Level.INFO, "Attempting to retrieve posts for tags: {0}", tagsDisplay);
            JsonArray posts = gelbooruClient.getPosts(tagsArray);
            if (posts.isEmpty()) {
                event.reply(String.format("No posts found for tags: %s.", 
                    TextFormat.markdownVerbatim(tagsDisplay))
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
            LOGGER.log(Level.WARNING, "Failed to retrieve posts for tags: {0}", tagsDisplay);
            event.reply(String.format("Error retrieving posts for tags: %s.", 
                TextFormat.markdownVerbatim(tagsDisplay))
            ).queue();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while retrieving posts for tags: {0}", tagsDisplay);
            event.reply(String.format("Interrupted while retrieving posts for tags: %s.", 
                TextFormat.markdownVerbatim(tagsDisplay))
            ).queue();
        }
    }
}
