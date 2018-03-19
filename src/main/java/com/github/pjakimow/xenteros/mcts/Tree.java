package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.player.Player;

public class Tree {

    private Node root;
    private Player me;
    private Player opponent;

    public Tree(Player me, Player he, int round) {
        this.me = me;
        this.opponent = he;
        this.root = new Node(me, he, MoveToMake.I_PLAY, round);
    }

    public void move(int seconds) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1000*seconds) {
            Node selection = root.select();
            selection.simulate();
        }

        System.out.println(root.getChildren());

    }
}
