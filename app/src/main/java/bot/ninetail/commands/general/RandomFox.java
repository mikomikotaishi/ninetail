package bot.ninetail.commands.general;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.RandomFoxClient;
import bot.ninetail.structures.commands.ApiCommand;
import bot.ninetail.structures.commands.ContentResponder;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve a random fox image.
 *
 * @extends ContentResponder
 * @implements ApiCommand
 */
@UtilityClass
public final class RandomFox extends ContentResponder implements ApiCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(RandomFox.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Random fox image command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        try {
            RandomFoxClient randomFoxClient = new RandomFoxClient();
            JsonArray imageData = randomFoxClient.getImage();
            
            if (!imageData.isEmpty()) {
                JsonObject imageObject = imageData.getJsonObject(0);
                String imageUrl = imageObject.getString("image");
                event.reply("Here's a random fox for you: " + imageUrl).queue();
            } else {
                event.reply("Could not retrieve a fox image at this time.").queue();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "An error occurred while retrieving random fox image: {0}", e.getMessage());
            event.reply("An error occurred while fetching a fox image.").queue();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while retrieving random fox image.");
            event.reply("Interrupted while retrieving random fox image.").queue();
        }
    }
}
