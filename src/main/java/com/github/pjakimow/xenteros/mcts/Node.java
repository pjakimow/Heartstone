package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Node> children = null;
    private Node parent = null;
    private int visited, reward;

    private Player me, opponent;

    public Node(Player me, Player opponent) {
        this.me = me.deepCopy();
        this.opponent = opponent.deepCopy();
        visited = reward = 0;
        children = new ArrayList<Node>();
    }

    public Node getBestChild(double c) {
        Node result = null;
        double best = 0, current = 0;

        for (Node child : children) {
            if (child.visited == 0)
                return child;
            current = child.reward / child.visited + (c * Math.sqrt(2 * Math.log(this.visited) / child.visited));
            if (current > best) {
                result = child;
                best = current;
            }
        }

        return result;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void addChild(Node child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void setParent(Node parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void addChildren(List<Node> children) {
        this.children.addAll(children);
        for (Node child : children)
            child.setParent(this);
    }

    public void incrementReward() {
        this.reward++;
    }

    public void incrementVisited() {
        this.visited++;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public Player getMe() {
        return me;
    }

    public Player getOpponent() {
        return opponent;
    }

    public int getVisited() {
        return visited;
    }

    public int getReward() {
        return reward;
    }

}
