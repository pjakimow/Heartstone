package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.Monster;

class Pair {

    private Card from;
    private Monster to;

    public Pair(Card from, Monster to) {
        this.from = from;
        this.to = to;
    }

    public Card getFrom() {
        return from;
    }

    public Monster getTo() {
        return to;
    }
}
