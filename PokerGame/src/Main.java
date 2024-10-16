import java.util.*;

public class Main {
    private static List<Card> deck = new ArrayList<>();
    private static List<Card> communityCards = new ArrayList<>();

    public static void main(String[] args) {
        List<Hand> hands = new ArrayList<>();
        Map<Hand, Integer> handOccurance = new HashMap<>();
        Map<String, Integer> handWinAmount = new HashMap<>();
        Map<String, Integer> handPulledAmount = new HashMap<>();
        for (int i = 0; i < 1000000; i++) {
            List<Player> players = new ArrayList<>();
            initializeDeck();
            communityCards = new ArrayList<>();
            for (int p = 0; p < 8; p++) {
                Player ply = new Player();
                ply.createHand();
                players.add(ply);
            }

            dealFlop();
            dealTurn();
            dealRiver();

            Map<Player, Hand> bestHands = new HashMap<>();
            for (Player player : players) {
              //  System.out.println(player.printHand());

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


               // System.out.println(handString);
                if (handWinAmount.containsKey(handString)) {
                    handWinAmount.put(handString, handWinAmount.get(handString) + 1);
                } else {
                    handWinAmount.put(handString, 1);
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

        for (String handString : handPulledAmount.keySet()) {
            System.out.println(handString + "|"
                    + handPulledAmount.get(handString) + "|"
                    + handWinAmount.get(handString) + "|"
                    + (handWinAmount.get(handString) / handPulledAmount.get(handString) * 100) + "%"
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
}

