package com.github.pjakimow.xenteros.card;

import java.util.UUID;

public class Spell implements Card {

    private String uuid = UUID.randomUUID().toString();
    private CardType type;
    private int cost;
    private SpellAction action;


    Spell(CardType type, int cost, SpellAction action) {
        this.type = type;
        this.cost = cost;
        this.action = action;
    }

    static Spell fromSpell(Spell that) {
        return new Spell(that.getType(), that.getCost(), that.getAction());
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public CardType getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public SpellAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "Spell:" +
                "C=" + cost +
                ", A=" + action;
    }
}
