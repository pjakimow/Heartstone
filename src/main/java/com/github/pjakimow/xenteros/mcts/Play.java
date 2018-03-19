package com.github.pjakimow.xenteros.mcts;

class Play {

    private int i;
    private PlayType playType;
    private int t;

    public Play(int i, PlayType playType, int t) {
        this.i = i;
        this.playType = playType;
        this.t = t;
    }

    public Play(int i, PlayType playType) {
        this.i = i;
        this.playType = playType;
    }

    public int getI() {
        return i;
    }

    public PlayType getPlayType() {
        return playType;
    }

    public int getT() {
        return t;
    }
}
