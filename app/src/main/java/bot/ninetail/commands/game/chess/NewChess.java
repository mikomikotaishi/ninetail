package bot.ninetail.commands.game.chess;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.game.chess.*;
import bot.ninetail.structures.commands.GameCommand;

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
        Logger.log(LogLevel.INFO, String.format("New chess game command invoked by %s (%s) of guild %s (%s)", 
                                                event.getUser().getGlobalName(), 
                                                event.getUser().getId(),
                                                event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
                                                event.getGuild() != null ? event.getGuild().getId() : "N/A"));
        ChessGameManager.startNewGame();
        ChessEngine chessEngine = ChessGameManager.getChessEngine();
        
        String boardState = chessEngine.getBoardState();
        Logger.log(LogLevel.INFO, "NewChess command - received board state: " + boardState);
        
        if (boardState.startsWith("ERROR:"))
            boardState = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        
        String boardDisplay = chessEngine.convertFenToEmoji(boardState);
        event.reply("New game started!\n" + boardDisplay).queue();
    }
}
