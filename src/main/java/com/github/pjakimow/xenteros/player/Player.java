package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.Monster;

import java.util.*;

import static java.lang.Math.min;
import static java.util.Collections.shuffle;

public class Player {

    private int health = 20;
    private int mana;
    private Queue<Card> deck;
    private Map<String, Card> hand = new HashMap<>();
    private Map<String, Monster> table = new HashMap<>();
    private List<Card> trashedCards = new LinkedList<>();

    Player(int mana, List<Card> cards) {
        this.mana = mana;
        shuffle(cards);
        deck = new LinkedList<>(cards);
    }

    public Card playCard(String uuid) {

        Card card = hand.get(uuid);
        if (card.getCost() > this.mana) {
            throw new IllegalMoveException();
        }
        if (card instanceof Monster && table.size() == 7) {
            throw new IllegalMoveException();
        }

        hand.remove(uuid);
        this.mana -= card.getCost();

        trashedCards.add(card);
        drawCard().ifPresent(c -> hand.put(c.getUuid(), c));

        return card;
    }

    public void receiveAttack(String uuid, int power) {
        Monster attackedCard = table.get(uuid);
        attackedCard.receiveAttack(power);

        if (attackedCard.getHealth() < 0) {
            table.remove(uuid);
            trashedCards.add(attackedCard);
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
        return hand.values().stream().mapToInt(Card::getCost).min().orElse(Integer.MAX_VALUE) <= this.mana;
    }
}
