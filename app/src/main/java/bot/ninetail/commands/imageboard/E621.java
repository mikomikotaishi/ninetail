package bot.ninetail.commands.imageboard;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.E621Client;
import bot.ninetail.structures.commands.ApiCommand;
import bot.ninetail.util.RandomNumberGenerator;
import bot.ninetail.util.TextFormat;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Command to retrieve an image from e621.
 * 
 * @implements ApiCommand
 */
@UtilityClass
public final class E621 implements ApiCommand {
    @Nonnull
    private static final System.Logger LOGGER = System.getLogger(E621.class.getName());

    /**
     * The e621 client.
     */
    @Nonnull
    private static final E621Client e621Client = new E621Client();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(System.Logger.Level.INFO, "e621 command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        if (e621Client.getApiKey() == null) {
            LOGGER.log(System.Logger.Level.WARNING, "Failed to invoke e621 command due to missing API token.");
            event.reply("Sorry, e621 API token was not provided, I cannot retrieve anything.").queue();
            return;
        }

        java.util.List<String> tagsList = new ArrayList<>();
        for (int i = 1; i <= 6; ++i) {
            OptionMapping option = event.getOption("tag" + i);
            if (option != null) {
                tagsList.add(option.getAsString());
            }
        }
        
        String[] tagsArray = tagsList.toArray(new String[0]);
        String tagsDisplay = String.join(", ", tagsList);

        try {
            LOGGER.log(System.Logger.Level.INFO, "Attempting to retrieve posts for tags: {0}", tagsDisplay);
            JsonArray posts = e621Client.getPosts(tagsArray);
            if (posts.isEmpty()) {
                event.reply(String.format("No posts found for tags: %s.", 
                    TextFormat.markdownVerbatim(tagsDisplay))
                ).queue();
                return;
            }
            int randomIndex = RandomNumberGenerator.generateRandomNumber(posts.size());
            JsonObject post = posts.getJsonObject(randomIndex);

            if (!post.containsKey("file") || post.isNull("file")) {
                event.reply("The selected post does not have a valid file object. Try again.").queue();
                return;
            }

            JsonObject file = post.getJsonObject("file");
            if (!file.containsKey("url") || file.isNull("url")) {
                event.reply("The selected post does not have a valid image URL. Try again.").queue();
                return;
            }

            String imageUrl = file.getString("url");
            event.reply(imageUrl).queue();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.WARNING, "Failed to retrieve posts for tags: {0}", tagsDisplay);
            event.reply(String.format("Error retrieving posts for tags: %s.", 
                TextFormat.markdownVerbatim(tagsDisplay))
            ).queue();
        } catch (InterruptedException e) {
            LOGGER.log(System.Logger.Level.WARNING, "Interrupted while retrieving posts for tags: {0}", tagsDisplay);
            event.reply(String.format("Interrupted while retrieving posts for tags: %s.", 
                TextFormat.markdownVerbatim(tagsDisplay))
            ).queue();
        }
    }
}