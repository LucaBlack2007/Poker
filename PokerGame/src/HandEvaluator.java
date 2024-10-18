import java.util.*;

public class HandEvaluator {

    public static Hand evaluateHand(List<Card> playerHand, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>(playerHand);
        allCards.addAll(communityCards);

        if (isRoyalFlush(allCards)) return Hand.ROYAL_FLUSH;
        if (isStraightFlush(allCards)) return Hand.STRAIGHT_FLUSH;
        if (isFourOfAKind(allCards)) return Hand.QUAD;
        if (isFullHouse(allCards)) return Hand.FULL_HOUSE;
        if (isFlush(allCards)) return Hand.FLUSH;
        if (isStraight(allCards)) return Hand.STRAIGHT;
        if (isThreeOfAKind(allCards)) return Hand.TRIPS;
        if (isTwoPair(allCards)) return Hand.TWO_PAIR;
        if (isPair(allCards)) return Hand.PAIR;
        return Hand.HIGH_CARD;
    }

    public static Map.Entry<Player, Hand> findWinner(Map<Player, Hand> playerHands) {
        Player bestPlayer = null;
        Hand bestHand = null;
        List<Player> tiedPlayers = new ArrayList<>();

        for (Map.Entry<Player, Hand> entry : playerHands.entrySet()) {
            Player player = entry.getKey();
            Hand hand = entry.getValue();

            if (bestHand == null || hand.getLevel() > bestHand.getLevel()) {
                bestHand = hand;
                bestPlayer = player;
                tiedPlayers.clear();
                tiedPlayers.add(player);
            } else if (hand.getLevel() == bestHand.getLevel()) {
                int comparison = compareHands(player.getHand(), bestPlayer.getHand(), hand);
                if (comparison > 0) {
                    bestPlayer = player;
                    bestHand = hand;
                    tiedPlayers.clear();
                    tiedPlayers.add(player);
                } else if (comparison == 0) {
                    tiedPlayers.add(player); // push
                }
            }
        }

        if (tiedPlayers.size() > 1) { // push
            return null;
        }

        return new AbstractMap.SimpleEntry<>(bestPlayer, bestHand);
    }



    private static int compareHands(List<Card> hand1, List<Card> hand2, Hand handType) {
        switch (handType) {
            case FLUSH:
                return compareFlush(hand1, hand2);
            case STRAIGHT:
            case STRAIGHT_FLUSH:
                return compareStraight(hand1, hand2);
            case PAIR:
            case TWO_PAIR:
            case TRIPS:
                return comparePairs(hand1, hand2);
            case HIGH_CARD:
                return compareHighCard(hand1, hand2);
            default:
                return 0;
        }
    }

    private static int compareFlush(List<Card> flush1, List<Card> flush2) {
        List<Integer> flush1Ranks = new ArrayList<>();
        List<Integer> flush2Ranks = new ArrayList<>();

        for (Card card : flush1) flush1Ranks.add(card.rankValue());
        for (Card card : flush2) flush2Ranks.add(card.rankValue());

        Collections.sort(flush1Ranks, Collections.reverseOrder());
        Collections.sort(flush2Ranks, Collections.reverseOrder());

        for (int i = 0; i < Math.min(flush1Ranks.size(), flush2Ranks.size()); i++) {
            if (!flush1Ranks.get(i).equals(flush2Ranks.get(i))) {
                return flush1Ranks.get(i) - flush2Ranks.get(i);
            }
        }

        return 0; // Hands are tied
    }

    private static int compareStraight(List<Card> straight1, List<Card> straight2) {
        int highCard1 = getHighCardInStraight(straight1);
        int highCard2 = getHighCardInStraight(straight2);
        return Integer.compare(highCard1, highCard2);
    }

    private static int getHighCardInStraight(List<Card> hand) {
        List<Integer> ranks = new ArrayList<>();
        for (Card card : hand) {
            ranks.add(card.rankValue());
        }
        Collections.sort(ranks);

        if (ranks.contains(14) && ranks.contains(2)) {
            return 5;
        }
        return ranks.get(ranks.size() - 1);
    }

    private static int comparePairs(List<Card> hand1, List<Card> hand2) {
        Map<Integer, Integer> hand1Counts = getRankCounts(hand1);
        Map<Integer, Integer> hand2Counts = getRankCounts(hand2);

        List<Integer> hand1Pairs = new ArrayList<>();
        List<Integer> hand2Pairs = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : hand1Counts.entrySet()) {
            if (entry.getValue() >= 2) hand1Pairs.add(entry.getKey());
        }

        for (Map.Entry<Integer, Integer> entry : hand2Counts.entrySet()) {
            if (entry.getValue() >= 2) hand2Pairs.add(entry.getKey());
        }

        Collections.sort(hand1Pairs, Collections.reverseOrder());
        Collections.sort(hand2Pairs, Collections.reverseOrder());

        for (int i = 0; i < Math.min(hand1Pairs.size(), hand2Pairs.size()); i++) {
            if (!hand1Pairs.get(i).equals(hand2Pairs.get(i))) {
                return hand1Pairs.get(i) - hand2Pairs.get(i);
            }
        }

        return compareHighCard(hand1, hand2);
    }

    private static int compareHighCard(List<Card> hand1, List<Card> hand2) {
        List<Integer> hand1Ranks = new ArrayList<>();
        List<Integer> hand2Ranks = new ArrayList<>();

        for (Card card : hand1) hand1Ranks.add(card.rankValue());
        for (Card card : hand2) hand2Ranks.add(card.rankValue());

        Collections.sort(hand1Ranks, Collections.reverseOrder());
        Collections.sort(hand2Ranks, Collections.reverseOrder());

        for (int i = 0; i < Math.min(hand1Ranks.size(), hand2Ranks.size()); i++) {
            if (!hand1Ranks.get(i).equals(hand2Ranks.get(i))) {
                return hand1Ranks.get(i) - hand2Ranks.get(i);
            }
        }

        return 0;
    }

    private static Map<Integer, Integer> getRankCounts(List<Card> hand) {
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Card card : hand) {
            rankCount.put(card.rankValue(), rankCount.getOrDefault(card.rankValue(), 0) + 1);
        }
        return rankCount;
    }

    private static boolean isRoyalFlush(List<Card> cards) {
        if (isFlush(cards)) {
            List<Integer> ranks = new ArrayList<>();
            for (Card card : cards) {
                ranks.add(card.rankValue());
            }
            Collections.sort(ranks);
            return ranks.contains(14) && ranks.contains(13) && ranks.contains(12) &&
                    ranks.contains(11) && ranks.contains(10) && isStraightFlush(cards);
        }
        return false;
    }

    private static boolean isStraightFlush(List<Card> cards) {
        List<Card> flushCards = getFlushCards(cards);
        return isStraight(flushCards);
    }

    private static List<Card> getFlushCards(List<Card> cards) {
        Map<Suit, List<Card>> suitGroups = new HashMap<>();
        for (Card card : cards) {
            suitGroups.computeIfAbsent(card.suit(), k -> new ArrayList<>()).add(card);
        }
        for (List<Card> suitedCards : suitGroups.values()) {
            if (suitedCards.size() >= 5) {
                return suitedCards;
            }
        }
        return new ArrayList<>();
    }

    private static boolean isFourOfAKind(List<Card> cards) {
        return hasNOfAKind(cards, 4);
    }

    private static boolean isFullHouse(List<Card> cards) {
        return isThreeOfAKind(cards) && isPair(cards);
    }

    private static boolean isFlush(List<Card> cards) {
        return !getFlushCards(cards).isEmpty();
    }

    private static boolean isStraight(List<Card> cards) {
        List<Integer> ranks = new ArrayList<>();
        for (Card card : cards) {
            ranks.add(card.rankValue());
        }
        Collections.sort(ranks);

        if (ranks.contains(14)) {
            ranks.add(1);
        }

        int consecutiveCount = 1;
        for (int i = 1; i < ranks.size(); i++) {
            if (ranks.get(i) == ranks.get(i - 1) + 1) {
                consecutiveCount++;
                if (consecutiveCount == 5) return true;
            } else if (ranks.get(i) != ranks.get(i - 1)) {
                consecutiveCount = 1;
            }
        }
        return false;
    }

    private static boolean isThreeOfAKind(List<Card> cards) {
        return hasNOfAKind(cards, 3);
    }

    private static boolean isTwoPair(List<Card> cards) {
        List<Integer> pairs = new ArrayList<>();
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            rankCount.put(card.rankValue(), rankCount.getOrDefault(card.rankValue(), 0) + 1);
        }
        for (Integer count : rankCount.values()) {
            if (count == 2) pairs.add(count);
        }
        return pairs.size() == 2;
    }

    private static boolean isPair(List<Card> cards) {
        return hasNOfAKind(cards, 2);
    }

    private static boolean hasNOfAKind(List<Card> cards, int n) {
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Card card : cards) {
            rankCount.put(card.rankValue(), rankCount.getOrDefault(card.rankValue(), 0) + 1);
        }
        return rankCount.containsValue(n);
    }
}
