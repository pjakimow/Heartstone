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
import com.github.pjakimow.xenteros.card.SpellAction;
import com.github.pjakimow.xenteros.game.GameManager;
import com.github.pjakimow.xenteros.game.GameMode;
import com.github.pjakimow.xenteros.mcts.Node;


@Component
public class AgressivePlayerService extends PlayerService{

    @Autowired
    public AgressivePlayerService(DeckProvider deckProvider) {
    	super(deckProvider);
    }

    public Player createPlayer() {
        return new Player(1, deckProvider.getDeck());
    }
    
    private void attackOpponent(int power, Node node) {
    	if (!node.getOpponent().hasTaunt()){
    		node.getOpponent().receiveAttack(power);
    	} else {
            List<Monster> opponentTauntCards = node.getOpponent().getMonstersToAttack();
           
            int choice = 0;//(int) (Math.random() * opponentTauntCards.size());
            
            if ( GameManager.getMode() == GameMode.EXPANSION){
	            for (; choice < opponentTauntCards.size(); choice++){ //expansion
	            	Node child = new Node(node.getPlayer(), node.getOpponent());
	            	child.getOpponent().receiveAttack(opponentTauntCards.get(choice).getUuid(), power);
	            	
	            	node.addChild(child);
	            }
	            GameManager.setMode(GameMode.SIMULATION);
	            //chose 1st
	            node = node.getFirstChild(); 
            } else if ( GameManager.getMode() == GameMode.SIMULATION){ 
            	node.getOpponent().receiveAttack(opponentTauntCards.get(choice).getUuid(), power);             
            }
           
    	}
    }
    
    private void throwSpell(Spell spell, Player player, Player opponent) {
        SpellAction spellAction = spell.getAction();

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
    	
    	if (player.getReadyTableSize() == 7){
    		cards = player.getSpellsPossibleToPlay(player.getMana());
    	} else {
        	cards = player.getCardsPossibleToPlay(player.getMana());
    	}
    	
    	Collections.sort(cards, new AttackCardComp());
    	Card chosen = cards.size() > 0 ? cards.get(0) : null;
    	//System.out.println("-Chosen: " + chosen);
    	return chosen;
    	
    }
    
    public void move(Node node, int round) {
        node.getPlayer().beginTurn(round);
        
        //first attack opponent
        List<Monster> table = node.getPlayer().getTable();
        for (Monster monster : table) {
            attackOpponent(monster.getAttack(), node);
        }
        
        //then buy some cards
        while (node.getPlayer().canPlayCard()) {

        	Card choice = chooseCard(node.getPlayer());
            if (choice == null) {
                break;
            }

            try {
            	if ( GameManager.getMode() == GameMode.SIMULATION){
            		node.getPlayer().playCard(choice.getUuid());
            	} else if ( GameManager.getMode() == GameMode.EXPANSION){
 	            	Node child = new Node(node.getPlayer(), node.getOpponent());
 	            	child.getPlayer().playCard(choice.getUuid());
 	            	
 	            	node.addChild(child);
     	      
     	            GameManager.setMode(GameMode.SIMULATION);
     	            //chose 1st
     	            node = node.getFirstChild(); 
            	}
            	
            } catch (IllegalMoveException e) {
                System.out.println("You already have 7 monsters on the table. Pick another one.");
                continue;
            }

            if (choice instanceof Monster) {
                Monster monster = (Monster) choice;
                
                node.getPlayer().addMonsterToTable(monster);
                if (monster.hasCharge()) {
                   attackOpponent(monster.getAttack(), node); //buying and using a monster are diff actions
                }
            } else {
                throwSpell((Spell) choice, node.getPlayer(), node.getOpponent()); 
                //node changed by buying a spell
            }
        }

        //player.moveMonstersToTable();
    }
}
