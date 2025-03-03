package bot.ninetail.commands.general;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonObject;

import bot.ninetail.clients.WeatherClient;
import bot.ninetail.structures.commands.APICommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve the weather for a given location.
 * 
 * @implements APICommand
 */
public final class Weather implements APICommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Weather() {}

    /**
     * The weather client.
     */
    private static final WeatherClient weatherClient = new WeatherClient();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        System.out.println("Weather command invoked.");
        String location = event.getOption("location").getAsString();
        try {
            System.out.println(String.format("Attempting to retrieve data for location: %s", location));
            JsonObject weatherData = weatherClient.getInfo(location);
            double temp = weatherData.getJsonNumber("temp").doubleValue();
            event.reply(String.format("Current temperature in %s: %.2f°C", location, temp)).queue();
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to execute weather command due to missing weather token.");
            event.reply("Weather command unavailable currently.").queue();
        } catch (IOException e) {
            System.err.println(String.format("Failed to retrieve weather data for location: %s.", location));
            event.reply(String.format("Error retrieving weather data for location: %s.", location)).queue();
        } catch (InterruptedException e) {
            System.err.println(String.format("Interrupted while retrieving posts for location: %s.", location));
            event.reply(String.format("Interrupted while retrieving posts for location: %s.", location)).queue();
        }
    }
}
