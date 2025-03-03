package bot.ninetail.commands.game.chess;

import bot.ninetail.game.chess.*;
import bot.ninetail.structures.commands.GameCommand;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to start a new chess game.
 * 
 * @implements GameCommand
 */
public final class NewChess implements GameCommand {
    /**
     * Private constructor to prevent instantiation.
     */
    private NewChess() {}

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        ChessGameManager.startNewGame();
        ChessEngine chessEngine = ChessGameManager.getChessEngine();
        String boardState = chessEngine.getBoardState();
        String boardDisplay = chessEngine.convertFenToEmoji(boardState);
        event.reply("New game started!\n" + boardDisplay).queue();
    }
}
