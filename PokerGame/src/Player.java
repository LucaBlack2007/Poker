import java.util.ArrayList;
import java.util.List;

public class Player {

	private List<Card> hand;

	Player() { hand = new ArrayList<>(); }
	Player(List<Card> hand) { setHand(hand); }

	public List<Card> getHand() {
		return hand;
	}

	public String printHand() {
		return (hand.get(0).print() + " " + hand.get(1).print());
	}

	public void createHand() {
		Card card = Main.generateRandomCard();
		for (int i = 0; i < 2; i++) {
			card = Main.generateRandomCard();
			hand.add(card);
		}
	}

	public void setHand(List<Card> hand) {
		this.hand = hand;
		//Main.cardsUsed.addAll(hand);
	}

}
