package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.Monster;
import com.github.pjakimow.xenteros.card.MonsterAbility;
import com.github.pjakimow.xenteros.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

public class Node {

    private List<Node> children = new ArrayList<>();
    private Node parent = null;
    private int visited, reward;
    private Player me, opponent;
    private MoveToMake moveToMake;
    private List<Card> possibleDraws;
    private List<Set<Card>> possiblePlays;
    private List<Pair> possibleAttacks;

    public Node(Player me, Player opponent, MoveToMake moveToMake) {
        this.me = me.deepCopy();
        this.opponent = opponent.deepCopy();
        visited = reward = 0;
        this.moveToMake = moveToMake;
        switch (moveToMake) {
            case I_DRAW:
                this.possibleDraws = me.getDeck().stream().collect(toList());
                break;
            case I_PLAY:
                this.possiblePlays = me.getPossibleMoves();
                break;
            case I_ATTACK:
                this.possibleAttacks = getPossibleAttacks(me, opponent);
                break;
            case HE_DRAWS:
                this.possibleDraws = opponent.getDeck().stream().collect(toList());
                break;
            case HE_PLAYS:
                this.possiblePlays = opponent.getPossibleMoves();
                break;
            case HE_ATTACKS:
                this.possibleAttacks = getPossibleAttacks(opponent, me);
                break;
        }
    }

    private Node getBestChild(double c) {
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

    private Node select() {
        switch (moveToMake) {
            case I_DRAW:
                return getBestChildDraw(me);
            case I_PLAY:
                return getBestChildPlay(me);
            case I_ATTACK:
                break;
            case HE_DRAWS:
                return getBestChildDraw(opponent);
            case HE_PLAYS:
                return getBestChildPlay(opponent);
            case HE_ATTACKS:
                break;
        }
        return null;
    }

    private Node getBestChildDraw(Player player) {

        if (this.possibleDraws.isEmpty()) {
            return getBestChild(Math.sqrt(2));
        }
        shuffle(this.possibleDraws);
        Card c = this.possibleDraws.remove(0);
        Node n = new Node(me, opponent, moveToMake.next());
        n.setParent(this);
        this.addChild(n);
        return n;
    }

    private Node getBestChildPlay(Player player) {
        if (this.possiblePlays.isEmpty()) {
            return getBestChild(Math.sqrt(2));
        }
        shuffle(this.possiblePlays);
        Set<Card> c = this.possiblePlays.remove(0);
        Node n = new Node(me, opponent, moveToMake.next());
        n.setParent(this);
        this.addChild(n);
        return n;
    }

    public boolean isRoot() {
        return parent == null;
    }

    private boolean hasChild() {
        return !this.children.isEmpty();
    }

    private void addChild(Node child) {
        this.children.add(child);
        child.setParent(this);
    }

    private void setParent(Node parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public void addChildren(List<Node> children) {
        this.children.addAll(children);
        for (Node child : children)
            child.setParent(this);
    }

    private List<Pair> getPossibleAttacks(Player from, Player to) {
        List<Monster> myTable = from.getTable();
        List<Monster> hisTable = to.getMonstersToAttack();
        List<int[]> combinations = new ArrayList<>();

        List<Pair> collect = from.getTable().stream()
                .flatMap(attacker -> to.getMonstersToAttack().stream().map(attackee -> new Pair(attacker, attackee)))
                .collect(toList());
        if (!collect.stream().anyMatch(  p -> p.getTo().getMonsterAbility() == MonsterAbility.TAUNT)) {
            collect.addAll(from.getTable().stream().map(c -> new Pair(c, null)).collect(toList()));
        }
        return collect;
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
