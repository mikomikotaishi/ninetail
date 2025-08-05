package bot.ninetail.commands.general;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;
import jakarta.json.JsonObject;

import bot.ninetail.clients.WeatherClient;
import bot.ninetail.structures.commands.ApiCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to retrieve the weather for a given location.
 * 
 * @implements ApiCommand
 */
@UtilityClass
public final class Weather implements ApiCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(Weather.class.getName());

    /**
     * The weather client.
     */
    @Nonnull
    private static final WeatherClient weatherClient = new WeatherClient();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Weather command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        String location = event.getOption("location").getAsString();
        try {
            LOGGER.log(Level.INFO, "Attempting to retrieve data for location: {0}", location);
            JsonObject weatherData = weatherClient.getInfo(location);
            double temp = weatherData.getJsonNumber("temp").doubleValue();
            event.reply(String.format("Current temperature in %s: %.2f°C", location, temp)).queue();
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Failed to execute weather command due to missing weather token.");
            event.reply("Weather command unavailable currently.").queue();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve weather data for location: {0}.", location);
            event.reply(String.format("Error retrieving weather data for location: %s.", location)).queue();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while retrieving posts for location: {0}.", location);
            event.reply(String.format("Interrupted while retrieving posts for location: %s.", location)).queue();
        }
    }
}
