package bot.ninetail.game.chess;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import bot.ninetail.core.LogLevel;
import bot.ninetail.core.Logger;
import bot.ninetail.game.Engine;

/**
 * Chess engine implementation using Java Foreign Function Interface
 * 
 * @extends Engine
 */
public class ChessEngine extends Engine {
    private final MethodHandle initChessEngineHandle;
    private final MethodHandle getBoardStateHandle;
    private final MethodHandle getBoardDisplayHandle;
    private final MethodHandle makeMoveHandle;
    private final MethodHandle resetBoardHandle;
    private final MethodHandle loadPositionHandle;
    private final MethodHandle isWhiteTurnHandle;
    private final MethodHandle getBestMoveHandle;
    private final MethodHandle isInCheckHandle;
    private final MethodHandle destroyChessEngineHandle;
    
    private final Arena arena;
    
    private static final Map<Character, String> FEN_TO_EMOJI = new HashMap<>();
    
    static {
        FEN_TO_EMOJI.put('P', ":white_pawn:");
        FEN_TO_EMOJI.put('N', ":white_knight:");
        FEN_TO_EMOJI.put('B', ":white_bishop:");
        FEN_TO_EMOJI.put('R', ":white_rook:");
        FEN_TO_EMOJI.put('Q', ":white_queen:");
        FEN_TO_EMOJI.put('K', ":white_king:");
        FEN_TO_EMOJI.put('p', ":black_pawn:");
        FEN_TO_EMOJI.put('n', ":black_knight:");
        FEN_TO_EMOJI.put('b', ":black_bishop:");
        FEN_TO_EMOJI.put('r', ":black_rook:");
        FEN_TO_EMOJI.put('q', ":black_queen:");
        FEN_TO_EMOJI.put('k', ":black_king:");
    }
    
    private static final String WHITE_SQUARE = ":white_large_square:";
    private static final String BLACK_SQUARE = ":black_large_square:";

    /**
     * Calculate the length of a null-terminated C string in a memory segment
     * 
     * @param segment The memory segment containing the string
     * 
     * @return The length of the string (excluding the null terminator)
     */
    private static long strlen(MemorySegment segment) {
        if (segment == null) 
            return -1;
        if (segment.byteSize() <= 0)
            return -1;
        
        long len = 0;
        try {
            while (true) {
                byte b = segment.get(ValueLayout.JAVA_BYTE, len);
                if (b == 0) 
                    break;
                ++len;
            }
            return len;
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }
    
    /**
     * Constructs a new ChessEngine object.
     */
    public ChessEngine() {
        super("chess");
        arena = Arena.ofShared();
        Linker linker = Linker.nativeLinker();
        Path libPath = Paths.get(System.getProperty("user.dir"), "src/native/chess/lib/libchess.so").toAbsolutePath();
        Logger.log(LogLevel.INFO, "Loading chess library from: " + libPath);
        SymbolLookup symbolLookup = SymbolLookup.libraryLookup(libPath.toString(), arena);
        
        initChessEngineHandle = linker.downcallHandle(
            symbolLookup.find("initChessEngine").orElseThrow(),
            FunctionDescriptor.ofVoid()
        );
        
        getBoardStateHandle = linker.downcallHandle(
            symbolLookup.find("getBoardState").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS)
        );
        
        getBoardDisplayHandle = linker.downcallHandle(
            symbolLookup.find("getBoardDisplay").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS)
        );
        
        makeMoveHandle = linker.downcallHandle(
            symbolLookup.find("makeMove").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );
        
        resetBoardHandle = linker.downcallHandle(
            symbolLookup.find("resetBoard").orElseThrow(),
            FunctionDescriptor.ofVoid()
        );
        
        loadPositionHandle = linker.downcallHandle(
            symbolLookup.find("loadPosition").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );
        
        isWhiteTurnHandle = linker.downcallHandle(
            symbolLookup.find("isWhiteTurn").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT)
        );
        
        getBestMoveHandle = linker.downcallHandle(
            symbolLookup.find("getBestMove").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
        );
        
        isInCheckHandle = linker.downcallHandle(
            symbolLookup.find("isInCheck").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );
        
        destroyChessEngineHandle = linker.downcallHandle(
            symbolLookup.find("destroyChessEngine").orElseThrow(),
            FunctionDescriptor.ofVoid()
        );
        
        initChessEngine();
    }
    
    /**
     * Initialises the chess engine.
     */
    public void initChessEngine() {
        try {
            Logger.log(LogLevel.INFO, "Initialising chess engine...");
            initChessEngineHandle.invoke();
            Logger.log(LogLevel.INFO, "Chess engine initialised successfully");
        } catch (Throwable t) {
            throw new RuntimeException("Failed to initialise chess engine", t);
        }
    }
    
    /**
     * Gets the board state in FEN notation.
     * 
     * @return The board state in FEN notation.
     */
    public String getBoardState() {
        try {
            MemorySegment result = (MemorySegment) getBoardStateHandle.invoke();
            
            if (result == null) {
                System.err.println("getBoardState: Native call returned null");
                return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            }
            
            long strLen = strlen(result);
            if (strLen <= 0) {
                System.err.println("getBoardState: Native call returned empty string");
                return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            }
            
            String resultStr = result.getUtf8String(0);
            Logger.log(LogLevel.INFO, "Java: getBoardState returned: " + resultStr);
            return resultStr;
        } catch (Throwable t) {
            System.err.println("getBoardState exception: " + e);
            e.printStackTrace();
            return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        }
    }
    
    /**
     * Gets a string representation of the board.
     * 
     * @return The string representation of the board.
     */
    public String getBoardDisplay() {
        try {
            MemorySegment result = (MemorySegment) getBoardDisplayHandle.invoke();
            if (result.byteSize() == 0)
                throw new RuntimeException("Failed to get board display: empty result");
            return result.getUtf8String(0);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get board display", t);
        }
    }
    
    /**
     * Makes a move on the board.
     * 
     * @param moveStr The move in algebraic notation.
     * 
     * @return 1 if the move was successful, 0 otherwise.
     */
    public int makeMove(String moveStr) {
        try {
            MemorySegment moveStrSegment = arena.allocateUtf8String(moveStr);
            return (int) makeMoveHandle.invoke(moveStrSegment);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to make move", t);
        }
    }
    
    /**
     * Resets the board to the initial position.
     */
    public void resetBoard() {
        try {
            resetBoardHandle.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to reset board", t);
        }
    }
    
    /**
     * Loads a position from a FEN string.
     * 
     * @param fen The FEN string.
     * 
     * @return 1 if the position was successfully loaded, 0 otherwise.
     */
    public int loadPosition(String fen) {
        try {
            MemorySegment fenSegment = arena.allocateUtf8String(fen);
            return (int) loadPositionHandle.invoke(fenSegment);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to load position", t);
        }
    }
    
    /**
     * Checks if it is white's turn to move.
     * 
     * @return 1 if it is white's turn to move, 0 otherwise.
     */
    public int isWhiteTurn() {
        try {
            return (int) isWhiteTurnHandle.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to check turn", t);
        }
    }
    
    /**
     * Gets the best move for the current position using alpha-beta pruning.
     * 
     * @param depth The depth of the search.
     * 
     * @return The best move in algebraic notation.
     */
    public String getBestMove(int depth) {
        try {
            MemorySegment result = (MemorySegment) getBestMoveHandle.invoke(depth);
            return result.getUtf8String(0);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get best move", t);
        }
    }
    
    /**
     * Checks if the specified colour is in check.
     * 
     * @param colour The colour to check ("white" or "black").
     * 
     * @return True if the specified colour is in check, false otherwise.
     */
    public boolean isInCheck(String colour) {
        try {
            MemorySegment colourSegment = arena.allocateUtf8String(colour);
            return (int) isInCheckHandle.invoke(colourSegment) == 1;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to check if in check", t);
        }
    }
    
    /**
     * Destroys the chess engine.
     */
    public void destroyChessEngine() {
        try {
            destroyChessEngineHandle.invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to destroy chess engine", t);
        }
    }
    
    /**
     * Closes this engine and releases any resources.
     */
    public void close() {
        destroyChessEngine();
        arena.close();
    }
    
    /**
     * Converts the FEN representation of the board to a Discord message using emojis.
     * 
     * @param fen The FEN string.
     * 
     * @return The board representation using Discord emojis.
     */
    public String convertFenToEmoji(String fen) {
        StringBuilder emojiBoard = new StringBuilder();
        String[] rows = fen.split(" ")[0].split("/");
        boolean isWhiteSquare = true;
        for (String row: rows) {
            for (char c: row.toCharArray()) {
                if (Character.isDigit(c)) {
                    int emptySquares = Character.getNumericValue(c);
                    for (int i = 0; i < emptySquares; i++) {
                        emojiBoard.append(isWhiteSquare ? WHITE_SQUARE : BLACK_SQUARE);
                        isWhiteSquare = !isWhiteSquare;
                    }
                } else {
                    emojiBoard.append(FEN_TO_EMOJI.getOrDefault(c, ""));
                    isWhiteSquare = !isWhiteSquare;
                }
            }
            emojiBoard.append("\n");
            isWhiteSquare = !isWhiteSquare;
        }
        return emojiBoard.toString();
    }
}
