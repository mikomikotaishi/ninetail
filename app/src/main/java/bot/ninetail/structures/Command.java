package bot.ninetail.structures;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Interface for all commands.
 * This interface is used to define the structure of basic commands.
 */
public interface Command {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        throw new UnsupportedOperationException("invoke() must be overriden!");
    }
}
