package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.CardType;
import com.github.pjakimow.xenteros.card.Monster;
import com.github.pjakimow.xenteros.card.Spell;

import java.util.*;

import static com.github.pjakimow.xenteros.card.MonsterAbility.TAUNT;
import static java.lang.Math.min;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

public class Player {

    private int health = 20;
    private int mana;
    private Queue<Card> deck;
    private Map<String, Card> hand = new HashMap<>();
    private Set<Monster> temp = new HashSet<>();
    private Map<String, Monster> table = new HashMap<>();
    private int failedDrawAttempts = 0;

    public Player(int mana, List<Card> cards) {
        this.mana = mana;
        shuffle(cards);
        deck = new LinkedList<>(cards);
    }
    
    public Player(int mana) {
        this.mana = mana;
        deck = new LinkedList<>();
    }
    
    static Player fromPlayer(Player that) {
    	Player newPlayer = new Player(that.getMana());
    	newPlayer.health = that.health;
    	newPlayer.failedDrawAttempts = that.failedDrawAttempts;
    	
    	//copy hand
    	List<Card> oldHand = that.getHand();
    	Map<String, Card> newHand = new HashMap<>();
    	Card newCard;
    	for ( Card c : oldHand){
    		if ( c instanceof Spell ){
    			newCard = Spell.fromSpell((Spell) c);
    			newHand.put(newCard.getUuid(), newCard);
    		} else if (c instanceof Monster ) {
    			newCard = Monster.fromMonster((Monster) c);
    			newHand.put(newCard.getUuid(), newCard);
    		}
    	}
    	newPlayer.hand = newHand;
    	
    	//copy table
    	List<Monster> oldTable = that.getTable();
    	Map<String, Monster> newTable = new HashMap<>();
    	Monster newMonster;
    	for ( Monster m : oldTable){
    			newMonster = Monster.fromMonster(m);
    			newHand.put(newMonster.getUuid(), newMonster);
    	}
    	newPlayer.table = newTable;
    	
    	//copy deck
    	Queue<Card> oldDeck = that.getDeck();
    	Queue<Card> newDeck = new LinkedList<>();
    	Card temp;
    	for ( Card c : oldDeck){
    		if ( c instanceof Spell ){
    			temp = Spell.fromSpell((Spell) c);
    			newDeck.add(temp);
    		} else if (c instanceof Monster ) {
    			temp = Monster.fromMonster((Monster) c);
    			newDeck.add(temp);;
    		}
    	}
    	newPlayer.deck = newDeck;
    	
        return newPlayer;
    }
    
    public List<Card> getHand() {
        return hand.values().stream()
                .collect(toList());
    }

    List<Monster> getTable() {
        return table.values().stream()
                .collect(toList());
    }

    public List<Monster> getMonstersToAttack() {
        if (table.values().stream().anyMatch(m -> m.getMonsterAbility() == TAUNT)) {
            return table.values().stream()
                    .filter(Monster::hasTaunt)
                    .collect(toList());
        }
        return getTable();
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    void beginTurn(int round) {
        drawCards(1);
        setMana(round);
    }

    public Card playCard(String uuid) {

        Card card = hand.get(uuid);
        if (card.getCost() > this.mana) {
            throw new IllegalMoveException();
        }
        if (card instanceof Monster && table.size() + temp.size() >= 7) {
            throw new IllegalMoveException();
        }

        if (card instanceof Monster) {
            temp.add((Monster) card);
        }

        hand.remove(uuid);
        this.mana -= card.getCost();

        return card;
    }

    void receiveAttack(String uuid, int power) {
        Monster attackedCard = table.get(uuid);
        attackedCard.receiveAttack(power);

        if (attackedCard.getHealth() < 0) {
            table.remove(uuid);
        }
    }

    void receiveAttack(int power) {
        this.health -= power;
        if (this.health <= 0) {
            throw new PlayerDeadException();
        }
    }

    private Optional<Card> drawCard() {
        if (deck.isEmpty()) {
            this.failedDrawAttempts++;
            this.health -= failedDrawAttempts;
            return Optional.empty();
        }
        return Optional.ofNullable(deck.poll());
    }

    public void drawCards(int n) {
        for (int i = 0; i < n; i++) {
            drawCard().ifPresent(c -> hand.put(c.getUuid(), c));
        }
    }

    public void setMana(int round) {
        this.mana = min(round, 10);
    }

    boolean canPlayCard() {
        return hand.values().stream()
                .mapToInt(Card::getCost)
                .min()
                .orElse(Integer.MAX_VALUE) <= this.mana;
    }

    boolean hasTaunt() {
        return table.values().stream().anyMatch(Monster::hasTaunt);
    }

    void addChargeMonsterToTable(Monster monster) {
        table.put(monster.getUuid(), monster);
    }

    void moveMonstersToTable() {
        temp.forEach(m -> table.put(m.getUuid(), m));
    }

    void heal(int points) {
        health += points;
    }

    public void printHand() {
        System.out.println("Hand:");
        hand.values().forEach(System.out::println);
    }

    public void printTable() {
        System.out.println("Table:");
        table.values().forEach(System.out::println);
    }
    
    public int getReadyTableSize(){
    	return table.size();
    }
    
    public int getUnreadyTableSize(){
    	return temp.size();
    }
    
    public List<Card> getCardsPossibleToPlay(int maxMana) {//?
        return hand.values().stream()
                .filter(c -> c.getCost() <= maxMana)
                .collect(toList());
    }

    public List<Card> getSpellsPossibleToPlay(int maxMana) {//?
        return hand.values().stream()
                .filter(c -> c.getCost() <= maxMana && c.getType() == CardType.SPELL)
                .collect(toList());
    }
    
	public void addMonsterToTable(Monster monster) {
        table.put(monster.getUuid(), monster);
	}

	public Queue<Card> getDeck() {
		return deck;
	}

	public int getFailedDrawAttempts() {
		return failedDrawAttempts;
	}
	
	
}
