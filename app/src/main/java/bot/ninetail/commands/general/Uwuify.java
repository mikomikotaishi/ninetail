package bot.ninetail.commands.general;

import java.io.IOException;

import bot.ninetail.clients.UwuifyClient;
import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.APICommand;

import jakarta.annotation.Nonnull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to uwuify text.
 * 
 * @implements APICommand
 */
public final class Uwuify implements APICommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private Uwuify() {}

    /**
     * The Uwuify client.
     */
    private static final UwuifyClient uwuifyClient = new UwuifyClient();

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Uwuify command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        if (uwuifyClient.getApiKey() == null) {
            Logger.log(LogLevel.INFO, "Failed to invoke Uwuify command due to missing API token.");
            event.reply("Sorry, the Uwuify API token was not provided. I cannot uwuify your text.").queue();
            return;
        }

        String text = event.getOption("text").getAsString();
        try {
            Logger.log(LogLevel.INFO, String.format("Attempting to uwuify text: %s.", text));
            String uwuifiedText = uwuifyClient.getText(text);

            if (uwuifiedText != null && !uwuifiedText.isEmpty()) {
                if (uwuifiedText.length() > 1994)
                    event.reply("Your text is too long!").queue();
                else
                    event.reply(uwuifiedText).queue();
            } else {
                event.reply("That didn't work. Try some other text!").queue();
            }
        } catch (IOException e) {
            Logger.log(LogLevel.INFO, String.format("Failed to uwuify text: %s.", text));
            event.reply("Error processing your request. Please try again later.").queue();
        } catch (InterruptedException e) {
            Logger.log(LogLevel.INFO, String.format("Interrupted while uwuifying text: %s.", text));
            event.reply("The request was interrupted. Please try again.").queue();
        }
    }
}
