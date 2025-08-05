package bot.ninetail.commands.game.chess;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import jakarta.annotation.Nonnull;

import bot.ninetail.game.chess.*;
import bot.ninetail.structures.commands.GameCommand;

import lombok.experimental.UtilityClass;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Command to make a move in the current chess game.
 * 
 * @implements GameCommand
 */
@UtilityClass
public final class ChessMove implements GameCommand {
    @Nonnull
    private static final Logger LOGGER = System.getLogger(ChessMove.class.getName());

    /**
     * Invokes the command.
     *
     * @param event The event that triggered the command.
     */
    public static void invoke(@Nonnull SlashCommandInteractionEvent event) {
        LOGGER.log(Level.INFO, "Chess move command invoked by {0} ({1}) of guild {2} ({3})", 
            event.getUser().getGlobalName(), 
            event.getUser().getId(),
            event.getGuild() != null ? event.getGuild().getName() : "DIRECTMESSAGES",
            event.getGuild() != null ? event.getGuild().getId() : "N/A"
        );
        
        ChessEngine chessEngine = ChessGameManager.getChessEngine();
        if (chessEngine == null) {
            event.reply("No game in progress. Use /newchess to start a new game.").queue();
            return;
        }

        String move = event.getOption("move").getAsString();
        if (chessEngine.makeMove(move)) {
            String boardState = chessEngine.getBoardState();
            String boardDisplay = chessEngine.convertFenToEmoji(boardState);
            event.reply(String.format("User move: %s\n%s", move, boardDisplay)).queue();

            String engineMove = chessEngine.getBestMove(3);
            chessEngine.makeMove(engineMove);
            boardState = chessEngine.getBoardState();
            boardDisplay = chessEngine.convertFenToEmoji(boardState);
            event.getChannel().sendMessage(String.format("Engine move: %s\n%s", engineMove, boardDisplay)).queue();

            if (isCheckmate(chessEngine)) {
                event.getChannel().sendMessage(String.format("Checkmate! %s wins!", chessEngine.isWhiteTurn() ? "Black" : "White")).queue();
            }
        } else {
            event.reply("Invalid move: " + move).queue();
        }
    }

    /**
     * Checks if the chess engine is in checkmate.
     * 
     * @param chessEngine The engine to check
     * 
     * @return Whether the chess engine is in checkmate
     */
    private static boolean isCheckmate(ChessEngine chessEngine) {
        return chessEngine.isInCheck(chessEngine.isWhiteTurn() ? "white" : "black");
    }
}
