import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.CardType;
import com.github.pjakimow.xenteros.card.Monster;
import com.github.pjakimow.xenteros.card.MonsterAbility;
import com.github.pjakimow.xenteros.player.Player;

public class AttackTest {

	private static Player player1;
	
	@BeforeClass
	public static void setup(){
		List<Monster> monsters = new LinkedList<Monster>();
	
		for(int i = 0; i < 2 ; i++)
			monsters.add(new Monster(CardType.MONSTER, 1, 2, 3, MonsterAbility.TAUNT));
		monsters.add(new Monster(CardType.MONSTER, 2, 2, 3, MonsterAbility.CHARGE));
	
		player1 = new Player(1, new LinkedList<Card>());
		
		//move all cards to table
		player1.moveMonstersToTable(monsters);
	}
	
	@Test
	public void should_returnAllTauntCards_when_onTable() {
		int expected = 2;
		int actual = 0;
		
		List<Monster> cards = player1.getMonstersToAttack();
		
		assertEquals(expected, cards.size());

		for( Monster card: cards)
			if(card.hasTaunt())
				actual++;
		
		assertEquals(expected, actual);
	}

}
