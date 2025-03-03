package bot.ninetail.game.chess;

import bot.ninetail.game.GameManager;

/**
 * Chess game manager class
 */
public class ChessGameManager extends GameManager {
    private static ChessEngine chessEngine;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ChessGameManager() {}

    /**
     * Initialises a new chess game.
     */
    public static void startNewGame() {
        chessEngine = new ChessEngine();
        chessEngine.resetBoard();
    }

    /**
     * Gets the current chess engine instance.
     * @return The current chess engine instance.
     */
    public static ChessEngine getChessEngine() {
        return chessEngine;
    }
}