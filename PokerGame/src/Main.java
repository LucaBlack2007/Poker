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
        List<Card> totalCards = new ArrayList<>();
        Player player = new Player();
        player.createHand();
        int attempts = 0;
        while (findHand(board, player).hand() != Hand.QUAD) {
            attempts++;
            cardsUsed = new ArrayList<>();
            board = new ArrayList<>();
            player = new Player();
            player.createHand();
            System.out.println(player.getHand().get(0).print() + " " + player.getHand().get(1).print());

            totalCards = new ArrayList<>();

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
                System.out.println("highcard: "+ meta.highCard().print());
                System.out.println("hand: "+ meta.hand());

           // } catch (InterruptedException e) {
            //    throw new RuntimeException(e);
           // }
        }
        System.out.println("quads took " + attempts + " attempts");

	}

    private static Card findHighCard(Player player) {
        if (player.getHand().get(0).rankValue() > player.getHand().get(1).rankValue())
            return player.getHand().get(0);
        else return player.getHand().get(1);
    }


    private static HandMeta findHand(List<Card> cards, Player player) {

        Hand hand = Hand.HIGH_CARD;
        Card highCard = findHighCard(player);
        HandMeta handMeta = new HandMeta(hand, highCard);

        HandMeta flush = flush(cards, player);
        if (flush.hand() == Hand.FLUSH) {
            handMeta.setHand(flush.hand());
            handMeta.setHighCard(flush.highCard());
        }

        HandMeta quads = quads(cards, player);
        if (quads.hand() == Hand.QUAD) {
            handMeta.setHand(quads.hand());
            handMeta.setHighCard(quads.highCard());
        }

        return handMeta;
    }

    private static HandMeta quads(List<Card> cards, Player player) {

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

    private static HandMeta flush(List<Card> cards, Player player) {
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

}