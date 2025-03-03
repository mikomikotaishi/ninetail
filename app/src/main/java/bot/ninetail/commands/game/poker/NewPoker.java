package bot.ninetail.commands.game.poker;

import bot.ninetail.game.poker.*;
import bot.ninetail.structures.commands.GameCommand;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to start a new poker game.
 * 
 * @implements GameCommand
 */
public final class NewPoker implements GameCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private NewPoker() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        PokerGameManager.startNewGame();
        event.reply("A new poker game has been started!").queue();
    }
}
