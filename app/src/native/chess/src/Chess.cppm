/**
 * @file Chess.cppm
 * @brief Exports the chess engine interface.
 */

module;

#ifdef _WIN32 
    #define EXPORT_API __declspec(dllexport)
#else
    #define EXPORT_API [[gnu::visibility("default")]]
#endif

#include <memory>
#include <string>

export module chess;

import chess.Board;
import chess.ChessEngine;
import chess.Move;
import chess.Piece;

export extern "C" {
    static std::unique_ptr<ChessEngine> chessEngine = nullptr;
    
    /**
     * @brief Initialises the chess engine.
     */
    EXPORT_API void initChessEngine() {
        if (!chessEngine)
            chessEngine = std::make_unique<ChessEngine>();
    }
    
    /**
     * @brief Gets the board state in FEN notation.
     * @return The board state in FEN notation.
     */
    EXPORT_API const char* getBoardState() {
        static std::string boardFEN;
        if (chessEngine)
            boardFEN = chessEngine->getBoardFEN();
        else
            boardFEN = "Engine not initialised";
        if (boardFEN.empty())
            boardFEN = "empty";
        return boardFEN.c_str();
    }
    
    /**
     * @brief Gets a string representation of the board.
     * @return The string representation of the board.
     */
    EXPORT_API const char* getBoardDisplay() {
        static std::string boardDisplay;
        if (chessEngine)
            boardDisplay = chessEngine->getBoardDisplay();
        else
            boardDisplay = "Engine not initialised";
        return boardDisplay.c_str();
    }
    
    /**
     * @brief Makes a move on the board.
     * @param moveStr The move in algebraic notation.
     * @return 1 if the move was successful, 0 otherwise.
     */
    EXPORT_API int makeMove(const char* moveStr) {
        if (chessEngine)
            return chessEngine->makeMove(moveStr) ? 1 : 0;
        return 0;
    }
    
    /**
     * @brief Resets the board to the initial position.
     */
    EXPORT_API void resetBoard() {
        if (chessEngine)
            chessEngine->resetBoard();
    }
    
    /**
     * @brief Loads a position from a FEN string.
     * @param fen The FEN string.
     * @return 1 if the position was successfully loaded, 0 otherwise.
     */
    EXPORT_API int loadPosition(const char* fen) {
        if (chessEngine)
            return chessEngine->loadPosition(fen) ? 1 : 0;
        return 0;
    }
    
    /**
     * @brief Checks if it is white's turn to move.
     * @return 1 if it is white's turn to move, 0 otherwise.
     */
    EXPORT_API int isWhiteTurn() {
        if (chessEngine)
            return chessEngine->isWhiteTurn() ? 1 : 0;
        return 0;
    }

    /**
     * @brief Gets the best move for the current position using alpha-beta pruning.
     * @param depth The depth of the search.
     * @return The best move in algebraic notation.
     */
    EXPORT_API const char* getBestMove(int depth) {
        static std::string bestMove;
        if (chessEngine) {
            bestMove = chessEngine->getBestMove(depth);
            return bestMove.c_str();
        }
        return nullptr;
    }
    
    /**
     * @brief Checks if the specified colour is in check.
     * @param colour The colour to check ("white" or "black").
     * @return 1 if the specified colour is in check, 0 otherwise.
     */
    EXPORT_API int isInCheck(const char* colour) {
        if (chessEngine)
            return chessEngine->isInCheck(colour) ? 1 : 0;
        return 0;
    }
    
    /**
     * @brief Destroys the chess engine.
     */
    EXPORT_API void destroyChessEngine() {
        chessEngine.reset();
    }
}