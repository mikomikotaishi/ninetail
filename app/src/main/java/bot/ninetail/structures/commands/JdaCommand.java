package bot.ninetail.structures.commands;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.Command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Interface for JDA commands.
 * This interface is used to define the structure of JDA commands.
 */
public interface JdaCommand extends Command {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     * @param instance The JDA instance.
     */
    static void invoke(@Nonnull SlashCommandInteractionEvent event, @Nonnull JDA instance) {
        throw new UnsupportedOperationException("invoke() must be overriden!");
    }
}
