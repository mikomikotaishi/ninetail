/**
 * @file HandEvaluator.cppm
 * @module bot.ninetail.game.poker.HandEvaluator
 * @brief Implementation of the HandEvaluator namespace for evaluating poker hands.
 */

module;

#include <algorithm>
#include <map>
#include <vector>
#include <stdexcept>

export module bot.ninetail.game.poker.HandEvaluator;

import bot.ninetail.game.poker.Card;

/**
 * @enum HandRank
 * @brief Enum representing the rank of a poker hand.
 */
export enum class HandRank {
    HighCard = 0, ///< High Card
    OnePair = 1, ///< One Pair
    TwoPair = 2, ///< Two Pair
    ThreeOfAKind = 3, ///< Three of a Kind
    Straight = 4, ///< Straight
    Flush = 5, ///< Flush
    FullHouse = 6, ///< Full House
    FourOfAKind = 7, ///< Four of a Kind
    StraightFlush = 8, ///< Straight Flush
    RoyalFlush = 9 ///< Royal Flush
};

/**
 * @namespace HandEvaluator
 * @brief Namespace containing functions for evaluating poker hands.
 */
export namespace HandEvaluator {
    /**
     * @brief Evaluates a poker hand and returns its rank.
     * @param hand The poker hand to evaluate.
     * @return The rank of the hand.
     * @throws std::runtime_error if the hand does not contain exactly 5 cards.
     */
    [[nodiscard]]
    int evaluateHand(const std::vector<Card>& hand) {
        if (hand.size() != 5)
            throw std::runtime_error("Hand must contain exactly 5 cards!");

        std::map<Rank, int> rankCount;
        std::map<Suit, int> suitCount;
        
        for (const Card& card: hand) {
            ++rankCount[card.rank];
            ++suitCount[card.suit];
        }
        
        bool isFlush = false;
        for (const auto& [suit, count]: suitCount) {
            if (count == 5) {
                isFlush = true;
                break;
            }
        }
        
        std::vector<int> ranks;
        for (const auto& [rank, count]: rankCount) {
            ranks.push_back(static_cast<int>(rank));
        }

        std::sort(ranks.begin(), ranks.end(), [](const int a, const int b) -> bool {
            return a < b;
        });
        
        bool isStraight = false;
        if (ranks.size() == 5 && 
            ranks[0] == static_cast<int>(Rank::Two) && 
            ranks[1] == static_cast<int>(Rank::Three) && 
            ranks[2] == static_cast<int>(Rank::Four) && 
            ranks[3] == static_cast<int>(Rank::Five) && 
            ranks[4] == static_cast<int>(Rank::Ace)) {
            isStraight = true;
        } else if (ranks.size() == 5 && 
                   ranks[1] == ranks[0] + 1 && 
                   ranks[2] == ranks[0] + 2 && 
                   ranks[3] == ranks[0] + 3 && 
                   ranks[4] == ranks[0] + 4) {
            isStraight = true;
        }
        
        bool isRoyalFlush = false;
        if (isFlush && isStraight && 
            ranks[0] == static_cast<int>(Rank::Ten) && 
            ranks[4] == static_cast<int>(Rank::Ace)) {
            isRoyalFlush = true;
            return static_cast<int>(HandRank::RoyalFlush);
        }
        
        if (isFlush && isStraight) {
            return static_cast<int>(HandRank::StraightFlush);
        }
        
        for (const auto& [rank, count]: rankCount) {
            if (count == 4) {
                return static_cast<int>(HandRank::FourOfAKind);
            }
        }
        
        bool hasThree = false;
        bool hasPair = false;
        for (const auto& [rank, count]: rankCount) {
            if (count == 3) {
                hasThree = true;
            } else if (count == 2) {
                hasPair = true;
            }
        }
        if (hasThree && hasPair) {
            return static_cast<int>(HandRank::FullHouse);
        }
        
        if (isFlush) {
            return static_cast<int>(HandRank::Flush);
        }
        
        if (isStraight) {
            return static_cast<int>(HandRank::Straight);
        }
        
        if (hasThree) {
            return static_cast<int>(HandRank::ThreeOfAKind);
        }
        
        int pairCount = 0;
        for (const auto& [rank, count]: rankCount) {
            if (count == 2) {
                ++pairCount;
            }
        }
        if (pairCount == 2) {
            return static_cast<int>(HandRank::TwoPair);
        }
        
        if (pairCount == 1) {
            return static_cast<int>(HandRank::OnePair);
        }
        
        return static_cast<int>(HandRank::HighCard);
    }
}
