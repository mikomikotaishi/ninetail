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
    /**
     * Static block to load contents.
     */
    static {
        setContents(FoxFacts.class, 
            new String[]{
                "Foxes are known to make up to 40 different sounds, some of which inlcude a scream-like howl, as well as chattering.",
                "Foxes have whiskers on their legs and faces that help them navigate in the dark and sense nearby objects.",
                "A group of foxes is called a \"skulk\" or \"leash\", but they're usually solitary hunters.",
                "Foxes use Earth's magnetic field to hunt, aligning their pounce with the magnetic north to catch hidden prey.",
                "Red foxes can run up to 30 miles per hour, which helps them escape predators and chase down dinner.",
                "The thick, bushy tails of foxes (called \"brushes\") help with balance, warmth, and even communication.",
                "Foxes are great climbers and swimmers, and some have even been spotted snoozing in trees.",
                "Fennec foxes, the smallest species, have enormous ears that help dissipate heat and hear insects underground.",
                "Arctic foxes change colour with the seasons—white in winter for snow camouflage, and brown in summer to match the tundra.",
                "Foxes are incredibly adaptable, living everywhere from deserts and forests to cities and suburbs.",
                "Foxes use their urine to mark food caches, helping them remember where they've stored leftovers for later snacking.",
                "Foxes have partially retractable claws, much like cats, giving them excellent grip for climbing and silent movement.",
                "Baby foxes are called kits, pups, or cubs, and they're born blind, deaf, and completely dependent on their parents.",
                "Foxes have slit-shaped pupils, similar to cats, which help them see well in low light and judge distance while hunting.",
                "The red fox is the most widespread carnivore on the planet, found across North America, Europe, Asia, and even Australia.",
                "Foxes communicate with each other using body language, ear position, tail movement, and facial expressions.",
                "Foxes can jump high fences and cover over 6 feet in a single leap, making them surprisingly agile escape artists.",
                "Some foxes \"play dead\" to lure in curious prey, especially when hunting birds.",
                "Urban foxes have learned to navigate traffic, wait at crosswalks, and even recognise garbage collection schedules.",
                "The grey fox can climb trees—it's one of the few members of the dog family with this talent, often using it to escape predators or find food.",
                "Foxes are omnivores, and their diets can include fruit, berries, insects, small mammals, and even garbage if they live near humans.",
                "A fox's hearing is so sharp it can detect a mouse squeaking from 100 feet away—even under snow!",
                "Foxes sometimes \"mouse pounce\" for fun, even when they're not hungry—possibly to keep their skills sharp.",
                "Foxes have been part of folklore and mythology for centuries, often portrayed as clever tricksters in cultures around the world.",
                "Fennec foxes' feet are covered in thick fur, protecting them from hot desert sand and giving extra grip.",
                "Some foxes mate for life, especially Arctic foxes, who share parenting duties and travel together across the tundra.",
                "Foxes can rotate their ears independently, allowing them to pinpoint sounds with amazing accuracy.",
                "Foxes groom each other in social pairs, not just to stay clean but also to strengthen social bonds.",
                "Foxes sometimes walk in a straight line, placing their back paws directly where their front paws stepped—a stealthy trick also seen in cats.",
                "Young foxes play-fight and wrestle, which helps them learn important survival and hunting skills as they grow."
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
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A")
        );
        
        event.reply(FoxFacts.getRandomContent()).queue();
    }
}
