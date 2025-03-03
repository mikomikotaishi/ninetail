/**
 * @file Board.cppm
 * @brief Implementation of the chess board and related functionalities.
 */

module;

#include <array>
#include <cctype>
#include <format>
#include <sstream>
#include <stdexcept>
#include <string>
#include <vector>

export module chess.Board;

import chess.Move;
import chess.Piece;

/**
 * @namespace Tables
 * @brief Contains piece-square tables for positional evaluation.
 */
export namespace Tables {
    constexpr std::array<std::array<int, 8>, 8> PawnTable{{
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 5, 5, 5, 5, 5, 5, 5, 5 },
        { 1, 1, 2, 3, 3, 2, 1, 1 },
        { 0, 0, 1, 2, 2, 1, 0, 0 },
        { 0, 0, 0, 2, 2, 0, 0, 0 },
        { 0, -1, -1, 0, 0, -1, -1, 0 },
        { 0, 1, 1, -2, -2, 1, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0 }
    }};

    constexpr std::array<std::array<int, 8>, 8> KnightTable{{
        { -5, -4, -3, -3, -3, -3, -4, -5 },
        { -4, -2, 0, 0, 0, 0, -2, -4 },
        { -3, 0, 1, 1, 1, 1, 0, -3 },
        { -3, 1, 2, 2, 2, 2, 1, -3 },
        { -3, 1, 2, 2, 2, 2, 1, -3 },
        { -3, 0, 1, 1, 1, 1, 0, -3 },
        { -4, -2, 0, 0, 0, 0, -2, -4 },
        { -5, -4, -3, -3, -3, -3, -4, -5 }
    }};

    constexpr std::array<std::array<int, 8>, 8> BishopTable{{
        { -2, -1, -1, -1, -1, -1, -1, -2 },
        { -1, 0, 0, 0, 0, 0, 0, -1 },
        { -1, 0, 1, 1, 1, 1, 0, -1 },
        { -1, 1, 1, 1, 1, 1, 1, -1 },
        { -1, 0, 1, 1, 1, 1, 0, -1 },
        { -1, 1, 1, 1, 1, 1, 1, -1 },
        { -1, 0, 0, 0, 0, 0, 0, -1 },
        { -2, -1, -1, -1, -1, -1, -1, -2 }
    }};

    constexpr std::array<std::array<int, 8>, 8> RookTable{{
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 1, 1, 0, 0, 0 }
    }};

    constexpr std::array<std::array<double, 8>, 8> QueenTable{{
        { -2, -1, -1, -0.5, -0.5, -1, -1, -2 },
        { -1, 0, 0, 0, 0, 0, 0, -1 },
        { -1, 0, 0.5, 0.5, 0.5, 0.5, 0, -1 },
        { -0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5 },
        { -0.5, 0, 0.5, 0.5, 0.5, 0.5, 0, -0.5 },
        { -1, 0.5, 0.5, 0.5, 0.5, 0.5, 0, -1 },
        { -1, 0, 0.5, 0, 0, 0, 0, -1 },
        { -2, -1, -1, -0.5, -0.5, -1, -1, -2 }
    }};

    constexpr std::array<std::array<int, 8>, 8> KingTable{{
        { -3, -4, -4, -5, -5, -4, -4, -3 },
        { -3, -4, -4, -5, -5, -4, -4, -3 },
        { -3, -4, -4, -5, -5, -4, -4, -3 },
        { -3, -4, -4, -5, -5, -4, -4, -3 },
        { -2, -3, -3, -4, -4, -3, -3, -2 },
        { -1, -2, -2, -2, -2, -2, -2, -1 },
        { 2, 2, 0, 0, 0, 0, 2, 2 },
        { 2, 3, 1, 0, 0, 1, 3, 2 }
    }};
}

export namespace Moves {
    const std::array<std::pair<int, int>, 8> Directions = {{
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, 
        {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
    }};

    constexpr std::array<std::pair<int, int>, 8> KnightMoves = {{
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    }};

    const std::array<std::pair<int, int>, 8> KingMoves = {{
        {1, 0}, {-1, 0}, {0, 1}, {0, -1},
        {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
    }};
}

/**
 * @namespace Values
 * @brief Contains piece values for evaluation.
 */
export namespace Values {
    constexpr int PawnValue = 100;
    constexpr int KnightValue = 320;
    constexpr int BishopValue = 330;
    constexpr int RookValue = 500;
    constexpr int QueenValue = 900;
    constexpr int KingValue = 20000;
}

/**
 * @class Board
 * @brief Represents the chess board and handles game logic.
 */
export class Board {
private:
    std::array<std::array<Piece, 8>, 8> board; ///< The chess board
    bool whiteToMove; ///< Indicates if it's white's turn to move
    bool whiteCanCastleKingside; ///< Indicates if white can castle kingside
    bool whiteCanCastleQueenside; ///< Indicates if white can castle queenside
    bool blackCanCastleKingside; ///< Indicates if black can castle kingside
    bool blackCanCastleQueenside; ///< Indicates if black can castle queenside
    std::array<int, 2> enPassantTargetSquare; ///< The en passant target square
    int halfMoveClock; ///< The half-move clock for the fifty-move rule
    int fullMoveNumber; ///< The full move number

public:
    /**
     * @brief Constructs a new Board object and initialises the board.
     */
    Board(): 
        whiteToMove{true},
        whiteCanCastleKingside{true},
        whiteCanCastleQueenside{true},
        blackCanCastleKingside{true},
        blackCanCastleQueenside{true},
        halfMoveClock{0},
        fullMoveNumber{1} {
        enPassantTargetSquare[0] = -1;
        enPassantTargetSquare[1] = -1;
        initialiseBoard();
    }
    
    /**
     * @brief Initialises the board to the starting position.
     */
    void initialiseBoard() {
        for (int row = 0; row < 8; ++row)
            for (int col = 0; col < 8; ++col)
                board[row][col] = Piece();
        
        for (int col = 0; col < 8; ++col) {
            board[1][col] = Piece(PieceType::Pawn, PieceColour::Black);
            board[6][col] = Piece(PieceType::Pawn, PieceColour::White);
        }
        
        setupBackRank(0, PieceColour::Black);
        setupBackRank(7, PieceColour::White);
    }
    
    /**
     * @brief Sets up the back rank for a given colour.
     * @param row The row to set up the back rank.
     * @param colour The colour of the pieces.
     */
    void setupBackRank(int row, PieceColour colour) {
        board[row][0] = Piece(PieceType::Rook, colour);
        board[row][1] = Piece(PieceType::Knight, colour);
        board[row][2] = Piece(PieceType::Bishop, colour);
        board[row][3] = Piece(PieceType::Queen, colour);
        board[row][4] = Piece(PieceType::King, colour);
        board[row][5] = Piece(PieceType::Bishop, colour);
        board[row][6] = Piece(PieceType::Knight, colour);
        board[row][7] = Piece(PieceType::Rook, colour);
    }
    
    /**
     * @brief Gets the FEN representation of the board.
     * @return The FEN string.
     */
    std::string getFEN() const {
        std::stringstream fen;
        
        for (int row = 0; row < 8; ++row) {
            int emptyCount = 0;
            
            for (int col = 0; col < 8; ++col) {
                char pieceChar = board[row][col].toFEN();
                
                if (pieceChar == ' ')
                    emptyCount++;
                else {
                    if (emptyCount > 0) {
                        fen << emptyCount;
                        emptyCount = 0;
                    }
                    fen << pieceChar;
                }
            }
            
            if (emptyCount > 0)
                fen << emptyCount;
            
            if (row < 7)
                fen << '/';
        }
        
        fen << (whiteToMove ? " w " : " b ");
        
        bool anyCastling = false;
        if (whiteCanCastleKingside) 
            fen << 'K'; anyCastling = true;
        if (whiteCanCastleQueenside)
            fen << 'Q'; anyCastling = true;
        if (blackCanCastleKingside) 
            fen << 'k'; anyCastling = true;
        if (blackCanCastleQueenside) 
            fen << 'q'; anyCastling = true;
        if (!anyCastling)
            fen << '-';
        
        fen << ' ';
        if (enPassantTargetSquare[0] != -1 && enPassantTargetSquare[1] != -1)
            fen << static_cast<char>('a' + enPassantTargetSquare[1])
                << static_cast<char>('8' - enPassantTargetSquare[0]);
        else
            fen << '-';
        
        fen << ' ' << halfMoveClock;
        
        fen << ' ' << fullMoveNumber;
        
        return fen.str();
    }
    
    /**
     * @brief Loads the board from a FEN string.
     * @param fen The FEN string.
     * @return True if the FEN string was successfully loaded, false otherwise.
     * @throws std::invalid_argument if the FEN string is invalid.
     */
    bool loadFEN(const std::string& fen) {
        std::istringstream ss(fen);
        std::string placement; 
        std::string activeColour; 
        std::string castling; 
        std::string enPassant; 
        std::string halfMoves; 
        std::string fullMoves;
        
        ss >> placement >> activeColour >> castling >> enPassant >> halfMoves >> fullMoves;

        if (ss.fail())
            throw std::invalid_argument("Invalid FEN string: missing fields");
        
        for (int row = 0; row < 8; ++row)
            for (int col = 0; col < 8; ++col)
                board[row][col] = Piece();
        
        int row = 0, col = 0;
        for (char c: placement) {
            if (c == '/') {
                row++;
                col = 0;
            } else if (std::isdigit(c)) {
                col += c - '0';
            } else {
                PieceColour colour = std::isupper(c) ? PieceColour::White : PieceColour::Black;
                PieceType type;
                switch (std::tolower(c)) {
                    case 'p': 
                        type = PieceType::Pawn; 
                        break;
                    case 'n': 
                        type = PieceType::Knight; 
                        break;
                    case 'b': 
                        type = PieceType::Bishop; 
                        break;
                    case 'r': 
                        type = PieceType::Rook; 
                        break;
                    case 'q': 
                        type = PieceType::Queen; 
                        break;
                    case 'k': 
                        type = PieceType::King; 
                        break;
                    default: 
                        return false;
                }
                board[row][col] = Piece(type, colour);
                col++;
            }
        }
        
        whiteToMove = (activeColour == "w");
        
        whiteCanCastleKingside = (castling.find('K') != std::string::npos);
        whiteCanCastleQueenside = (castling.find('Q') != std::string::npos);
        blackCanCastleKingside = (castling.find('k') != std::string::npos);
        blackCanCastleQueenside = (castling.find('q') != std::string::npos);
        
        if (enPassant != "-") {
            enPassantTargetSquare[1] = enPassant[0] - 'a';
            enPassantTargetSquare[0] = '8' - enPassant[1];
        } else {
            enPassantTargetSquare[0] = -1;
            enPassantTargetSquare[1] = -1;
        }
        
        halfMoveClock = std::stoi(halfMoves);
        fullMoveNumber = std::stoi(fullMoves);
        
        return true;
    }
    
    /**
     * @brief Makes a move on the board.
     * @param move The move to make.
     * @return True if the move was successfully made, false otherwise.
     */
    bool makeMove(const Move& move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7 ||
            toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7)
            return false;

        Piece& fromPiece = board[fromRow][fromCol];
        Piece& toPiece = board[toRow][toCol];

        if ((whiteToMove && fromPiece.getColour() != PieceColour::White) ||
            (!whiteToMove && fromPiece.getColour() != PieceColour::Black))
            return false;

        bool isCapture = (toPiece.getType() != PieceType::Empty);

        bool isEnPassant = false;
        if (fromPiece.getType() == PieceType::Pawn && 
            fromCol != toCol && 
            toPiece.getType() == PieceType::Empty) {
            isEnPassant = true;
            board[fromRow][toCol] = Piece();
        }

        bool isCastling = false;
        if (fromPiece.getType() == PieceType::King && abs(fromCol - toCol) == 2) {
            isCastling = true;
            if (toCol > fromCol) {
                board[fromRow][7] = Piece();
                board[fromRow][5] = Piece(PieceType::Rook, fromPiece.getColour());
            } else {
                board[fromRow][0] = Piece();
                board[fromRow][3] = Piece(PieceType::Rook, fromPiece.getColour());
            }
        }

        if (fromPiece.getType() == PieceType::King) {
            if (fromPiece.getColour() == PieceColour::White) {
                whiteCanCastleKingside = false;
                whiteCanCastleQueenside = false;
            } else {
                blackCanCastleKingside = false;
                blackCanCastleQueenside = false;
            }
        }

        if (fromPiece.getType() == PieceType::Rook) {
            if (fromRow == 7 && fromCol == 0)
                whiteCanCastleQueenside = false;
            else if (fromRow == 7 && fromCol == 7)
                whiteCanCastleKingside = false;
            else if (fromRow == 0 && fromCol == 0)
                blackCanCastleQueenside = false;
            else if (fromRow == 0 && fromCol == 7)
                blackCanCastleKingside = false;
        }

        bool isPromotion = false;
        if (fromPiece.getType() == PieceType::Pawn && (toRow == 0 || toRow == 7)) {
            isPromotion = true;

            PieceType promotionType = PieceType::Queen;
            if (move.getIsPromotion()) {
                char promotionChar = std::tolower(move.getPromotionPiece());
                switch (promotionChar) {
                    case 'n': 
                        promotionType = PieceType::Knight; 
                        break;
                    case 'b': 
                        promotionType = PieceType::Bishop; 
                        break;
                    case 'r': 
                        promotionType = PieceType::Rook; 
                        break;
                    default: 
                        promotionType = PieceType::Queen; 
                        break;
                }
            }
            fromPiece = Piece(promotionType, fromPiece.getColour());
        }

        if (fromPiece.getType() == PieceType::Pawn && abs(fromRow - toRow) == 2) {
            enPassantTargetSquare[0] = (fromRow + toRow) / 2;
            enPassantTargetSquare[1] = fromCol;
        } else {
            enPassantTargetSquare[0] = -1;
            enPassantTargetSquare[1] = -1;
        }

        board[toRow][toCol] = fromPiece;
        board[fromRow][fromCol] = Piece();

        if (fromPiece.getType() == PieceType::Pawn || isCapture)
            halfMoveClock = 0;
        else
            halfMoveClock++;

        if (!whiteToMove)
            fullMoveNumber++;

        whiteToMove = !whiteToMove;

        return true;
    }
    
    /** 
     * @brief Gets the piece at the specified position on the board.
     * @param row The row of the piece.
     * @param col The column of the piece.
     * @return The piece at the specified position.
     */
    Piece getPieceAt(int row, int col) const {
        if (row >= 0 && row < 8 && col >= 0 && col < 8)
            return board[row][col];
        return Piece();
    }
    
    /**
     * @brief Gets a string representation of the board.
     * @return The string representation of the board.
     */
    std::string getBoardString() const {
        std::stringstream ss;
        
        ss << "  a b c d e f g h\n";
        for (int row = 0; row < 8; ++row) {
            ss << std::format("{} ", (8 - row));
            for (int col = 0; col < 8; ++col)
                ss << std::format("{} ", board[row][col].toChar());
            ss << std::format("{}\n", (8 - row));
        }
        ss << "  a b c d e f g h\n";
        
        return ss.str();
    }
    
    /**
     * @brief Checks if it is white's turn to move.
     * @return True if it is white's turn to move, false otherwise.
     */
    bool isWhiteToMove() const { 
        return whiteToMove; 
    }
    
    /**
     * @brief Checks if the specified colour is in check.
     * @param colour The colour to check.
     * @return True if the specified colour is in check, false otherwise.
     */
    bool isInCheck(PieceColour colour) const {
        int kingRow = -1, kingCol = -1;
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                Piece piece = board[row][col];
                if (piece.getType() == PieceType::King && piece.getColour() == colour) {
                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }
            if (kingRow != -1) 
                break;
        }

        if (kingRow == -1) 
            return false;

        PieceColour opponentColour = (colour == PieceColour::White) ? PieceColour::Black : PieceColour::White;

        int pawnDirection = (colour == PieceColour::White) ? -1 : 1;
        if (kingRow + pawnDirection >= 0 && kingRow + pawnDirection < 8) {
            if (kingCol - 1 >= 0 && board[kingRow + pawnDirection][kingCol - 1].getType() == PieceType::Pawn &&
                board[kingRow + pawnDirection][kingCol - 1].getColour() == opponentColour)
                return true;
            if (kingCol + 1 < 8 && board[kingRow + pawnDirection][kingCol + 1].getType() == PieceType::Pawn &&
                board[kingRow + pawnDirection][kingCol + 1].getColour() == opponentColour)
                return true;
        }

        for (const auto& [dr, dc]: Moves::KnightMoves) {
            int newRow = kingRow + dr;
            int newCol = kingCol + dc;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 &&
                board[newRow][newCol].getType() == PieceType::Knight &&
                board[newRow][newCol].getColour() == opponentColour)
                return true;
        }

        for (const auto& [dr, dc]: Moves::Directions) {
            int newRow = kingRow + dr;
            int newCol = kingCol + dc;
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece piece = board[newRow][newCol];
                if (piece.getType() != PieceType::Empty)
                    if (piece.getColour() == opponentColour &&
                        ((piece.getType() == PieceType::Rook && (dr == 0 || dc == 0)) ||
                         (piece.getType() == PieceType::Bishop && (dr != 0 && dc != 0)) ||
                          piece.getType() == PieceType::Queen))
                        return true;
                    break;
                newRow += dr;
                newCol += dc;
            }
        }

        for (const auto& [dr, dc]: Moves::KingMoves) {
            int newRow = kingRow + dr;
            int newCol = kingCol + dc;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8 &&
                board[newRow][newCol].getType() == PieceType::King &&
                board[newRow][newCol].getColour() == opponentColour)
                return true;
        }

        return false;
    }
    
    /**
     * @brief Gets all legal moves for the current player.
     * @return A vector of legal moves.
     */
    std::vector<Move> getLegalMoves() const {
        std::vector<Move> legalMoves;
        PieceColour currentPlayer = whiteToMove ? PieceColour::White : PieceColour::Black;

        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                Piece piece = board[row][col];
                if (piece.getColour() == currentPlayer) {
                    switch (piece.getType()) {
                        case PieceType::Pawn:
                            generatePawnMoves(row, col, legalMoves);
                            break;
                        case PieceType::Knight:
                            generateKnightMoves(row, col, legalMoves);
                            break;
                        case PieceType::Bishop:
                            generateBishopMoves(row, col, legalMoves);
                            break;
                        case PieceType::Rook:
                            generateRookMoves(row, col, legalMoves);
                            break;
                        case PieceType::Queen:
                            generateQueenMoves(row, col, legalMoves);
                            break;
                        case PieceType::King:
                            generateKingMoves(row, col, legalMoves);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return legalMoves;
    }

    /**
     * @brief Generates all legal moves for a pawn at the specified position.
     * @param row The row of the pawn.
     * @param col The column of the pawn.
     * @param legalMoves The vector to store the legal moves.
     */
    void generatePawnMoves(int row, int col, std::vector<Move>& legalMoves) const {
        Piece pawn = board[row][col];
        PieceColour colour = pawn.getColour();
        int direction = (colour == PieceColour::White) ? -1 : 1;
        int startRow = (colour == PieceColour::White) ? 6 : 1;
        int promotionRow = (colour == PieceColour::White) ? 0 : 7;

        int newRow = row + direction;
        if (newRow >= 0 && newRow < 8 && board[newRow][col].getType() == PieceType::Empty) {
            if (newRow == promotionRow) {
                legalMoves.push_back(Move(row, col, newRow, col, 'q'));
                legalMoves.push_back(Move(row, col, newRow, col, 'r'));
                legalMoves.push_back(Move(row, col, newRow, col, 'b'));
                legalMoves.push_back(Move(row, col, newRow, col, 'n'));
            } else {
                legalMoves.push_back(Move(row, col, newRow, col));
            }

            if (row == startRow && board[newRow + direction][col].getType() == PieceType::Empty)
                legalMoves.push_back(Move(row, col, newRow + direction, col));
        }

        for (int dc: {-1, 1}) {
            int newCol = col + dc;
            if (newCol >= 0 && newCol < 8) {
                Piece target = board[newRow][newCol];
                if (target.getType() != PieceType::Empty && target.getColour() != colour) {
                    if (newRow == promotionRow) {
                        legalMoves.push_back(Move(row, col, newRow, newCol, 'q'));
                        legalMoves.push_back(Move(row, col, newRow, newCol, 'r'));
                        legalMoves.push_back(Move(row, col, newRow, newCol, 'b'));
                        legalMoves.push_back(Move(row, col, newRow, newCol, 'n'));
                    } else {
                        legalMoves.push_back(Move(row, col, newRow, newCol));
                    }
                }
            }
        }

        if (enPassantTargetSquare[0] == newRow && std::abs(enPassantTargetSquare[1] - col) == 1)
            legalMoves.push_back(Move(row, col, newRow, enPassantTargetSquare[1], true));
    }

    /**
     * @brief Generates all legal moves for a knight at the specified position.
     * @param row The row of the knight.
     * @param col The column of the knight.
     * @param legalMoves The vector to store the legal moves.
     */
    void generateKnightMoves(int row, int col, std::vector<Move>& legalMoves) const {
        Piece knight = board[row][col];
        PieceColour colour = knight.getColour();

        for (const auto& [dr, dc]: Moves::KnightMoves) {
            int newRow = row + dr;
            int newCol = col + dc;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board[newRow][newCol];
                if (target.getType() == PieceType::Empty || target.getColour() != colour)
                    legalMoves.push_back(Move(row, col, newRow, newCol));
            }
        }
    }

    /**
     * @brief Generates all legal moves for a bishop at the specified position.
     * @param row The row of the bishop.
     * @param col The column of the bishop.
     * @param legalMoves The vector to store the legal moves.
     */
    void generateBishopMoves(int row, int col, std::vector<Move>& legalMoves) const {
        Piece bishop = board[row][col];
        PieceColour colour = bishop.getColour();

        for (const auto& [dr, dc]: Moves::Directions) {
            if (std::abs(dr) != std::abs(dc)) 
                continue;

            int newRow = row + dr;
            int newCol = col + dc;
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board[newRow][newCol];
                if (target.getType() == PieceType::Empty) {
                    legalMoves.push_back(Move(row, col, newRow, newCol));
                } else {
                    if (target.getColour() != colour)
                        legalMoves.push_back(Move(row, col, newRow, newCol));
                    break;
                }
                newRow += dr;
                newCol += dc;
            }
        }
    }

    /**
     * @brief Generates all legal moves for a rook at the specified position.
     * @param row The row of the rook.
     * @param col The column of the rook.
     * @param legalMoves The vector to store the legal moves.
     */
    void generateRookMoves(int row, int col, std::vector<Move>& legalMoves) const {
        Piece rook = board[row][col];
        PieceColour colour = rook.getColour();

        for (const auto& [dr, dc] : Moves::Directions) {
            if (dr != 0 && dc != 0) 
                continue;

            int newRow = row + dr;
            int newCol = col + dc;
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board[newRow][newCol];
                if (target.getType() == PieceType::Empty) {
                    legalMoves.push_back(Move(row, col, newRow, newCol));
                } else {
                    if (target.getColour() != colour)
                        legalMoves.push_back(Move(row, col, newRow, newCol));
                    break; 
                }
                newRow += dr;
                newCol += dc;
            }
        }
    }

    /**
     * @brief Generates all legal moves for a queen at the specified position.
     * @param row The row of the queen.
     * @param col The column of the queen.
     * @param legalMoves The vector to store the legal moves.
     */
    void generateQueenMoves(int row, int col, std::vector<Move>& legalMoves) const {
        Piece queen = board[row][col];
        PieceColour colour = queen.getColour();

        for (const auto& [dr, dc]: Moves::Directions) {
            int newRow = row + dr;
            int newCol = col + dc;
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board[newRow][newCol];
                if (target.getType() == PieceType::Empty) {
                    legalMoves.push_back(Move(row, col, newRow, newCol));
                } else {
                    if (target.getColour() != colour)
                        legalMoves.push_back(Move(row, col, newRow, newCol));
                    break;
                }
                newRow += dr;
                newCol += dc;
            }
        }
    }

    /**
     * @brief Generates all legal moves for a king at the specified position.
     * @param row The row of the king.
     * @param col The column of the king.
     * @param legalMoves The vector to store the legal moves.
     */
    void generateKingMoves(int row, int col, std::vector<Move>& legalMoves) const {
        Piece king = board[row][col];
        PieceColour colour = king.getColour();

        for (const auto& [dr, dc]: Moves::KingMoves) {
            int newRow = row + dr;
            int newCol = col + dc;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                Piece target = board[newRow][newCol];
                if (target.getType() == PieceType::Empty || target.getColour() != colour)
                    legalMoves.push_back(Move(row, col, newRow, newCol));
            }
        }

        if (colour == PieceColour::White) {
            if (whiteCanCastleKingside && board[7][5].getType() == PieceType::Empty && board[7][6].getType() == PieceType::Empty)
                legalMoves.push_back(Move(row, col, 7, 6));
            if (whiteCanCastleQueenside && board[7][1].getType() == PieceType::Empty && board[7][2].getType() == PieceType::Empty && board[7][3].getType() == PieceType::Empty)
                legalMoves.push_back(Move(row, col, 7, 2));
        } else {
            if (blackCanCastleKingside && board[0][5].getType() == PieceType::Empty && board[0][6].getType() == PieceType::Empty)
                legalMoves.push_back(Move(row, col, 0, 6));
            if (blackCanCastleQueenside && board[0][1].getType() == PieceType::Empty && board[0][2].getType() == PieceType::Empty && board[0][3].getType() == PieceType::Empty)
                legalMoves.push_back(Move(row, col, 0, 2));
        }
    }

    /**
     * @brief Evaluates the board and returns a score.
     * @return The evaluation score of the board.
     */
    int evaluate() const {
        int score = 0;

        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                Piece piece = board[row][col];
                if (piece.getType() != PieceType::Empty) {
                    int pieceValue = 0;
                    int positionalValue = 0;
                    switch (piece.getType()) {
                        case PieceType::Pawn: 
                            pieceValue = Values::PawnValue; 
                            positionalValue = Tables::PawnTable[row][col];
                            break;
                        case PieceType::Knight: 
                            pieceValue = Values::KnightValue; 
                            positionalValue = Tables::KnightTable[row][col];
                            break;
                        case PieceType::Bishop: 
                            pieceValue = Values::BishopValue; 
                            positionalValue = Tables::BishopTable[row][col];
                            break;
                        case PieceType::Rook: 
                            pieceValue = Values::RookValue; 
                            positionalValue = Tables::RookTable[row][col];
                            break;
                        case PieceType::Queen: 
                            pieceValue = Values::QueenValue; 
                            positionalValue = Tables::QueenTable[row][col];
                            break;
                        case PieceType::King: 
                            pieceValue = Values::KingValue; 
                            positionalValue = Tables::KingTable[row][col];
                            break;
                        default: 
                            break;
                    }
                    score += (piece.getColour() == PieceColour::White) ? (pieceValue + positionalValue) : -(pieceValue + positionalValue);
                }
            }
        }

        return score;
    }

    /**
     * @brief Performs the alpha-beta pruning algorithm to evaluate the best move.
     * @param depth The depth of the search.
     * @param alpha The alpha value for pruning.
     * @param beta The beta value for pruning.
     * @param maximisingPlayer True if the current player is maximising, false if minimising.
     * @return The evaluation score of the best move.
     */
    int alphaBeta(int depth, int alpha, int beta, bool maximisingPlayer) {
        if (depth == 0)
            return evaluate();

        std::vector<Move> legalMoves = getLegalMoves();
        if (legalMoves.empty())
            return (isInCheck(maximisingPlayer ? PieceColour::White : PieceColour::Black)) ? -10000 : 0;

        if (maximisingPlayer) {
            int maxEval = -10000;
            for (const Move& move: legalMoves) {
                Board newBoard = *this;
                newBoard.makeMove(move);
                int eval = newBoard.alphaBeta(depth - 1, alpha, beta, false);
                maxEval = std::max(maxEval, eval);
                alpha = std::max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        } else {
            int minEval = 10000;
            for (const Move& move: legalMoves) {
                Board newBoard = *this;
                newBoard.makeMove(move);
                int eval = newBoard.alphaBeta(depth - 1, alpha, beta, true);
                minEval = std::min(minEval, eval);
                beta = std::min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }
};