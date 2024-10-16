public enum Suit {

    HEART('❤'),
    SPADE('♠'),
    DIAMOND('♦'),
    CLUB('♣');

    private char emoji;

    Suit(char emoji) {
        this.emoji = emoji;
    }

    public char emoji() {
        return this.emoji;
    }

}
