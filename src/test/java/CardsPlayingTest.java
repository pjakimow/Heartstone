import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.CardType;
import com.github.pjakimow.xenteros.card.Monster;
import com.github.pjakimow.xenteros.card.MonsterAbility;
import com.github.pjakimow.xenteros.player.IllegalMoveException;
import com.github.pjakimow.xenteros.player.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardsPlayingTest {

	private static Player player;
	
	@BeforeClass
	public static void setup(){
		List<Card> deck = new LinkedList<Card>();
	
		for(int i = 0; i < 10 ; i++)
			deck.add(new Monster(CardType.MONSTER, 1, 2, 3, MonsterAbility.TAUNT));
		
		deck.add(new Monster(CardType.MONSTER, 2, 2, 3, MonsterAbility.TAUNT));
	
		player = new Player(1, deck);
		
		//draw all cards to hand
		player.drawCards(deck.size());
	}
	
	@Test(expected = IllegalMoveException.class)
	public void should_throwException_when_notEnoughMana() {
		player.setMana(1);
		Card card = player.getHand().stream()
	            .filter(x -> x.getCost() == 2)
	            .findFirst()
	            .get();
		
     	player.playCard(card.getUuid());
	}
	
	@Test
	public void should_playACard_when_enoughMana() {
		player.setMana(1);
		Card card = player.getHand().stream()
	            .filter(x -> x.getCost() == 1)
	            .findFirst()
	            .get();
		
     	player.playCard(card.getUuid());
	}
	
	@Test(expected = IllegalMoveException.class)
	public void should_throwException_when_notEnoughPlaceOnTable() {
		player.setMana(10);
		List<Monster> playedMonsters = new ArrayList<>();
		
		//put 7 cards on table
		while(player.getReadyTableSize() + player.getUnreadyTableSize() < 7){
	
			Card card = player.getHand().get(0);
			player.playCard(card.getUuid());
			
			if (card instanceof Monster) {
				playedMonsters.add((Monster) card);
				player.moveMonstersToTable(playedMonsters);
			}       
		}
		
		//try to play 8th card
		Card card = player.getHand().get(0);
	
     	player.playCard(card.getUuid());
	}
	
}
