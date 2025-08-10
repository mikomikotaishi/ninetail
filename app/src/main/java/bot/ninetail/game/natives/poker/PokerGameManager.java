package bot.ninetail.game.natives.poker;

import jakarta.annotation.Nonnull;

import lombok.experimental.UtilityClass;

/**
 * Poker game manager class
 */
@UtilityClass
public class PokerGameManager {
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