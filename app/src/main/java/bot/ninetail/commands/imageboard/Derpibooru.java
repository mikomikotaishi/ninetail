package bot.ninetail.commands.imageboard;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.DerpibooruClient;
import bot.ninetail.structures.commands.ApiCommand;
import bot.ninetail.util.RandomNumberGenerator;
import bot.ninetail.util.TextFormat;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to retrieve an image from Derpibooru.
 * 
 * @implements ApiCommand
 */
@UtilityClass
public final class Derpibooru implements ApiCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Derpibooru.class.getName());

    /**
     * The Derpibooru client.
     */
    @Nonnull
    private static final DerpibooruClient derpibooruClient = new DerpibooruClient();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Derpibooru command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        if (derpibooruClient.getApiKey() == null) {
            LOGGER.log(Level.WARNING, "Failed to invoke Derpibooru command due to missing API token.");
            event.reply("Sorry, Derpibooru API token was not provided, I cannot retrieve anything.").queue();
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
            LOGGER.log(Level.INFO, "Attempting to retrieve images for tags: {0}", tagsDisplay);
            JsonArray images = derpibooruClient.getPosts(tagsArray);
            if (images.isEmpty()) {
                event.reply(String.format("No images found for tags: %s.", 
                    TextFormat.markdownVerbatim(tagsDisplay))
                ).queue();
                return;
            }

            int randomIndex = RandomNumberGenerator.generateRandomNumber(images.size());
            JsonObject image = images.getJsonObject(randomIndex);

            if (!image.containsKey("view_url") || image.isNull("view_url")) {
                event.reply("The selected image does not have a valid URL. Try again.").queue();
                return;
            }

            String imageUrl = image.getString("view_url");
            event.reply(imageUrl).queue();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve images for tags: {0}", tagsDisplay);
            event.reply(String.format("Error retrieving images for tags: %s.", 
                TextFormat.markdownVerbatim(tagsDisplay))
            ).queue();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while retrieving images for tags: {0}", tagsDisplay);
            event.reply(String.format("Interrupted while retrieving images for tags: %s.", 
                TextFormat.markdownVerbatim(tagsDisplay))
            ).queue();
        }
    }
}
