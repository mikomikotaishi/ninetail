/**
 * @file Deck.cppm
 * @brief Implementation of the Deck class for a standard deck of playing cards.
 */

module;

#include <algorithm>
#include <random>
#include <stdexcept>
#include <vector>

export module poker.Deck;

import poker.Card;

/**
 * @class Deck
 * @brief A class representing a deck of playing cards.
 */
export class Deck {
private:
    std::vector<Card> cards; ///< The cards in the deck.
    std::mt19937 rng; ///< Random number generator for shuffling the deck.
public:
    /**
     * @brief Constructs a new Deck object and initialises it with a standard 52-card deck.
     */
    Deck():
        rng{std::random_device{}()} {
        for (int suit = 0; suit < 4; ++suit)
            for (int rank = 2; rank <= 14; ++rank)
                cards.push_back({ static_cast<Rank>(rank), static_cast<Suit>(suit) });
        shuffle();
    }

    /**
     * @brief Shuffles the deck using a random number generator.
     */
    void shuffle() {
        std::shuffle(cards.begin(), cards.end(), rng);
    }

    /**
     * @brief Draws a card from the top of the deck.
     * @return The card drawn from the top of the deck.
     * @throws std::runtime_error if the deck is empty.
     */
    Card draw() {
        if (cards.empty())
            throw std::runtime_error("Deck is empty!");
        Card top = cards.back();
        cards.pop_back();
        return top;
    }
};