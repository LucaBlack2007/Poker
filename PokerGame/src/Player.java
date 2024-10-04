import java.util.ArrayList;
import java.util.List;

public class Player {

	private List<Card> hand;

	Player() {
		hand = new ArrayList<>();
	}

	public List<Card> getHand() {
		return hand;
	}

	public void createHand() {
		Card card = Main.generateRandomCard();
		for (int i = 0; i < 2; i++) {
			card = Main.generateRandomCard();
			hand.add(card);
		}
	}

}
