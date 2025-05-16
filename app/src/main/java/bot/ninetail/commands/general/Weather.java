package bot.ninetail.commands.general;

import java.io.IOException;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonObject;

import bot.ninetail.clients.WeatherClient;
import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.ApiCommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve the weather for a given location.
 * 
 * @implements APICommand
 */
public final class Weather implements ApiCommand {
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
        Logger.log(LogLevel.INFO, String.format("Weather command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        String location = event.getOption("location").getAsString();
        try {
            Logger.log(LogLevel.INFO, String.format("Attempting to retrieve data for location: %s", location));
            JsonObject weatherData = weatherClient.getInfo(location);
            double temp = weatherData.getJsonNumber("temp").doubleValue();
            event.reply(String.format("Current temperature in %s: %.2f°C", location, temp)).queue();
        } catch (IllegalArgumentException e) {
            Logger.log(LogLevel.WARN, "Failed to execute weather command due to missing weather token.");
            event.reply("Weather command unavailable currently.").queue();
        } catch (IOException e) {
            Logger.log(LogLevel.WARN, String.format("Failed to retrieve weather data for location: %s.", location));
            event.reply(String.format("Error retrieving weather data for location: %s.", location)).queue();
        } catch (InterruptedException e) {
            Logger.log(LogLevel.WARN, String.format("Interrupted while retrieving posts for location: %s.", location));
            event.reply(String.format("Interrupted while retrieving posts for location: %s.", location)).queue();
        }
    }
}
