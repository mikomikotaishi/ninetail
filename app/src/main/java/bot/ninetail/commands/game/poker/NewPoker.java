package bot.ninetail.commands.game.poker;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.game.poker.*;
import bot.ninetail.structures.commands.GameCommand;

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
        Logger.log(LogLevel.INFO, String.format("New poker game command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        PokerGameManager.startNewGame();
        Logger.log(LogLevel.INFO, String.format("A new poker game has been started by %s of guild %s", event.getUser(), event.getGuild()));
        event.reply("A new poker game has been started!").queue();
    }
}
