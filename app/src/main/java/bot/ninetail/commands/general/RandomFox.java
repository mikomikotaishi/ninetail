package bot.ninetail.commands.general;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.RandomFoxClient;
import bot.ninetail.structures.commands.APICommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve a random fox image.
 *
 * @implements APICommand
 */
public final class RandomFox implements APICommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private RandomFox() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        try {
            RandomFoxClient foxClient = new RandomFoxClient();
            JsonArray imageData = foxClient.getImage();
            
            if (!imageData.isEmpty()) {
                JsonObject imageObject = imageData.getJsonObject(0);
                String imageUrl = imageObject.getString("image");
                event.reply("Here's a random fox for you: " + imageUrl).queue();
            } else {
                event.reply("Could not retrieve a fox image at this time.").queue();
            }
        } catch (IOException e) {
            System.err.println("An error occured while retrieving random fox image.");
            event.reply("An error occurred while fetching a fox image.").queue();
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while retrieving random fox image.");
            event.reply("Interrupted while retrieving random fox image.").queue();
        }
    }
}
