/**
 * @file Poker.cppm
 * @module bot.ninetail.game.poker
 * @brief Exports the poker engine interface for use with Java FFI.
 */

module;

#ifdef _WIN32 
    #define EXPORT_API __declspec(dllexport)
#else
    #define EXPORT_API [[gnu::visibility("default")]]
#endif

#include <format>
#include <memory>
#include <string>
#include <stdexcept>

export module bot.ninetail.game.poker;

import bot.ninetail.game.poker.Card;
import bot.ninetail.game.poker.Deck;
import bot.ninetail.game.poker.HandEvaluator;
import bot.ninetail.game.poker.PokerEngine;

export extern "C" {
    static std::unique_ptr<PokerEngine> pokerEngine = nullptr;

    /**
     * @brief Creates a new poker game.
     * @param startingChips The initial number of chips for each player.
     * @param smallBlind The amount of the small blind.
     * @param bigBlind The amount of the big blind.
     * @return True if the game was created successfully, false otherwise.
     */
    EXPORT_API bool createGame(int startingChips, int smallBlind, int bigBlind) {
        try {
            pokerEngine = std::make_unique<PokerEngine>(startingChips, smallBlind, bigBlind);
            return true;
        } catch (...) {
            pokerEngine = nullptr;
            return false;
        }
    }

    /**
     * @brief Destroys the poker game.
     */
    EXPORT_API void destroyGame() {
        pokerEngine.reset();
    }

    /**
     * @brief Starts a new hand in the poker game.
     * @return True if the hand was started successfully, false otherwise.
     */
    EXPORT_API bool startHand() {
        try {
            if (!pokerEngine) 
                return false;
            pokerEngine->startGame();
            return true;
        } catch (...) {
            return false;
        }
    }

    /**
     * @brief Gets the current game state as a string.
     * @return A string describing the current game state.
     */
    EXPORT_API const char* getGameState() {
        static std::string result;
        try {
            if (!pokerEngine) 
                return "Error: Invalid game pointer";
            result = pokerEngine->getGameState();
            return result.c_str();
        } catch (const std::exception& e) {
            result = std::format("Error: {}", std::string(e.what()));
            return result.c_str();
        } catch (...) {
            result = "Unknown error occurred";
            return result.c_str();
        }
    }

    /**
     * @brief Executes a player action.
     * @param action The action to execute (0: Fold, 1: Check, 2: Call, 3: Raise, 4: AllIn).
     * @param amount The amount for the action (if applicable).
     * @return A string describing the result of the action.
     */
    EXPORT_API const char* executeAction(int action, int amount) {
        static std::string result;
        try {
            if (!pokerEngine) 
                return "Error: Invalid game pointer";
            
            PlayerAction playerAction;
            switch (action) {
                case 0: 
                    playerAction = PlayerAction::Fold; 
                    break;
                case 1: 
                    playerAction = PlayerAction::Check; 
                    break;
                case 2: 
                    playerAction = PlayerAction::Call; 
                    break;
                case 3: 
                    playerAction = PlayerAction::Raise; 
                    break;
                case 4: 
                    playerAction = PlayerAction::AllIn; 
                    break;
                default: 
                    return "Error: Invalid action";
            }
            
            result = pokerEngine->executeAction(playerAction, amount);
            return result.c_str();
        } catch (const std::exception& e) {
            result = std::format("Error: {}", std::string(e.what()));
            return result.c_str();
        } catch (...) {
            result = "Unknown error occurred";
            return result.c_str();
        }
    }
}
