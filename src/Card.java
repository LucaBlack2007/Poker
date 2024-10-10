public class Card {

    private Suit suit;
    private int rank;

    Card(int rank, Suit suit) {
        this.suit = suit;
        this.rank = rank;
    }

    public int rankValue() { return this.rank; }
    public Suit suit() { return this.suit; }
    public String rank() {
        String ret;
        switch (this.rank) {
            case 11: ret = "J"; break;
            case 12: ret = "Q"; break;
            case 13: ret = "K"; break;
            case 14: ret = "A"; break;
            default: ret = String.valueOf(this.rank); break;
        }
        //System.out.println(rank);
        return ret;
    }
    public String print() { return rank() + suit().emoji(); }

}
