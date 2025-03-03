package bot.ninetail.structures.commands;

import jakarta.annotation.Nonnull;

import bot.ninetail.structures.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Interface for webhook commands.
 * This interface is used to define the structure of webhook commands.
 */
public interface WebhookCommand extends Command {
    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        throw new UnsupportedOperationException("invoke() must be overriden!");
    }
}