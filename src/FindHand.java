import java.util.*;

public class FindHand {

    public static Card findHighCard(Player player) {
        if (player.getHand().get(0).rankValue() > player.getHand().get(1).rankValue())
            return player.getHand().get(0);
        else return player.getHand().get(1);
    }

    public static HandMeta straightFlush(List<Card> cards, Player player) {
        HandMeta meta = straight(cards, player); // First, check if there's a straight

        if (meta.hand() == Hand.STRAIGHT) {
            List<Card> allCards = new ArrayList<>(cards);
            allCards.addAll(player.getHand());

            allCards.sort(Comparator.comparingInt(Card::rankValue));

            List<Card> uniqueCards = new ArrayList<>();
            for (Card card : allCards) {
                if (uniqueCards.stream().noneMatch(c -> c.rankValue() == card.rankValue())) {
                    uniqueCards.add(card);
                }
            }

            // We already know we have a straight, now check if all cards in the straight are of the same suit
            for (int i = 0; i <= uniqueCards.size() - 5; i++) {
                List<Card> subList = uniqueCards.subList(i, i + 5);
                if (isConsecutive(subList)) {
                    Suit firstSuit = subList.get(0).suit(); // Get the suit of the first card
                    boolean isSameSuit = subList.stream().allMatch(card -> card.suit() == firstSuit);

                    if (isSameSuit) {
                        meta.setHand(Hand.STRAIGHT_FLUSH); // Set to straight flush if all cards are the same suit
                        meta.setHighCard(subList.get(subList.size() - 1)); // Set the high card
                        return meta;
                    }
                }
            }
        }
        return meta; // Return the original hand if not a straight flush
    }

    // Function to check for a royal flush
    public static HandMeta royalFlush(List<Card> cards, Player player) {
        HandMeta meta = straightFlush(cards, player); // First, check if there's a straight flush

        if (meta.hand() == Hand.STRAIGHT_FLUSH) {
            List<Card> allCards = new ArrayList<>(cards);
            allCards.addAll(player.getHand());

            allCards.sort(Comparator.comparingInt(Card::rankValue));

            List<Card> uniqueCards = new ArrayList<>();
            for (Card card : allCards) {
                if (uniqueCards.stream().noneMatch(c -> c.rankValue() == card.rankValue())) {
                    uniqueCards.add(card);
                }
            }

            // Check if the straight flush is a royal flush (10, J, Q, K, A)
            List<Integer> royalRanks = Arrays.asList(10, 11, 12, 13, 14); // 10, J, Q, K, A
            List<Card> royalCards = uniqueCards.stream().filter(card -> royalRanks.contains(card.rankValue())).toList();

            if (royalCards.size() == 5) {
                Suit firstSuit = royalCards.get(0).suit(); // Check if all cards are of the same suit
                boolean isSameSuit = royalCards.stream().allMatch(card -> card.suit() == firstSuit);

                if (isSameSuit) {
                    meta.setHand(Hand.ROYAL_FLUSH); // Set to royal flush if it's a royal straight flush
                    meta.setHighCard(royalCards.get(royalCards.size() - 1)); // High card is the Ace
                }
            }
        }

        return meta; // Return the result (either a royal flush or a straight flush)
    }

    public static HandMeta straight(List<Card> cards, Player player) {
        List<Card> allCards = new ArrayList<>(cards);
        allCards.addAll(player.getHand());

        allCards.sort(Comparator.comparingInt(Card::rankValue));

        List<Card> uniqueCards = new ArrayList<>();
        for (Card card : allCards) {
            if (uniqueCards.stream().noneMatch(c -> c.rankValue() == card.rankValue())) {
                uniqueCards.add(card);
            }
        }

        boolean straight = false;
        Card highCardInStraight = null;

        if (uniqueCards.size() >= 5) {
            for (int i = 0; i <= uniqueCards.size() - 5; i++) {
                List<Card> subList = uniqueCards.subList(i, i + 5);
                if (isConsecutive(subList)) {
                    straight = true;
                    highCardInStraight = subList.get(subList.size() - 1); // High card in straight
                    break;
                }
            }
        }

        if (!straight && uniqueCards.stream().anyMatch(c -> c.rankValue() == 14) && // Ace
                uniqueCards.stream().anyMatch(c -> c.rankValue() == 2) &&
                uniqueCards.stream().anyMatch(c -> c.rankValue() == 3) &&
                uniqueCards.stream().anyMatch(c -> c.rankValue() == 4) &&
                uniqueCards.stream().anyMatch(c -> c.rankValue() == 5)) {

            straight = true;
            highCardInStraight = uniqueCards.stream()
                    .filter(c -> c.rankValue() == 5)
                    .findFirst().orElse(null);
        }

        HandMeta meta = new HandMeta(Hand.HIGH_CARD, findHighCard(player));

        if (straight && highCardInStraight != null) {
            meta.setHand(Hand.STRAIGHT);
            meta.setHighCard(highCardInStraight);
        }

        return meta;
    }

    // Helper method to check if a list of Card objects have consecutive ranks
    private static boolean isConsecutive(List<Card> cards) {
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).rankValue() + 1 != cards.get(i + 1).rankValue()) {
                return false; // Not consecutive
            }
        }
        return true; // All consecutive
    }

    public static HandMeta twoPair(List<Card> cards, Player player) {
        Map<Integer, Integer> cardMap = new HashMap<>();
        Map<Integer, Integer> playerCardMap = new HashMap<>();
        HandMeta meta = new HandMeta(Hand.HIGH_CARD, findHighCard(player));

        // Populate cardMap from the board cards
        for (Card card : cards) {
            cardMap.put(card.rankValue(), cardMap.getOrDefault(card.rankValue(), 0) + 1);
        }

        // Populate playerCardMap from the player's hand
        for (Card card : player.getHand()) {
            playerCardMap.put(card.rankValue(), playerCardMap.getOrDefault(card.rankValue(), 0) + 1);
        }

        int pair1 = 0;
        int pair2 = 0;

        // First loop to find and remove the first pair
        Iterator<Map.Entry<Integer, Integer>> iterator = cardMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            Integer boardCard = entry.getKey();
            if (playerCardMap.containsKey(boardCard)) {
                if (cardMap.get(boardCard) + playerCardMap.get(boardCard) == 2) {
                    pair1 = boardCard;
                    iterator.remove(); // Remove using iterator
                    playerCardMap.remove(boardCard); // Remove from playerCardMap
                    break; // Exit the loop after finding the first pair
                }
            }
        }

        // Second loop to find and remove the second pair
        iterator = cardMap.entrySet().iterator(); // Create a new iterator for the second loop
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            Integer boardCard = entry.getKey();
            if (playerCardMap.containsKey(boardCard)) {
                if (cardMap.get(boardCard) + playerCardMap.get(boardCard) == 2) {
                    pair2 = boardCard;
                    iterator.remove(); // Remove using iterator
                    playerCardMap.remove(boardCard); // Remove from playerCardMap
                    break; // Exit the loop after finding the second pair
                }
            }
        }

        // Check if both pairs were found
        if (pair1 != 0 && pair2 != 0) {
            meta.setHand(Hand.TWO_PAIR);
        }

        return meta;
    }

    public static HandMeta pair(List<Card> cards, Player player) {

        Map<Integer, Integer> cardNumMap = new HashMap<>();
        Map<Integer, Integer> playerCardNumMap = new HashMap<>();
        HandMeta meta = new HandMeta(Hand.HIGH_CARD, findHighCard(player));

        for (Card card : cards) {
            if (cardNumMap.containsKey(card.rankValue())) {
                cardNumMap.put(card.rankValue(), cardNumMap.get(card.rankValue()) + 1);
            } else {
                cardNumMap.put(card.rankValue(), 1);
            }
        }
        for (Card card : player.getHand()) {
            if (playerCardNumMap.containsKey(card.rankValue())) {
                playerCardNumMap.put(card.rankValue(), playerCardNumMap.get(card.rankValue()) + 1);
            } else {
                playerCardNumMap.put(card.rankValue(), 1);
            }
        }

        for (int num : cardNumMap.keySet()) {

            if (playerCardNumMap.containsKey(num)) {
                if (cardNumMap.get(num) + playerCardNumMap.get(num) == 2) {
                    meta.setHand(Hand.PAIR);
                    if (playerCardNumMap.get(num) > 0) {
                        meta.setHighCard(player.getHand().get(0).rankValue() == num ?
                                player.getHand().get(0) :
                                player.getHand().get(1));
                    } else {
                        for (Card card : cards) {
                            if (card.rankValue() == num) meta.setHighCard(card);
                        }
                    }
                }
            }
        }

        return meta;
    }
    public static HandMeta trips(List<Card> cards, Player player) {

        Map<Integer, Integer> cardNumMap = new HashMap<>();
        Map<Integer, Integer> playerCardNumMap = new HashMap<>();
        HandMeta meta = new HandMeta(Hand.HIGH_CARD, findHighCard(player));

        for (Card card : cards) {
            if (cardNumMap.containsKey(card.rankValue())) {
                cardNumMap.put(card.rankValue(), cardNumMap.get(card.rankValue()) + 1);
            } else {
                cardNumMap.put(card.rankValue(), 1);
            }
        }
        for (Card card : player.getHand()) {
            if (playerCardNumMap.containsKey(card.rankValue())) {
                playerCardNumMap.put(card.rankValue(), playerCardNumMap.get(card.rankValue()) + 1);
            } else {
                playerCardNumMap.put(card.rankValue(), 1);
            }
        }

        for (int num : cardNumMap.keySet()) {

            if (playerCardNumMap.containsKey(num)) {
                if (cardNumMap.get(num) + playerCardNumMap.get(num) == 3) {
                    meta.setHand(Hand.SET);
                    if (playerCardNumMap.get(num) > 0) {
                        meta.setHighCard(player.getHand().get(0).rankValue() == num ?
                                player.getHand().get(0) :
                                player.getHand().get(1));
                    } else {
                        for (Card card : cards) {
                            if (card.rankValue() == num) meta.setHighCard(card);
                        }
                    }
                }
            }
        }

        return meta;
    }
    public static HandMeta flush(List<Card> cards, Player player) {
        Map<Suit, Integer> suitMap = new HashMap<>();
        Map<Suit, Integer> playerSuitMap = new HashMap<>();


        for (Card card : cards) {
            Suit suit = card.suit();
            if (suitMap.containsKey(suit)) {
                suitMap.put(suit, suitMap.get(suit) + 1);
            } else {
                suitMap.put(suit, 1);
            }
        }
        for (Card card : player.getHand()) {
            Suit suit = card.suit();
            if (playerSuitMap.containsKey(suit)) {
                playerSuitMap.put(suit, playerSuitMap.get(suit) + 1);
            } else {
                playerSuitMap.put(suit, 1);
            }
        }

        boolean flush = false;
        Card high = new Card(0,null);

        for (Suit suit : suitMap.keySet()) {
            if (playerSuitMap.containsKey(suit)) {
                if (suitMap.get(suit) + playerSuitMap.get(suit) >= 5) {
                    flush = true;
                    for (Card card : player.getHand()) {
                        if (card.rankValue() > high.rankValue() && card.suit() == suit)
                            high = card;
                    }
                }
            }
        }

        Hand hand = Hand.HIGH_CARD;
        if (flush)
            hand = Hand.FLUSH;

        return new HandMeta(hand, high);

    }
    public static HandMeta fullHouse(List<Card> cards, Player player) {

        Map<Integer, Integer> cardNumMap = new HashMap<>();
        Map<Integer, Integer> playerCardNumMap = new HashMap<>();
        HandMeta meta = new HandMeta(Hand.HIGH_CARD, findHighCard(player));

        for (Card card : cards) {
            if (cardNumMap.containsKey(card.rankValue())) {
                cardNumMap.put(card.rankValue(), cardNumMap.get(card.rankValue()) + 1);
            } else {
                cardNumMap.put(card.rankValue(), 1);
            }
        }
        for (Card card : player.getHand()) {
            if (playerCardNumMap.containsKey(card.rankValue())) {
                playerCardNumMap.put(card.rankValue(), playerCardNumMap.get(card.rankValue()) + 1);
            } else {
                playerCardNumMap.put(card.rankValue(), 1);
            }
        }

        for (int num : cardNumMap.keySet()) {

            if (playerCardNumMap.containsKey(num)) {
                if (cardNumMap.get(num) + playerCardNumMap.get(num) == 2) {
                    //   meta.setHand(Hand.PAIR);
                    if (playerCardNumMap.get(num) > 0) {
                        meta.setHighCard(player.getHand().get(0).rankValue() == num ?
                                player.getHand().get(0) :
                                player.getHand().get(1));
                    } else {
                        for (Card card : cards) {
                            if (card.rankValue() == num) meta.setHighCard(card);
                        }
                    }
                    HandMeta trips = trips(cards, player);
                    if (trips.hand() == Hand.SET) {
                        if (player.getHand().contains(trips.highCard())) {
                            meta.setHighCard(trips.highCard());
                        }
                        meta.setHand(Hand.FULL_HOUSE);
                    }
                }
            }
        }



        return meta;
    }
    public static HandMeta quads(List<Card> cards, Player player) {

        Map<Integer, Integer> cardNumMap = new HashMap<>();
        Map<Integer, Integer> playerCardNumMap = new HashMap<>();
        HandMeta meta = new HandMeta(Hand.HIGH_CARD, findHighCard(player));

        for (Card card : cards) {
            if (cardNumMap.containsKey(card.rankValue())) {
                cardNumMap.put(card.rankValue(), cardNumMap.get(card.rankValue()) + 1);
            } else {
                cardNumMap.put(card.rankValue(), 1);
            }
        }
        for (Card card : player.getHand()) {
            if (playerCardNumMap.containsKey(card.rankValue())) {
                playerCardNumMap.put(card.rankValue(), playerCardNumMap.get(card.rankValue()) + 1);
            } else {
                playerCardNumMap.put(card.rankValue(), 1);
            }
        }

        for (int num : cardNumMap.keySet()) {

            if (playerCardNumMap.containsKey(num)) {
                if (cardNumMap.get(num) + playerCardNumMap.get(num) == 4) {
                    meta.setHand(Hand.QUAD);
                    if (playerCardNumMap.get(num) > 0) {
                        meta.setHighCard(player.getHand().get(0).rankValue() == num ?
                                player.getHand().get(0) :
                                player.getHand().get(1));
                    } else {
                        for (Card card : cards) {
                            if (card.rankValue() == num) meta.setHighCard(card);
                        }
                    }
                }
            }
        }

        return meta;
    }
}
