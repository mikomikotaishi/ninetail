package bot.ninetail.commands.general;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.structures.commands.BasicCommand;
import bot.ninetail.structures.commands.ContentResponder;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to provide facts about foxes.
 * 
 * @extends ContentResponder
 * @implements BasicCommand
 */
public final class FoxFacts extends ContentResponder implements BasicCommand {
    static {
        setContents(FoxFacts.class, new String[]{
                "Foxes are known to make up to 40 different sounds, some of which inlcude a scream-like howl, as well as chattering.",
                "Foxes have whiskers on their legs and faces that help them navigate in the dark and sense nearby objects.",
                "A group of foxes is called a \"skulk\" or \"leash\", but they’re usually solitary hunters.",
                "Foxes use Earth’s magnetic field to hunt, aligning their pounce with the magnetic north to catch hidden prey.",
                "Red foxes can run up to 30 miles per hour, which helps them escape predators and chase down dinner.",
                "The thick, bushy tails of foxes (called \"brushes\") help with balance, warmth, and even communication.",
                "Foxes are great climbers and swimmers, and some have even been spotted snoozing in trees.",
                "Fennec foxes, the smallest species, have enormous ears that help dissipate heat and hear insects underground.",
                "Arctic foxes change color with the seasons—white in winter for snow camouflage, and brown in summer to match the tundra.",
                "Foxes are incredibly adaptable, living everywhere from deserts and forests to cities and suburbs."
            }
        );
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private FoxFacts() {}

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
        event.reply(FoxFacts.getRandomContent()).queue();
    }
}
