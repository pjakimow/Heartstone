package com.github.pjakimow.xenteros.card;

import java.util.UUID;

class Monster implements Card {

    private String uuid = UUID.randomUUID().toString();
    private CardType type;
    private int cost;
    private int attack;
    private int health;

    public Monster(CardType type, int cost, int attack, int health) {
        this.type = type;
        this.cost = cost;
        this.attack = attack;
        this.health = health;
    }

    @Override
    public CardType getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }
}
