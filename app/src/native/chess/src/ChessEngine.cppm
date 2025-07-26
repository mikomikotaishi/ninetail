/**
 * @file ChessEngine.cppm
 * @module bot.ninetail.game.chess.ChessEngine
 * @brief Implementation of the chess engine.
 */

module;

#include <memory>
#include <string>
#include <string_view>
#include <vector>

export module bot.ninetail.game.chess.ChessEngine;

import bot.ninetail.game.chess.Board;
import bot.ninetail.game.chess.Move;
import bot.ninetail.game.chess.Piece;

/**
 * @class ChessEngine
 * @brief A class representing a chess engine.
 */
export class ChessEngine {
private:
    std::unique_ptr<Board> board; ///< The chess board.
    std::vector<std::string> moveHistory; ///< The history of moves.

public:
    /**
     * @brief Constructs a new ChessEngine object.
     */
    ChessEngine(): 
        board{std::make_unique<Board>()} {}
    
    /**
     * @brief Makes a move on the board.
     * @param moveStr The move in algebraic notation.
     * @return True if the move was successful, false otherwise.
     */
    bool makeMove(std::string_view moveStr) noexcept {
        Move move = Move::fromAlgebraic(moveStr);
        bool success = board->makeMove(move);
        if (success) {
            moveHistory.push_back(moveStr);
        }
        return success;
    }
    
    /**
     * @brief Gets the board state in FEN notation.
     * @return The board state in FEN notation.
     */
    [[nodiscard]]
    std::string getBoardFEN() const {
        return board->getFEN();
    }
    
    /**
     * @brief Gets a string representation of the board.
     * @return The string representation of the board.
     */
    [[nodiscard]]
    std::string getBoardDisplay() const noexcept {
        return board->getBoardString();
    }
    
    /**
     * @brief Resets the board to the initial position.
     */
    [[nodiscard]]
    void resetBoard() noexcept {
        board = std::make_unique<Board>();
        moveHistory.clear();
    }
    
    /**
     * @brief Loads a position from a FEN string.
     * @param fen The FEN string.
     * @return True if the position was successfully loaded, false otherwise.
     */
    [[nodiscard]]
    bool loadPosition(const std::string& fen) {
        board = std::make_unique<Board>();
        moveHistory.clear();
        return board->loadFEN(fen);
    }
    
    /**
     * @brief Checks if it is white's turn to move.
     * @return True if it is white's turn to move, false otherwise.
     */
    [[nodiscard]]
    bool isWhiteTurn() const noexcept {
        return board->isWhiteToMove();
    }

    /**
     * @brief Gets the best move for the current position using alpha-beta pruning.
     * @param depth The depth of the search.
     * @return The best move in algebraic notation.
     */
    [[nodiscard]]
    std::string getBestMove(int depth) noexcept {
        std::vector<Move> legalMoves = board->getLegalMoves();
        Move bestMove(0, 0, 0, 0);
        int bestValue = -10000;
        for (const Move& move : legalMoves) {
            Board newBoard = *board;
            newBoard.makeMove(move);
            int boardValue = newBoard.alphaBeta(depth - 1, -10000, 10000, false);
            if (boardValue > bestValue) {
                bestValue = boardValue;
                bestMove = move;
            }
        }
        return bestMove.toAlgebraic();
    }

    /**
     * @brief Checks if the specified colour is in check.
     * @param colour The colour to check.
     * @return True if the specified colour is in check, false otherwise.
     */
    [[nodiscard]]
    bool isInCheck(const std::string& colour) const noexcept {
        PieceColour pieceColour = (colour == "white") ? PieceColour::White : PieceColour::Black;
        return board->isInCheck(pieceColour);
    }
};
