package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("controlling")
public class ControllingPlayerService extends PlayerService{

    @Autowired
    public ControllingPlayerService(DeckProvider deckProvider) {
    	super(deckProvider);
    }

    public Player createPlayer() {
        return new Player(1, deckProvider.getDeck());
    }
    
    private void attackOpponent(int power, Player opponent) {//ok
    	/** opponentMinionsCards contains only taunt cards 
    	 * or only standard cards if there are no cards with taunt **/
    	List<Monster> opponentMinionsCards = opponent.getMonstersToAttack();
    
    	if (opponentMinionsCards.isEmpty()){ //there are no minions on table
    		opponent.receiveAttack(power);
    	} else{
    		int choice = (int) (Math.random() * opponentMinionsCards.size());
            opponent.receiveAttack(opponentMinionsCards.get(choice).getUuid(), power);
    	}
    }
    
    private void throwSpell(Spell spell, Player player, Player opponent) {//ok
        SpellAction spellAction = spell.getAction();

        switch (spellAction) {
            case DEAL_1_DAMAGE_DRAW_1_CARD:
                player.drawCards(1);
                attackOpponent(1, opponent);
                break;
            case DEAL_2_DAMAGE_RESTORE_2_HEALTH:
                player.heal(2);
                attackOpponent(2, opponent);
                break;
            case DRAW_2_CARDS:
                player.drawCards(2);
                break;
            default:
                break;
        }
    }
    
    private Card chooseCard(Player player){
    	List<Card> cards;
    	
    	if (player.getReadyTableSize() + player.getUnreadyTableSize() >= 7){
    		cards = player.getSpellsPossibleToPlay(player.getMana());
    	} else {
        	cards = player.getCardsPossibleToPlay(player.getMana());
    	}
    	System.out.println("controlling: " + cards.size());
    	Collections.sort(cards, new AttackCardComp());
    	return cards.size() > 0 ? cards.get(0) : null;
    	
    }
    
    public void move(Player player, Player opponent, int round) {
        player.beginTurn(round);
        
        //first attack minions
        List<Monster> table = player.getTable();
        for (Monster monster : table) {
            attackOpponent(monster.getAttack(), opponent);
        }
        
        //then buy some cards
        while (player.canPlayCard()) {

        	Card choice = chooseCard(player); //is this strategy the same as for agressive player?
            if (choice == null) {
                break;
            }

            try {
                player.playCard(choice.getUuid());
            } catch (IllegalMoveException e) {
              //  System.out.println("You already have 7 monsters on the table. Pick another one.");
                continue;
            }

            if (choice instanceof Monster) {
                Monster monster = (Monster) choice;
                
                player.addMonsterToTable(monster);
                if (monster.hasCharge()) {
                   attackOpponent(monster.getAttack(), opponent);
                }
            } else {
                throwSpell((Spell) choice, player, opponent);
            }
        }

        //player.moveMonstersToTable();
    }
}
