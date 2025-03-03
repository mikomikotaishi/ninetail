/**
 * @file Card.cppm
 * @brief Definition of the Card structure and related enums.
 */

export module poker.Card;

/**
 * @enum Rank
 * @brief Enum representing the rank of a card.
 */
export enum class Rank: int {
    Two = 2, ///< Rank 2
    Three, ///< Rank 3
    Four, ///< Rank 4
    Five, ///< Rank 5
    Six, ///< Rank 6
    Seven, ///< Rank 7
    Eight, ///< Rank 8
    Nine, ///< Rank 9
    Ten, ///< Rank 10
    Jack, ///< Rank Jack
    Queen, ///< Rank Queen
    King, ///< Rank King
    Ace ///< Rank Ace
};

/**
 * @enum Suit
 * @brief Enum representing the suit of a card.
 */
export enum class Suit: int {
    Clubs, ///< Suit Clubs
    Diamonds, ///< Suit Diamonds
    Hearts, ///< Suit Hearts
    Spades ///< Suit Spades
};

/**
 * @struct Card
 * @brief A structure representing a playing card.
 */
export struct Card {
    Rank rank; ///< The rank of the card
    Suit suit; ///< The suit of the card

    /**
     * @brief Equality operator for comparing two cards.
     * @param other The other card to compare with.
     * @return True if the cards are equal, false otherwise.
     */
    constexpr bool operator==(const Card& other) {
        return rank == other.rank && suit == other.suit;
    }
};