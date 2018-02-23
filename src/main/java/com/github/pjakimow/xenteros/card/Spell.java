package com.github.pjakimow.xenteros.card;

import java.util.UUID;

class Spell implements Card {

    private String uuid = UUID.randomUUID().toString();
    private CardType type;
    private int cost;
    private SpellAction action;


    public Spell(CardType type, int cost, SpellAction action) {
        this.type = type;
        this.cost = cost;
        this.action = action;
    }

    @Override
    public CardType getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public int getCost() {
        return cost;
    }

    public SpellAction getAction() {
        return action;
    }
}
