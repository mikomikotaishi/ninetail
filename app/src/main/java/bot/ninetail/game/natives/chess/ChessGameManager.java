package bot.ninetail.game.natives.chess;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Chess game manager class
 */
@UtilityClass
public final class ChessGameManager {
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
