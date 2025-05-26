package bot.ninetail.game.poker;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.annotation.Nonnull;

import bot.ninetail.core.config.ConfigNames;
import bot.ninetail.game.Engine;

/**
 * Poker engine implementation using Java Foreign Function Interface
 * 
 * @extends Engine
 */
public class PokerEngine extends Engine {
    @Nonnull
    private final MethodHandle createGameHandle;
    
    @Nonnull
    private final MethodHandle destroyGameHandle;

    @Nonnull
    private final MethodHandle startHandHandle;

    @Nonnull
    private final MethodHandle getGameStateHandle;
    
    @Nonnull
    private final MethodHandle executeActionHandle;

    @Nonnull
    private final Arena arena;

    /**
     * Constructs a new PokerEngine object.
     */
    public PokerEngine() {
        super("poker");
        arena = Arena.ofShared();
        Linker linker = Linker.nativeLinker();
        Path libPath = Paths.get(ConfigNames.ENGINE_POKER_PATH).toAbsolutePath();
        SymbolLookup symbolLookup = SymbolLookup.libraryLookup(libPath.toString(), arena);

        createGameHandle = linker.downcallHandle(
            symbolLookup.find("createGame").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT)
        );

        destroyGameHandle = linker.downcallHandle(
            symbolLookup.find("destroyGame").orElseThrow(),
            FunctionDescriptor.ofVoid()
        );

        startHandHandle = linker.downcallHandle(
            symbolLookup.find("startHand").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN)
        );

        getGameStateHandle = linker.downcallHandle(
            symbolLookup.find("getGameState").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS)
        );

        executeActionHandle = linker.downcallHandle(
            symbolLookup.find("executeAction").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT)
        );

        createGame(1000, 5, 10); // Default values for starting chips, small blind, and big blind
    }

    /**
     * Creates a new poker game.
     * 
     * @param startingChips The initial number of chips for each player.
     * @param smallBlind The amount of the small blind.
     * @param bigBlind The amount of the big blind.
     * 
     * @return True if the game was created successfully, false otherwise.
     */
    public boolean createGame(int startingChips, int smallBlind, int bigBlind) {
        try {
            return (boolean) createGameHandle.invoke(startingChips, smallBlind, bigBlind);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to create game", t);
        }
    }

    /**
     * Destroys the poker game.
     */
    public void destroyGame() {
        try {
            destroyGameHandle.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to destroy game", t);
        }
    }

    /**
     * Starts a new hand in the poker game.
     * 
     * @return True if the hand was started successfully, false otherwise.
     */
    public boolean startHand() {
        try {
            return (boolean) startHandHandle.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to start hand", t);
        }
    }

    /**
     * Gets the current game state as a string.
     * 
     * @return A string describing the current game state.
     */
    public String getGameState() {
        try {
            MemorySegment result = (MemorySegment) getGameStateHandle.invoke();
            return result.getString(0);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get game state", t);
        }
    }

    /**
     * Executes a player action.
     * 
     * @param action The action to execute (0: Fold, 1: Check, 2: Call, 3: Raise, 4: AllIn).
     * @param amount The amount for the action (if applicable).
     * 
     * @return A string describing the result of the action.
     */
    public String executeAction(int action, int amount) {
        try {
            MemorySegment result = (MemorySegment) executeActionHandle.invoke(action, amount);
            return result.getString(0);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to execute action", t);
        }
    }

    /**
     * Closes this engine and releases any resources.
     */
    public void close() {
        destroyGame();
        arena.close();
    }
}
