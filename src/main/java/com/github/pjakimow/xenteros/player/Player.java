package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.Monster;

import java.util.*;

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
    private List<Card> trashedCards = new LinkedList<>();

    Player(int mana, List<Card> cards) {
        this.mana = mana;
        shuffle(cards);
        deck = new LinkedList<>(cards);
    }

    List<Card> getHand() {
        return hand.values().stream()
                .collect(toList());
    }

    List<Monster> getTable() {
        return table.values().stream()
                .collect(toList());
    }

    int getHealth() {
        return health;
    }

    void trashCard(Card card) {
        trashedCards.add(card);
    }

    void beginTurn() {
        drawCards(1);
    }

    void endTurn() {
        temp.forEach(m -> table.put(m.getUuid(), m));
        temp.clear();
    }

    public Card playCard(String uuid) {

        Card card = hand.get(uuid);
        if (card.getCost() > this.mana) {
            throw new IllegalMoveException();
        }
        if (card instanceof Monster && table.size() + temp.size() >= 7) {
            throw new IllegalMoveException();
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
            trashedCards.add(attackedCard);
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
            shuffle(trashedCards);
            deck.addAll(trashedCards);
            trashedCards = new LinkedList<>();
        }
        return Optional.ofNullable(deck.poll());
    }

    void drawCards(int n) {
        for (int i = 0; i < n; i++) {
            drawCard().ifPresent(c -> hand.put(c.getUuid(), c));
        }
    }

    void setMana(int round) {
        this.mana = min(round, 10);
    }

    boolean canPlayCard() {
        return hand.values().stream()
                .mapToInt(Card::getCost)
                .min()
                .orElse(Integer.MAX_VALUE) <= this.mana;
    }

    void moveMonstersToTable(Collection<Monster> monsters) {
        monsters.forEach(m -> table.put(m.getUuid(), m));
    }
}
