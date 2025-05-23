/**
 * @file Move.cppm
 * @brief Implementation of chess moves.
 */

module;

#include <string>

export module chess.Move;

/**
 * @class Move
 * @brief A class representing a chess move.
 */
export class Move {
private:
    int fromRow; ///< The starting row of the move.
    int fromCol; ///< The starting column of the move.
    int toRow; ///< The ending row of the move.
    int toCol; ///< The ending column of the move.
    bool isCapture; ///< Indicates if the move is a capture.
    bool isPromotion; ///< Indicates if the move is a promotion.
    bool isCastling; ///< Indicates if the move is a castling move.
    bool isEnPassant; ///< Indicates if the move is an en passant capture.
    char promotionPiece; ///< The piece to which a pawn is promoted.

public:
    /**
     * @brief Constructs a new Move object.
     * @param fromR The starting row.
     * @param fromC The starting column.
     * @param toR The ending row.
     * @param toC The ending column.
     */
    Move(int fromR, int fromC, int toR, int toC): 
        fromRow{fromR}, fromCol{fromC}, toRow{toR}, toCol{toC},
        isCapture{false}, isPromotion{false}, isCastling{false}, 
        isEnPassant{false}, promotionPiece{'\0'} {}

    /**
     * @brief Constructs a new Move object with promotion.
     * @param fromR The starting row.
     * @param fromC The starting column.
     * @param toR The ending row.
     * @param toC The ending column.
     * @param promotion The piece to which a pawn is promoted.
     */
    Move(int fromR, int fromC, int toR, int toC, char promotion): 
        fromRow{fromR}, fromCol{fromC}, toRow{toR}, toCol{toC},
        isCapture{false}, isPromotion{true}, isCastling{false}, 
        isEnPassant{false}, promotionPiece{promotion} {}

    /**
     * @brief Constructs a new Move object with en passant capture.
     * @param fromR The starting row.
     * @param fromC The starting column.
     * @param toR The ending row.
     * @param toC The ending column.
     * @param enPassant Indicates if the move is an en passant capture.
     */
    Move(int fromR, int fromC, int toR, int toC, bool enPassant): 
        fromRow{fromR}, fromCol{fromC}, toRow{toR}, toCol{toC},
        isCapture{true}, isPromotion{false}, isCastling{false}, 
        isEnPassant{enPassant}, promotionPiece{'\0'} {}
    
    /**
     * @brief Gets the starting row of the move.
     * @return The starting row.
     */
    int getFromRow() const { 
        return fromRow; 
    }

    /**
     * @brief Gets the starting column of the move.
     * @return The starting column.
     */
    int getFromCol() const { 
        return fromCol; 
    }

    /**
     * @brief Gets the ending row of the move.
     * @return The ending row.
     */
    int getToRow() const { 
        return toRow; 
    }

    /**
     * @brief Gets the ending column of the move.
     * @return The ending column.
     */
    int getToCol() const { 
        return toCol; 
    }

    /**
     * @brief Checks if the move is a capture.
     * @return True if the move is a capture, false otherwise.
     */
    bool getIsCapture() const { 
        return isCapture; 
    }

    /**
     * @brief Checks if the move is a promotion.
     * @return True if the move is a promotion, false otherwise.
     */
    bool getIsPromotion() const { 
        return isPromotion; 
    }

    /**
     * @brief Checks if the move is a castling move.
     * @return True if the move is a castling move, false otherwise.
     */
    bool getIsCastling() const { 
        return isCastling; 
    }

    /**
     * @brief Checks if the move is an en passant capture.
     * @return True if the move is an en passant capture, false otherwise.
     */
    bool getIsEnPassant() const { 
        return isEnPassant; 
    }

    /**
     * @brief Gets the promotion piece.
     * @return The promotion piece.
     */
    char getPromotionPiece() const { 
        return promotionPiece; 
    }
    
    /**
     * @brief Sets the capture flag.
     * @param capture True if the move is a capture, false otherwise.
     */
    void setCapture(bool capture) { 
        isCapture = capture; 
    }

    /**
     * @brief Sets the promotion flag and piece.
     * @param promotion True if the move is a promotion, false otherwise.
     * @param piece The piece to which a pawn is promoted.
     */
    void setPromotion(bool promotion, char piece = 'q') { 
        isPromotion = promotion;
        promotionPiece = piece;
    }

    /**
     * @brief Sets the castling flag.
     * @param castling True if the move is a castling move, false otherwise.
     */
    void setCastling(bool castling) { 
        isCastling = castling; 
    }

    /**
     * @brief Sets the en passant flag.
     * @param enPassant True if the move is an en passant capture, false otherwise.
     */
    void setEnPassant(bool enPassant) { 
        isEnPassant = enPassant; 
    }
    
    /**
     * @brief Creates a Move object from algebraic notation.
     * @param algebraic The move in algebraic notation.
     * @return The Move object.
     */
    static Move fromAlgebraic(const std::string& algebraic) {
        int fromCol = algebraic[0] - 'a';
        int fromRow = '8' - algebraic[1];
        int toCol = algebraic[2] - 'a';
        int toRow = '8' - algebraic[3];
        
        Move move(fromRow, fromCol, toRow, toCol);
        
        if (algebraic.length() > 4) 
            move.setPromotion(true, algebraic[4]);
        
        return move;
    }
    
    /**
     * @brief Converts the move to algebraic notation.
     * @return The move in algebraic notation.
     */
    std::string toAlgebraic() const {
        std::string result;
        result += static_cast<char>('a' + fromCol);
        result += static_cast<char>('8' - fromRow);
        result += static_cast<char>('a' + toCol);
        result += static_cast<char>('8' - toRow);
        
        if (isPromotion && promotionPiece != '\0')
            result += promotionPiece;
        
        return result;
    }
};