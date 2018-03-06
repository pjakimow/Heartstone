package com.github.pjakimow.xenteros.mcts;

enum MoveToMake {

    I_DRAW(0), I_PLAY(1), I_ATTACK(2), HE_DRAWS(3), HE_PLAYS(4), HE_ATTACKS(5);

    private final int i;

    MoveToMake(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }

    public MoveToMake next() {
        return values()[(i + 1) % values().length];
    }
}
