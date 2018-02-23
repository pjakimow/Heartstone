package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.Card;

import java.util.*;

public class Player {

    private int health = 20;
    private int mana;
    private Map<String, Card> hand = new HashMap<>();
    private Map<String, Card> table = new HashMap<>();
    private Queue<Card> deck = new LinkedList<>();
    private Set<Card> trashedCards = new HashSet<>();

    public Player(int mana) {
        this.mana = mana;
    }

    public Card playCard(String uuid) {

        Card card = hand.remove(uuid);
        this.mana -= card.getCost();

        return card;
    }

    public void receiveAttack(String uuid, Card card) {
        //TODO
    }



}
