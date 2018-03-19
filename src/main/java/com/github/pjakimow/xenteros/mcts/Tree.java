package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.player.Player;

class Tree {

    private Node root;

    public Tree(Player me, Player he) {
        this.root = new Node(me, he, MoveToMake.I_PLAY);
    }
}
