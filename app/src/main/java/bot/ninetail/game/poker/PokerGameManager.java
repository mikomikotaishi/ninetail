package bot.ninetail.game.poker;

import bot.ninetail.game.GameManager;

/**
 * Poker game manager class
 * 
 * @extends GameManager
 */
public class PokerGameManager extends GameManager {
    private static PokerEngine pokerEngine;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private PokerGameManager() {}

    /**
     * Initialises a new poker game.
     */
    public static void startNewGame() {
        pokerEngine = new PokerEngine();
        pokerEngine.startHand();
    }

    /**
     * Gets the current poker engine instance.
     * 
     * @return The current poker engine instance.
     */
    public static PokerEngine getPokerEngine() {
        return pokerEngine;
    }
}