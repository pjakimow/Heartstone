package com.github.pjakimow.xenteros.card;

import java.util.UUID;

import static com.github.pjakimow.xenteros.card.MonsterAbility.CHARGE;
import static com.github.pjakimow.xenteros.card.MonsterAbility.TAUNT;

public class Monster implements Card {

    private String uuid = UUID.randomUUID().toString();
    private CardType type;
    private int cost;
    private int attack;
    private int health;
    private MonsterAbility monsterAbility;

    public Monster(CardType type, int cost, int attack, int health, MonsterAbility monsterAbility) {
        this.type = type;
        this.cost = cost;
        this.attack = attack;
        this.health = health;
        this.monsterAbility = monsterAbility;
    }

    public static Monster fromMonster(Monster that) {
        return new Monster(that.getType(), that.getCost(), that.getAttack(), that.getHealth(), that.getMonsterAbility());
    }

    @Override
    public CardType getType() {
        return type;
    }

    @Override
    public String getUuid() {
        return uuid;
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

    public boolean hasTaunt() {
        return monsterAbility == TAUNT;
    }

    public boolean hasCharge() {
        return monsterAbility == CHARGE;
    }

    public MonsterAbility getMonsterAbility() {
        return monsterAbility;
    }

    public void receiveAttack(int attack) {
        this.health -= attack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Monster)) return false;

        Monster monster = (Monster) o;

        return uuid.equals(monster.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "Monster:" +
                " C=" + cost +
                ", A=" + attack +
                ", H=" + health +
                ", ability=" + monsterAbility;
    }
    
}
