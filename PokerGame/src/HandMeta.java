public class HandMeta {

	private Card highCard;
	private Hand hand;

	HandMeta(Hand hand, Card highCard) {
		this.hand = hand;
		this.highCard = highCard;
	}

	public Card highCard() {
		return this.highCard;
	}

	public Hand hand() {
		return this.hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void setHighCard(Card highCard) {
		this.highCard = highCard;
	}
}
