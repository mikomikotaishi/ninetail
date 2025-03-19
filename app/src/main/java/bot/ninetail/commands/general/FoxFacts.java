package bot.ninetail.commands.general;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.BasicCommand;
import bot.ninetail.utilities.RandomNumberGenerator;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to provide facts about foxes.
 * 
 * @implements BasicCommand
 */
public final class FoxFacts implements BasicCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private FoxFacts() {}

    /**
     * Array of facts about foxes.
     */
    private static final String[] FACTS = {
        "Foxes are known to make up to 40 different sounds, some of which inlcude a scream-like howl, as well as chattering."
    };

    /**
     * Gets a random fact about foxes.
     *
     * @return A random fact about foxes.
     */
    private static String getResponse() {
        int index = RandomNumberGenerator.generateRandomNumber(FACTS.length);
        return FACTS[index];
    }

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        Logger.log(LogLevel.INFO, String.format("Fox facts command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        event.reply(FoxFacts.getResponse()).queue();
    }
}
