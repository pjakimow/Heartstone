package com.github.pjakimow.xenteros.card;

import java.util.UUID;

public class Spell implements Card {

    private String uuid = UUID.randomUUID().toString();
    private CardType type;
    private int cost;
    private SpellAction action;


    public Spell(CardType type, int cost, SpellAction action, String uuid) {
        this.type = type;
        this.cost = cost;
        this.action = action;
        this.uuid = uuid;
    }

    public static Spell fromSpell(Spell that) {
        return new Spell(that.getType(), that.getCost(), that.getAction(), that.getUuid());
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

    public boolean isOffensive() {
        return action == SpellAction.DEAL_1_DAMAGE_DRAW_1_CARD || action == SpellAction.DEAL_2_DAMAGE_RESTORE_2_HEALTH;
    }

    @Override
    public String toString() {
        return "Spell:" +
                "C=" + cost +
                ", A=" + action;
    }

    @Override
    public Card deepCopy() {
        return fromSpell(this);
    }
}
