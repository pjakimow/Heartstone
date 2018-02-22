package model;

import java.util.LinkedList;
import java.util.List;

public class Hero {
	private int manaAvailable;
	private int lifePoints;
	private List<Card> cardsInHand;
	private List<Card> cardsOnTable;
	private List<Card> cardsInDeck;
	
	public Hero() {
		manaAvailable = 1;
		lifePoints = 10;
		cardsInHand = new LinkedList();
		cardsOnTable = new LinkedList();
		cardsInDeck = new LinkedList();
		cardsInHand.add(new Card(2));
	}
	
}
