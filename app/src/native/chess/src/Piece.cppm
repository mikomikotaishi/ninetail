/**
 * @file Piece.cppm
 * @brief Implementation of the chess piece.
 */

module;

#include <cctype>
#include <utility>

export module chess.Piece;

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
 * @enum PieceColour
 * @brief Enum representing the colour of a chess piece.
 */
export enum class PieceColour {
    None, ///< No colour
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
    PieceColour colour; ///< The colour of the piece.
public:
    /**
     * @brief Constructs a new Piece object.
     * @param type The type of the piece.
     * @param colour The colour of the piece.
     */
    Piece(PieceType type = PieceType::Empty, PieceColour colour = PieceColour::None):
        type{type}, colour{colour} {}
    
    /**
     * @brief Gets the type of the piece.
     * @return The type of the piece.
     */
    PieceType getType() const { 
        return type; 
    }

    /**
     * @brief Gets the colour of the piece.
     * @return The colour of the piece.
     */
    PieceColour getColour() const { 
        return colour; 
    }
    
    /**
     * @brief Converts the piece to a character representation.
     * @return The character representation of the piece.
     */
    char toChar() const {
        if (type == PieceType::Empty) 
            return ' ';
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
        
        return (colour == PieceColour::White) ? std::toupper(pieceChar) : pieceChar;
    }
    
    /**
     * @brief Converts the piece to its FEN notation character.
     * @return The FEN notation character of the piece.
     */
    char toFEN() const {
        return toChar();
    }
};