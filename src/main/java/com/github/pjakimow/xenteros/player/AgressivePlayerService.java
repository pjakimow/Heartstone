package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("agressive")
public class AgressivePlayerService extends PlayerService{

    @Autowired
    public AgressivePlayerService(DeckProvider deckProvider) {
    	super(deckProvider);
    }

    public Player createPlayer() {
        return new Player(1, deckProvider.getDeck());
    }
    
    private void attackOpponent(int power, Player opponent) {
    	if (!opponent.hasTaunt()){
    		opponent.receiveAttack(power);
    	//	System.out.println("atak hero with power " + power);
    	} else {
            List<Monster> opponentTauntCards = opponent.getMonstersToAttack();
            //TODO:which one to attack? now: randomly
            int choice = (int) (Math.random() * opponentTauntCards.size());
            opponent.receiveAttack(opponentTauntCards.get(choice).getUuid(), power);
          //  System.out.println("atak: " + opponentTauntCards.get(choice) +" with power " + power);
    	}
    }
    
    private void throwSpell(Spell spell, Player player, Player opponent) {
        SpellAction spellAction = spell.getAction();
       // System.out.println("zaklęcie: " + spellAction);
        switch (spellAction) {
            case DEAL_1_DAMAGE_DRAW_1_CARD:
                player.drawCards(1);
            	opponent.receiveAttack(1);
                break;
            case DEAL_2_DAMAGE_RESTORE_2_HEALTH:
                player.heal(2);
            	opponent.receiveAttack(2);
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
    	//System.out.println("agressive" + cards.size());
    	Collections.sort(cards, new AttackCardComp2());
    	return cards.size() > 0 ? cards.get(0) : null;
    	
    }
    
    public void move(Player player, Player opponent, int round) {
        player.beginTurn(round);
        
        //first attack opponent
        List<Monster> table = player.getTable();
        for (Monster monster : table) {
            attackOpponent(monster.getAttack(), opponent);//next state
        }
        
        //then buy some cards
        while (player.canPlayCard()) {

        	Card choice = chooseCard(player);
          //  System.out.println("Wybierz: " + choice);
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
                   attackOpponent(monster.getAttack(), opponent);///next state
                }
            } else {
                throwSpell((Spell) choice, player, opponent);//next state
            }
        }

        //player.moveMonstersToTable();
    }
}
