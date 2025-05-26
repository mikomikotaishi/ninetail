package bot.ninetail.game.poker;

import jakarta.annotation.Nonnull;

import bot.ninetail.game.GameManager;

import lombok.experimental.UtilityClass;

/**
 * Poker game manager class
 * 
 * @extends GameManager
 */
@UtilityClass
public class PokerGameManager extends GameManager {
    /**
     * The poker engine instance.
     */
    @Nonnull
    private static PokerEngine pokerEngine;

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