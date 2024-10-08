import java.util.*;
// rand between 0 and bound-1


public class Main {
    public static List<Card> cardsUsed;

    public static Card generateRandomCard() {
        //cardsUsed = new ArrayList<>();
        Random rand = new Random();
        int r = rand.nextInt(13) + 2;

        Suit suit;
        Random randSuit = new Random();
        int s = randSuit.nextInt(4);
        switch (s) {
            case 0: suit = Suit.CLUB; break;
            case 1: suit = Suit.DIAMOND; break;
            case 2: suit = Suit.HEART; break;
            case 3: suit = Suit.SPADE; break;
            default: suit = null; break;
        }

        Card card = new Card(r, suit);

        while (cardsUsed.contains(card)) {
            System.out.println("refreshing");
            rand = new Random();
            r = rand.nextInt(13) + 2;

            randSuit = new Random();
            s = randSuit.nextInt(4);
            switch (s) {
                case 0: suit = Suit.CLUB; break;
                case 1: suit = Suit.DIAMOND; break;
                case 2: suit = Suit.HEART; break;
                case 3: suit = Suit.SPADE; break;
                default: suit = null; break;
            }

            card = new Card(r, suit);
        }

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
        cardsUsed = new ArrayList<>();
        Player player = new Player();
        player.createHand();
        Map<Hand, Integer> hands = new HashMap<>();
        for (int i = 0; i < 100000; i++) {
            cardsUsed = new ArrayList<>();
            board = new ArrayList<>();
            player = new Player();
            player.createHand();
            System.out.println(player.getHand().get(0).print() + " " + player.getHand().get(1).print());

            //try {
            //Thread.sleep(500);

            flop();
            printBoard();
            //Thread.sleep(500);

            turn();
            printBoard();
            // Thread.sleep(500);

            river();
            printBoard();

            // totalCards = player.getHand();
            // totalCards.addAll(board);

            HandMeta meta = findHand(board, player);
            System.out.println("highcard: " + meta.highCard().print());
            System.out.println("hand: " + meta.hand());

            if (hands.containsKey(meta.hand())) {
                hands.put(meta.hand(), hands.get(meta.hand()) + 1);
            } else hands.put(meta.hand(), 1);

           // board.addAll(player.getHand()))
            FindHand.straight(board, player);

       //     if (meta.hand() == Hand.ROYAL_FLUSH) return;

            // } catch (InterruptedException e) {
            //    throw new RuntimeException(e);
            // }
        }
        // System.out.println("quads took " + attempts + " attempts");
        for (Hand hand : hands.keySet()) {
            System.out.println(hand + " " + hands.get(hand));
        }
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