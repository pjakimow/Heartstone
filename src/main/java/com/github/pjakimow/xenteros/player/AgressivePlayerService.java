package com.github.pjakimow.xenteros.player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pjakimow.xenteros.card.AttackCardComp;
import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.DeckProvider;
import com.github.pjakimow.xenteros.card.Monster;
import com.github.pjakimow.xenteros.card.Spell;

@Component
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
    	} else {
            List<Monster> opponentTauntCards = opponent.getMonstersToAttack();
            //TODO:which one to attack? now: randomly
            int choice = (int) (Math.random() * opponentTauntCards.size());
            opponent.receiveAttack(opponentTauntCards.get(choice).getUuid(), power);
    	}
    }
    
    private Card chooseCard(Player player){
    	List<Card> cards;
    	
    	if (player.getReadyTableSize() == 7){
    		cards = player.getSpellsPossibleToPlay(player.getMana());
    	} else {
        	cards = player.getCardsPossibleToPlay(player.getMana());
    	}
    	
    	Collections.sort(cards, new AttackCardComp());
    	
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
            if (choice == null) {
                break;
            }

            try {
                player.playCard(choice.getUuid());
            } catch (IllegalMoveException e) {
                System.out.println("You already have 7 monsters on the table. Pick another one.");
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
