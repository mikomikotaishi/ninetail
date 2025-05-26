package bot.ninetail.game.chess;

import jakarta.annotation.Nonnull;

import bot.ninetail.game.GameManager;

import lombok.experimental.UtilityClass;

/**
 * Chess game manager class
 * 
 * @extends GameManager
 */
@UtilityClass
public class ChessGameManager extends GameManager {
    /**
     * The chess engine instance.
     */
    @Nonnull
    private static ChessEngine chessEngine;

    /**
     * Initialises a new chess game.
     */
    public static void startNewGame() {
        chessEngine = new ChessEngine();
        chessEngine.resetBoard();
    }

    /**
     * Gets the current chess engine instance.
     * 
     * @return The current chess engine instance.
     */
    public static ChessEngine getChessEngine() {
        return chessEngine;
    }
}
