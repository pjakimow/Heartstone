package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.card.Card;

class Pair {

    private Card from;
    private Card to;

    public Pair(Card from, Card to) {
        this.from = from;
        this.to = to;
    }

    public Card getFrom() {
        return from;
    }

    public Card getTo() {
        return to;
    }
}
