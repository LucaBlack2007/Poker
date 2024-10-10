import java.util.*;

public class Main {
    public static List<Card> cardsUsed;

    public static Card generateRandomCard() {
        Random rand = new Random();
        int r;
        Suit suit;
        Card card;

        do {
            r = rand.nextInt(13) + 2;
            int s = rand.nextInt(4);
            switch (s) {
                case 0: suit = Suit.CLUB; break;
                case 1: suit = Suit.DIAMOND; break;
                case 2: suit = Suit.HEART; break;
                case 3: suit = Suit.SPADE; break;
                default: suit = null; break;
            }
            card = new Card(r, suit);
        } while (cardsUsed.contains(card));

        cardsUsed.add(card);
        return card;
    }

    private static List<Card> board = new ArrayList<>();

    public static void flop() {
        for (int i = 0; i < 3; i++) {
            Card card = generateRandomCard();
            board.add(card);
        }
    }

    public static void turn() {
        Card card = generateRandomCard();
        board.add(card);
    }

    public static void river() {
        Card card = generateRandomCard();
        board.add(card);
    }

    private static void printBoard() {
        for (Card card : board) {
            System.out.print(card.print() + " ");
        }
        System.out.println("");
    }

    public static void main(String[] args) {
        int totalRounds = 1000000;
        Map<Hand, Integer> handWins = new HashMap<>();

        for (int i = 0; i < totalRounds; i++) {
            cardsUsed = new ArrayList<>();
            board = new ArrayList<>();
            List<Player> players = new ArrayList<>();

            for (int j = 0; j < 8; j++) {
                Player player = new Player();
                player.createHand();
                players.add(player);
                System.out.println("Player " + (j + 1) + ": " + player.getHand().get(0).print() + " " + player.getHand().get(1).print());
            }

            flop();
            printBoard();
            turn();
            printBoard();
            river();
            printBoard();

            Player winningPlayer = null;
            HandMeta winningMeta = null;

            for (int j = 0; j < 8; j++) {
                Player player = players.get(j);
                HandMeta meta = findHand(board, player);
                System.out.println("Player " + (j + 1) + " highcard: " + meta.highCard().print());
                System.out.println("Player " + (j + 1) + " hand: " + meta.hand());

                if (winningPlayer == null || meta.hand().getLevel() > winningMeta.hand().getLevel()) {
                    winningPlayer = player;
                    winningMeta = meta;
                } else if (meta.hand().getLevel() == winningMeta.hand().getLevel()) {
                    if (compareHighCards(meta, winningMeta) > 0) {
                        winningPlayer = player;
                        winningMeta = meta;
                    }
                }
            }

            Hand winningHand = winningMeta.hand();
            handWins.put(winningHand, handWins.getOrDefault(winningHand, 0) + 1);

            System.out.println("Winning hand: " + winningHand);
        }

        System.out.println("\nWinning Hand Statistics:");

        int totalWins = 0;
        List<Hand> sortedHands = new ArrayList<>(handWins.keySet());
        sortedHands.sort(Comparator.comparingInt(Hand::getLevel));

        for (Hand hand : sortedHands) {
            int wins = handWins.get(hand);
            totalWins += wins;
            System.out.println(hand + " won " + wins + " times.");
        }

        System.out.println("\nTotal rounds played: " + totalRounds);
        System.out.println("Total winning hands counted: " + totalWins);
    }

    private static int compareHighCards(HandMeta meta1, HandMeta meta2) {
        int comparison = Integer.compare(meta1.highCard().rankValue(), meta2.highCard().rankValue());

        return comparison;
    }

    private static HandMeta findHand(List<Card> cards, Player player) {
        Hand hand = Hand.HIGH_CARD;
        Card highCard = FindHand.findHighCard(player);
        HandMeta handMeta = new HandMeta(hand, highCard);

        HandMeta pair = FindHand.pair(cards, player);
        if (pair.hand() == Hand.PAIR) {
            handMeta.setHand(pair.hand());
            handMeta.setHighCard(pair.highCard());
        }

        HandMeta twoPair = FindHand.twoPair(cards, player);
        if (twoPair.hand() == Hand.TWO_PAIR) {
            handMeta.setHand(twoPair.hand());
            handMeta.setHighCard(twoPair.highCard());
        }

        HandMeta trips = FindHand.trips(cards, player);
        if (trips.hand() == Hand.SET) {
            handMeta.setHand(trips.hand());
            handMeta.setHighCard(trips.highCard());
        }

        HandMeta straight = FindHand.straight(cards, player);
        if (straight.hand() == Hand.STRAIGHT) {
            handMeta.setHand(straight.hand());
            handMeta.setHighCard(straight.highCard());
        }

        HandMeta flush = FindHand.flush(cards, player);
        if (flush.hand() == Hand.FLUSH) {
            handMeta.setHand(flush.hand());
            handMeta.setHighCard(flush.highCard());
        }

        HandMeta fullHouse = FindHand.fullHouse(cards, player);
        if (fullHouse.hand() == Hand.FULL_HOUSE) {
            handMeta.setHand(fullHouse.hand());
            handMeta.setHighCard(fullHouse.highCard());
        }

        HandMeta quads = FindHand.quads(cards, player);
        if (quads.hand() == Hand.QUAD) {
            handMeta.setHand(quads.hand());
            handMeta.setHighCard(quads.highCard());
        }

        HandMeta straightFlush = FindHand.straightFlush(cards, player);
        if (straightFlush.hand() == Hand.STRAIGHT_FLUSH) {
            handMeta.setHand(straightFlush.hand());
            handMeta.setHighCard(straightFlush.highCard());
        }

        HandMeta royalFlush = FindHand.royalFlush(cards, player);
        if (royalFlush.hand() == Hand.ROYAL_FLUSH) {
            handMeta.setHand(royalFlush.hand());
            handMeta.setHighCard(royalFlush.highCard());
        }

        return handMeta;
    }
}
