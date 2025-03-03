package bot.ninetail.commands.general;

import bot.ninetail.structures.commands.BasicCommand;
import bot.ninetail.utilities.RandomNumberGenerator;

import jakarta.annotation.Nonnull;

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
        System.out.println("Fox fact command invoked.");
        event.reply(FoxFacts.getResponse()).queue();
    }
}
