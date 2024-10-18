import java.util.*;

// testing to the river with 14 players, 1 million times
// this was tested with 2,3,4,8 and 14 players to the river 1 to 10 million times
// data might not agree with consensus best hands becuase in correctly played poker, most hands don't play as long

public class Main {
    private static List<Card> deck = new ArrayList<>();
    private static List<Card> communityCards = new ArrayList<>();

    public static void main(String[] args) {
        List<Hand> hands = new ArrayList<>();

        Map<Hand, Integer> handOccurance = new HashMap<>();
        Map<String, Integer> handWinAmount = new HashMap<>();
        Map<String, Integer> handPulledAmount = new HashMap<>();
        Map<String, Map<Hand, Integer>> winningHandMap = new HashMap<>();

        for (int i = 0; i < 1000000; i++) {
            List<Player> players = new ArrayList<>();
            initializeDeck();
            communityCards = new ArrayList<>();
            for (int p = 0; p < 14; p++) {
                Player ply = new Player();
                ply.createHand();
                players.add(ply);
            }

            dealFlop();
            dealTurn();
            dealRiver();

            Map<Player, Hand> bestHands = new HashMap<>();
            for (Player player : players) {

                List<Card> hand = player.getHand();
                boolean isSuited = hand.get(0).suit() == hand.get(1).suit();
                String handString = (hand.get(0).rankValue() > hand.get(1).rankValue() ? hand.get(0).rank() : hand.get(1).rank())
                        + (hand.get(0).rankValue() > hand.get(1).rankValue() ? hand.get(1).rank() : hand.get(0).rank())
                        + (isSuited ? "s" : "");

                if (handPulledAmount.containsKey(handString)) {
                    handPulledAmount.put(handString, handPulledAmount.get(handString) + 1);
                } else {
                    handPulledAmount.put(handString, 1);
                }

                Hand bestHand = HandEvaluator.evaluateHand(player.getHand(), communityCards);
                bestHands.put(player, bestHand);
            }

           // printCommunityCards();
            if (HandEvaluator.findWinner(bestHands) != null) {

                Player winner = HandEvaluator.findWinner(bestHands).getKey();
                Hand winnerHand = HandEvaluator.findWinner(bestHands).getValue();

                //System.out.println("Winner is " + winner.printHand() + " with " + winnerHand);

                List<Card> hand = winner.getHand();
                boolean isSuited = hand.get(0).suit() == hand.get(1).suit();
                String handString = (hand.get(0).rankValue() > hand.get(1).rankValue() ? hand.get(0).rank() : hand.get(1).rank())
                        + (hand.get(0).rankValue() > hand.get(1).rankValue() ? hand.get(1).rank() : hand.get(0).rank())
                        + (isSuited ? "s" : "");

//Map<String, Map<Hand, Integer>> winningHandMap = new HashMap<>();
               // System.out.println(handString);
                if (handWinAmount.containsKey(handString)) {
                    handWinAmount.put(handString, handWinAmount.get(handString) + 1);
                } else {
                    handWinAmount.put(handString, 1);
                }

                if (winningHandMap.containsKey(handString)) {
                    if (winningHandMap.get(handString).containsKey(winnerHand)) {
                        winningHandMap.get(handString).put(winnerHand, winningHandMap.get(handString).get(winnerHand) + 1);
                    } else {
                        winningHandMap.get(handString).put(winnerHand, 1);
                    }
                } else {
                    Map<Hand, Integer> tempMap = new HashMap<>();
                    tempMap.put(winnerHand, 1);
                    winningHandMap.put(handString, tempMap);
                }

                System.out.print("\rTested " + i + " hands, winner: " + handString + " (" + Math.round((double)handWinAmount.get(handString) / (double)handPulledAmount.get(handString) * 100) + "% win), hand: " + winnerHand);
            }
        }
        for (Hand hand : hands) {
            if (handOccurance.containsKey(hand)) {
                handOccurance.put(hand, handOccurance.get(hand) + 1);
            } else {
                handOccurance.put(hand, 1);
            }
        }
        System.out.println(" ");

        for (String handString : handPulledAmount.keySet()) {
            if (!handWinAmount.containsKey(handString)) {
                handWinAmount.put(handString, 0);
            }
        }
        System.out.println("Hand|Times Had|Times Won|Win %|High Card|Pair|Two Pair|Trips|Straight|Flush|Full House|Quad|Straight Flush|Royal Flush");
        for (String handString : handPulledAmount.keySet()) {
            //winningHandMap.get(handString).put(winnerHand, winningHandMap.get(handString).get(winnerHand) + 1);
            System.out.println(handString + "|"
                    + handPulledAmount.get(handString) + "|"
                    + handWinAmount.get(handString) + "|"
                    + ((double)handWinAmount.get(handString) / (double)handPulledAmount.get(handString) * 100) + "%|"
                    + winningHandMap.get(handString).getOrDefault(Hand.HIGH_CARD, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.PAIR, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.TWO_PAIR, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.SET, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.STRAIGHT, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.FLUSH, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.FULL_HOUSE, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.QUAD, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.STRAIGHT_FLUSH, 0) + "|"
                    + winningHandMap.get(handString).getOrDefault(Hand.ROYAL_FLUSH, 0)
            );
        }

        System.out.println(handWinAmount);
        System.out.println(handPulledAmount);
    }

    public static Card generateRandomCard() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(deck.size());
        Card card = deck.remove(randomIndex);
        return card;
    }

    private static void initializeDeck() {
        deck = new ArrayList<>();
        for (int rank = 2; rank <= 14; rank++) {
            for (Suit suit : Suit.values()) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    public static void dealFlop() {
        burnCard();
        for (int i = 0; i < 3; i++) {
            communityCards.add(generateRandomCard());
        }
    }

    public static void dealTurn() {
        burnCard();
        communityCards.add(generateRandomCard());
    }

    public static void dealRiver() {
        burnCard();
        communityCards.add(generateRandomCard());
    }

    private static void burnCard() {
        generateRandomCard();
    }

    public static void printCommunityCards() {
        System.out.print("Community Cards: ");
        for (Card card : communityCards) {
            System.out.print(card.print() + " ");
        }
        System.out.println();
    }


    private static Card card(Suit suit, int rank) { return new Card(rank, suit); }
    public static void testMain(String[] args) {


        communityCards = Arrays.asList(
                card(Suit.CLUB, 10),
                card(Suit.CLUB, 9),
                card(Suit.CLUB, 8),
                card(Suit.CLUB, 6),
                card(Suit.SPADE, 10)
        );

        Player player1 = new Player(Arrays.asList(card(Suit.HEART, 10), card(Suit.DIAMOND, 10))); // quad
        Player player2 = new Player(Arrays.asList(card(Suit.CLUB, 5), card(Suit.CLUB, 7))); // straight flush
        Player player3 = new Player(Arrays.asList(card(Suit.HEART, 9), card(Suit.DIAMOND, 9))); // full house
        Player player4 = new Player(Arrays.asList(card(Suit.HEART, 6), card(Suit.DIAMOND, 6))); // full house
        Player player5 = new Player(Arrays.asList(card(Suit.HEART, 2), card(Suit.DIAMOND, 14))); // pair (on board)
        Player player6 = new Player(Arrays.asList(card(Suit.HEART, 8), card(Suit.SPADE, 13))); // two pair (one on baord)
        Player player7 = new Player(Arrays.asList(card(Suit.HEART, 11), card(Suit.CLUB, 14))); // flush
        Player player8 = new Player(Arrays.asList(card(Suit.DIAMOND, 5), card(Suit.DIAMOND, 7))); // straight


        Hand hand1 = HandEvaluator.evaluateHand(player1.getHand(), communityCards);
        Hand hand2 = HandEvaluator.evaluateHand(player2.getHand(), communityCards);
        Hand hand3 = HandEvaluator.evaluateHand(player3.getHand(), communityCards);
        Hand hand4 = HandEvaluator.evaluateHand(player4.getHand(), communityCards);
        Hand hand5 = HandEvaluator.evaluateHand(player5.getHand(), communityCards);
        Hand hand6 = HandEvaluator.evaluateHand(player6.getHand(), communityCards);
        Hand hand7 = HandEvaluator.evaluateHand(player7.getHand(), communityCards);
        Hand hand8 = HandEvaluator.evaluateHand(player8.getHand(), communityCards);

        System.out.println(hand1);
        System.out.println(hand2);
        System.out.println(hand3);
        System.out.println(hand4);
        System.out.println(hand5);
        System.out.println(hand6);
        System.out.println(hand7);
        System.out.println(hand8);

    }
}

