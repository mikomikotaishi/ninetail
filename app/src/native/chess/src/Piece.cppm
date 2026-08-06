/**
 * @file Piece.cppm
 * @module bot.ninetail.native.game.chess.Piece
 * @brief Implementation of the chess piece.
 */

module;

#include <cctype>
#include <utility>

export module bot.ninetail.native.game.chess.Piece;

/**
 * @enum PieceType
 * @brief Enum representing the type of a chess piece.
 */
export enum class PieceType { 
    Empty, ///< No piece
    Pawn, ///< Pawn piece
    Knight, ///< Knight piece
    Bishop, ///< Bishop piece
    Rook, ///< Rook piece
    Queen, ///< Queen piece
    King ///< King piece
};

/**
 * @enum PieceColor
 * @brief Enum representing the color of a chess piece.
 */
export enum class PieceColor {
    None, ///< No color
    Black, ///< Black piece
    White ///< White piece
};

/**
 * @class Piece
 * @brief A class representing a chess piece.
 */
export class Piece {
private:
    PieceType type; ///< The type of the piece.
    PieceColor color; ///< The color of the piece.
public:
    /**
     * @brief Constructs a new Piece object.
     * @param type The type of the piece.
     * @param color The color of the piece.
     */
    Piece(PieceType type = PieceType::Empty, PieceColor color = PieceColor::None):
        type{type}, color{color} {}
    
    /**
     * @brief Gets the type of the piece.
     * @return The type of the piece.
     */
    [[nodiscard]]
    PieceType getType() const noexcept { 
        return type; 
    }

    /**
     * @brief Gets the color of the piece.
     * @return The color of the piece.
     */
    [[nodiscard]]
    PieceColor getColor() const noexcept { 
        return color; 
    }
    
    /**
     * @brief Converts the piece to a character representation.
     * @return The character representation of the piece.
     */
    [[nodiscard]]
    char toChar() const noexcept {
        if (type == PieceType::Empty) {
            return ' ';
        }
        char pieceChar;
        switch (type) {
            case PieceType::Pawn: 
                pieceChar = 'p'; 
                break;
            case PieceType::Knight: 
                pieceChar = 'n'; 
                break;
            case PieceType::Bishop: 
                pieceChar = 'b'; 
                break;
            case PieceType::Rook: 
                pieceChar = 'r'; 
                break;
            case PieceType::Queen: 
                pieceChar = 'q'; 
                break;
            case PieceType::King: 
                pieceChar = 'k'; 
                break;
            default: 
                std::unreachable();
        }
        
        return (color == PieceColor::White) ? std::toupper(pieceChar) : pieceChar;
    }
    
    /**
     * @brief Converts the piece to its FEN notation character.
     * @return The FEN notation character of the piece.
     */
    [[nodiscard]]
    char toFEN() const noexcept {
        return toChar();
    }
};
