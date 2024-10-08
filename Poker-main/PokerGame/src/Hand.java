public enum Hand {

    HIGH_CARD(0),
    PAIR(1),
    TWO_PAIR(2),
    SET(3),
    STRAIGHT(4),
    FLUSH(5),
    FULL_HOUSE(6),
    QUAD(7),
    STRAIGHT_FLUSH(8),
    ROYAL_FLUSH(9);

    private int level;

    Hand(int level) {
        this.level = level;
    }

}
