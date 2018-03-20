package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.*;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.pjakimow.xenteros.card.CardType.MONSTER;
import static com.github.pjakimow.xenteros.card.MonsterAbility.TAUNT;
import static java.lang.Math.min;
import static java.util.Collections.shuffle;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class Player {

    private int health = 20;
    private int mana;
    private LinkedList<Card> deck;
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

    public Player deepCopy() {
        Player newPlayer = new Player(this.getMana());
        newPlayer.health = this.health;
        newPlayer.failedDrawAttempts = this.failedDrawAttempts;

        newPlayer.hand = this.getHand().stream()
                .map(Card::deepCopy)
                .collect(toMap(Card::getUuid, identity()));

        newPlayer.table = this.getTable().stream()
                .map(Monster::fromMonster)
                .collect(toMap(Monster::getUuid, identity()));

        newPlayer.deck = this.getDeck().stream()
                .map(Card::deepCopy)
                .collect(Collectors.toCollection(LinkedList::new));

        return newPlayer;
    }

    public List<Card> getHand() {
        return hand.values().stream()
                .collect(toList());
    }

    public List<Monster> getTable() {
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

    public void beginTurn(int round) {
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
            Monster m = (Monster) card;
            if (m.hasCharge()) {
                table.put(m.getUuid(), m);
            } else {
                temp.add((Monster) card);
            }
        }

        hand.remove(uuid);
        this.mana -= card.getCost();

        return card;
    }

    public void receiveAttack(String uuid, int power) {
        Monster attackedCard = table.get(uuid);
        if (attackedCard == null) {
            return;
        }
        attackedCard.receiveAttack(power);

        if (attackedCard.getHealth() < 0) {
            table.remove(uuid);
        }
    }

    public void receiveAttack(int power) {
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

    public void moveMonstersToTable() {
        temp.forEach(m -> table.put(m.getUuid(), m));
        temp.clear();
    }

    public void heal(int points) {
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

    public int getReadyTableSize() {
        return table.size();
    }

    public int getUnreadyTableSize() {
        return temp.size();
    }

    public List<Card> getCardsPossibleToPlay(int maxMana) {
        return hand.values().stream()
                .filter(c -> c.getCost() <= maxMana)
                .collect(toList());
    }

    public List<Card> getSpellsPossibleToPlay(int maxMana) {
        return hand.values().stream()
                .filter(c -> c.getCost() <= maxMana && c.getType() == CardType.SPELL)
                .collect(toList());
    }

    public void addMonsterToTable(Monster monster) {
        table.put(monster.getUuid(), monster);
    }

    public LinkedList<Card> getDeck() {
        return deck;
    }

    public List<Set<Card>> getPossiblePlays() {
        return Sets.powerSet(hand.values().stream().filter(c -> c.getType() == MONSTER || (c.getType() == CardType.SPELL && ((Spell) c).getAction() == SpellAction.DRAW_2_CARDS)).collect(toSet()))
                .stream()
                .filter(ss -> ss.stream().mapToInt(Card::getCost).sum() < this.mana)
                .filter(ss -> ss.stream().filter(c -> c.getType() == MONSTER).count() + this.table.size() <= 7)
                .collect(toList());
    }

    public List<Spell> getOffensiveCards() {
        return hand.values().stream()
                .filter(s -> s.getType() == CardType.SPELL)
                .filter(s -> s.getCost() <= mana)
                .map(c -> (Spell) c)
                .filter(Spell::isOffensive)
                .collect(Collectors.toList());
    }

    public void drawCard(Card c) {
        this.deck.remove(c);
        this.hand.put(c.getUuid(), c);
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }
}
