/**
 * @file PokerEngine.cppm
 * @brief Implementation of the PokerEngine class for managing a poker game.
 */

module;

#include <vector>
#include <string>
#include <memory>
#include <stdexcept>
#include <sstream>
#include <utility>

export module poker.PokerEngine;

import poker.Card;
import poker.Deck;
import poker.HandEvaluator;

/**
 * @enum PlayerAction
 * @brief Enum representing the possible actions a player can take.
 */
export enum class PlayerAction {
    Fold, ///< Fold the hand
    Check, ///< Check (no bet)
    Call, ///< Call the current bet
    Raise, ///< Raise the current bet
    AllIn ///< Go all-in
};

/**
 * @enum GameState
 * @brief Enum representing the different states of the game.
 */
export enum class GameState {
    NotStarted, ///< Game has not started
    Preflop, ///< Preflop stage
    Flop, ///< Flop stage
    Turn, ///< Turn stage
    River, ///< River stage
    Showdown, ///< Showdown stage
    Complete ///< Game is complete
};

/**
 * @struct Player
 * @brief A structure representing a player in the poker game.
 */
export struct Player {
    int id; ///< Player ID
    int chips; ///< Number of chips the player has
    int currentBet; ///< Current bet of the player
    bool folded; ///< Whether the player has folded
    bool isAllIn; ///< Whether the player is all-in
    std::vector<Card> holeCards; ///< The player's hole cards

    /**
     * @brief Constructs a new Player object.
     * @param playerId The ID of the player.
     * @param startingChips The starting number of chips for the player.
     */
    Player(int playerId, int startingChips): 
        id(playerId), 
        chips(startingChips), 
        currentBet(0), 
        folded(false), 
        isAllIn(false) {}
};

/**
 * @class PokerEngine
 * @brief A class representing the poker game engine.
 */
export class PokerEngine {
private:
    Deck deck; ///< The deck of cards
    std::vector<Card> communityCards; ///< The community cards
    std::vector<Player> players; ///< The players in the game
    int potSize; ///< The size of the pot
    int currentBet; ///< The current bet
    int dealerPosition; ///< The position of the dealer
    int currentPlayerIndex; ///< The index of the current player
    int smallBlind; ///< The small blind amount
    int bigBlind; ///< The big blind amount
    GameState state; ///< The current state of the game
    bool actionComplete; ///< Whether the current action is complete

    /**
     * @brief Advances the game state to the next stage.
     */
    void advanceGameState() {
        switch (state) {
            case GameState::NotStarted:
                state = GameState::Preflop;
                dealCards();
                collectBlinds();
                break;
            case GameState::Preflop:
                state = GameState::Flop;
                dealCommunityCards(3);
                resetBettingRound();
                break;
            case GameState::Flop:
                state = GameState::Turn;
                dealCommunityCards(1);
                resetBettingRound();
                break;
            case GameState::Turn:
                state = GameState::River;
                dealCommunityCards(1);
                resetBettingRound();
                break;
            case GameState::River:
                state = GameState::Showdown;
                determineWinner();
                break;
            case GameState::Showdown:
                state = GameState::Complete;
                break;
            case GameState::Complete:
                resetGame();
                state = GameState::NotStarted;
                break;
        }
    }

    /**
     * @brief Deals two cards to each player.
     */
    void dealCards() {
        for (Player& player: players)
            player.holeCards.clear();

        for (int i = 0; i < 2; ++i)
            for (Player& player : players)
                player.holeCards.push_back(deck.draw());
    }

    /**
     * @brief Deals a specified number of community cards.
     * @param count The number of community cards to deal.
     */
    void dealCommunityCards(int count) {
        for (int i = 0; i < count; ++i)
            communityCards.push_back(deck.draw());
    }

    /**
     * @brief Collects the blinds from the players.
     */
    void collectBlinds() {
        int smallBlindPos = (dealerPosition + 1) % players.size();
        players[smallBlindPos].chips -= smallBlind;
        players[smallBlindPos].currentBet = smallBlind;
        
        int bigBlindPos = (dealerPosition + 2) % players.size();
        players[bigBlindPos].chips -= bigBlind;
        players[bigBlindPos].currentBet = bigBlind;

        potSize = smallBlind + bigBlind;
        currentBet = bigBlind;
        
        currentPlayerIndex = (bigBlindPos + 1) % players.size();
    }

    /**
     * @brief Resets the betting round.
     */
    void resetBettingRound() {
        for (Player& player: players)
            player.currentBet = 0;
        currentBet = 0;
        currentPlayerIndex = (dealerPosition + 1) % players.size();
        while (players[currentPlayerIndex].folded && !allPlayersFoldedExceptOne())
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        actionComplete = false;
    }

    /**
     * @brief Checks if all players except one have folded.
     * @return True if all players except one have folded, false otherwise.
     */
    bool allPlayersFoldedExceptOne() {
        int activePlayers = 0;
        for (const Player& player: players)
            if (!player.folded) 
                activePlayers++;
        return activePlayers <= 1;
    }

    /**
     * @brief Checks if all players have acted.
     * @return True if all players have acted, false otherwise.
     */
    bool allPlayersActed() {
        for (const Player& player: players) {
            if (player.folded) 
                continue;
            
            if (player.isAllIn) 
                continue;
            
            if (player.currentBet < currentBet)
                return false;
        }
        return true;
    }

    /**
     * @brief Moves to the next player.
     */
    void nextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while ((players[currentPlayerIndex].folded || players[currentPlayerIndex].isAllIn) && 
                 !allPlayersFoldedExceptOne() && 
                 !allPlayersActed());
        
        if (allPlayersActed() || allPlayersFoldedExceptOne())
            actionComplete = true;
    }

    /**
     * @brief Determines the winner of the hand.
     */
    void determineWinner() {
        if (allPlayersFoldedExceptOne()) {
            for (Player& player: players) {
                if (!player.folded) {
                    player.chips += potSize;
                    break;
                }
            }
            return;
        }

        int bestHandValue = -1;
        std::vector<int> winners;

        for (int i = 0; i < players.size(); ++i) {
            if (players[i].folded) 
                continue;

            std::vector<Card> fullHand = players[i].holeCards;
            fullHand.insert(fullHand.end(), communityCards.begin(), communityCards.end());

            std::vector<Card> bestFiveCards(fullHand.begin(), fullHand.begin() + 5);
            int handValue = HandEvaluator::evaluateHand(bestFiveCards);

            if (handValue > bestHandValue) {
                bestHandValue = handValue;
                winners.clear();
                winners.push_back(i);
            } else if (handValue == bestHandValue) {
                winners.push_back(i);
            }
        }

        int winningsPerPlayer = potSize / winners.size();
        for (int winnerIdx: winners)
            players[winnerIdx].chips += winningsPerPlayer;
    }

    /**
     * @brief Resets the game for a new hand.
     */
    void resetGame() {
        potSize = 0;
        currentBet = 0;
        communityCards.clear();
        dealerPosition = (dealerPosition + 1) % players.size();
        
        for (Player& player : players) {
            player.currentBet = 0;
            player.folded = false;
            player.isAllIn = false;
            player.holeCards.clear();
        }
        
        deck = Deck();
    }

public:
    /**
     * @brief Constructs a new PokerEngine object.
     * @param initialChips The initial number of chips for each player.
     * @param smallBlindAmount The amount of the small blind.
     * @param bigBlindAmount The amount of the big blind.
     */
    PokerEngine(int initialChips = 1000, int smallBlindAmount = 5, int bigBlindAmount = 10):
        potSize(0),
        currentBet(0),
        dealerPosition(0),
        currentPlayerIndex(0),
        smallBlind(smallBlindAmount),
        bigBlind(bigBlindAmount),
        state(GameState::NotStarted),
        actionComplete(false) {
        
        players.push_back(Player(0, initialChips));
        players.push_back(Player(1, initialChips));
    }

    /**
     * @brief Starts a new game.
     */
    void startGame() {
        if (state != GameState::NotStarted && state != GameState::Complete)
            throw std::runtime_error("Cannot start a new game while one is in progress");
        
        resetGame();
        advanceGameState();
    }

    /**
     * @brief Executes a player action.
     * @param action The action to execute.
     * @param amount The amount for the action (if applicable).
     * @return A string describing the result of the action.
     */
    std::string executeAction(PlayerAction action, int amount = 0) {
        if (currentPlayerIndex != 0)
            return "It's not your turn!";

        Player& currentPlayer = players[currentPlayerIndex];
        std::stringstream result;

        switch (action) {
            case PlayerAction::Fold:
                currentPlayer.folded = true;
                result << "You folded.";
                break;

            case PlayerAction::Check:
                if (currentBet > currentPlayer.currentBet)
                    return "Cannot check when there's a bet to call!";
                result << "You checked.";
                break;

            case PlayerAction::Call:
                if (currentBet <= currentPlayer.currentBet)
                    return "There's no bet to call!";
                if (currentBet - currentPlayer.currentBet >= currentPlayer.chips) {
                    potSize += currentPlayer.chips;
                    currentPlayer.currentBet += currentPlayer.chips;
                    currentPlayer.chips = 0;
                    currentPlayer.isAllIn = true;
                    result << "You called and are all-in!";
                } else {
                    int callAmount = currentBet - currentPlayer.currentBet;
                    potSize += callAmount;
                    currentPlayer.chips -= callAmount;
                    currentPlayer.currentBet = currentBet;
                    result << "You called " << callAmount << " chips.";
                }
                break;

            case PlayerAction::Raise:
                if (amount <= currentBet)
                    return "Raise amount must be greater than the current bet!";
                if (amount - currentPlayer.currentBet >= currentPlayer.chips) {
                    potSize += currentPlayer.chips;
                    currentPlayer.currentBet += currentPlayer.chips;
                    currentPlayer.chips = 0;
                    currentPlayer.isAllIn = true;
                    currentBet = currentPlayer.currentBet;
                    result << "You raised and are all-in!";
                } else {
                    int raiseAmount = amount - currentPlayer.currentBet;
                    potSize += raiseAmount;
                    currentPlayer.chips -= raiseAmount;
                    currentPlayer.currentBet = amount;
                    currentBet = amount;
                    result << "You raised to " << amount << " chips.";
                }
                break;

            case PlayerAction::AllIn:
                if (currentPlayer.chips == 0)
                    return "You don't have any chips to go all-in!";
                
                potSize += currentPlayer.chips;
                currentPlayer.currentBet += currentPlayer.chips;
                if (currentPlayer.currentBet > currentBet) {
                    currentBet = currentPlayer.currentBet;
                }
                currentPlayer.chips = 0;
                currentPlayer.isAllIn = true;
                result << "You went all-in for " << currentPlayer.currentBet << " chips!";
                break;
        }

        nextPlayer();
        
        if (actionComplete || allPlayersFoldedExceptOne()) {
            advanceGameState();
            
            while (state != GameState::Complete && currentPlayerIndex != 0) {
                executeAIAction();
                if (actionComplete || allPlayersFoldedExceptOne())
                    advanceGameState();
            }
        }

        return result.str();
    }

    /**
     * @brief Executes the AI player's action.
     */
    void executeAIAction() {
        Player& aiPlayer = players[currentPlayerIndex];
        
        if (currentBet <= aiPlayer.currentBet) {
            aiPlayer.currentBet = currentBet;
        } else {
            int callAmount = currentBet - aiPlayer.currentBet;
            
            if (callAmount < aiPlayer.chips) {
                potSize += callAmount;
                aiPlayer.chips -= callAmount;
                aiPlayer.currentBet = currentBet;
            } else {
                potSize += aiPlayer.chips;
                aiPlayer.currentBet += aiPlayer.chips;
                aiPlayer.chips = 0;
                aiPlayer.isAllIn = true;
            }
        }
        
        nextPlayer();
    }

    /**
     * @brief Gets the current game state as a string.
     * @return The current game state as a string.
     */
    std::string getGameState() const {
        std::stringstream ss;
        
        switch (state) {
            case GameState::NotStarted:
                ss << "Game not started yet.";
                return ss.str();
            case GameState::Preflop:
                ss << "*** PREFLOP ***\n";
                break;
            case GameState::Flop:
                ss << "*** FLOP ***\n";
                break;
            case GameState::Turn:
                ss << "*** TURN ***\n";
                break;
            case GameState::River:
                ss << "*** RIVER ***\n";
                break;
            case GameState::Showdown:
                ss << "*** SHOWDOWN ***\n";
                break;
            case GameState::Complete:
                ss << "Hand complete. Start a new hand.\n";
                return ss.str();
        }
        
        ss << std::format("Your chips: {}\n", players[0].chips);
        ss << std::format("AI chips: {}\n", players[1].chips);
        ss << std::format("Pot: {}\n", potSize);
        
        ss << "Your hand: ";
        for (const Card& card: players[0].holeCards)
            ss << std::format("{}{} ", rankToString(card.rank), suitToString(card.suit));
        ss << "\n";
        
        if (!communityCards.empty()) {
            ss << "Board: ";
            for (const Card& card: communityCards)
                ss << std::format("{}{} ", rankToString(card.rank), suitToString(card.suit));
            ss << "\n";
        }
        
        if (state != GameState::Showdown && state != GameState::Complete) {
            if (currentPlayerIndex == 0) {
                ss << "Your action (";
                if (currentBet <= players[0].currentBet)
                    ss << "check/bet";
                else
                    ss << std::format("call {}/raise/fold", (currentBet - players[0].currentBet));
                ss << ")";
            } else {
                ss << "Waiting for AI...";
            }
        }
        
        return ss.str();
    }

    /**
     * @brief Converts a rank to its string representation.
     * @param rank The rank to convert.
     * @return The string representation of the rank.
     */
    static std::string rankToString(Rank rank) {
        switch (rank) {
            case Rank::Two: 
                return "2";
            case Rank::Three: 
                return "3";
            case Rank::Four: 
                return "4";
            case Rank::Five: 
                return "5";
            case Rank::Six: 
                return "6";
            case Rank::Seven: 
                return "7";
            case Rank::Eight: 
                return "8";
            case Rank::Nine: 
                return "9";
            case Rank::Ten: 
                return "T";
            case Rank::Jack: 
                return "J";
            case Rank::Queen: 
                return "Q";
            case Rank::King: 
                return "K";
            case Rank::Ace: 
                return "A";
            default: 
                std::unreachable();
        }
    }

    /**
     * @brief Converts a suit to its string representation.
     * @param suit The suit to convert.
     * @return The string representation of the suit.
     */
    static std::string suitToString(Suit suit) {
        switch (suit) {
            case Suit::Clubs: 
                return "♣";
            case Suit::Diamonds: 
                return "♦";
            case Suit::Hearts: 
                return "♥";
            case Suit::Spades: 
                return "♠";
            default: 
                std::unreachable();
        }
    }
};