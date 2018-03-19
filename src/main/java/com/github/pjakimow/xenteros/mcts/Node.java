package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.card.*;
import com.github.pjakimow.xenteros.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
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
    private List<List<Pair>> possibleAttacks;

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
                this.possiblePlays = me.getPossiblePlays();
                break;
            case I_ATTACK:
                this.possibleAttacks = getPossibleAttacks(me, opponent);
                break;
            case HE_DRAWS:
                this.possibleDraws = opponent.getDeck().stream().collect(toList());
                break;
            case HE_PLAYS:
                this.possiblePlays = opponent.getPossiblePlays();
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
                return getBestChildAttack(me, opponent);
            case HE_DRAWS:
                return getBestChildDraw(opponent);
            case HE_PLAYS:
                return getBestChildPlay(opponent);
            case HE_ATTACKS:
                return getBestChildAttack(opponent, me);
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
        if (moveToMake == MoveToMake.I_DRAW) {
            n.getMe().drawCard(c);
        } else {
            n.getOpponent().drawCard(c);
        }
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
        for (Card card : c) {
            player.playCard(card.getUuid());
            if (card instanceof Spell) {
                player.drawCards(2);
            }
        }
        return n;
    }

    private Node getBestChildAttack(Player from, Player to) {
        if (this.possibleAttacks.isEmpty()) {
            return getBestChild(Math.sqrt(2));
        }
        shuffle(this.possibleAttacks);
        List<Pair> move = this.possibleAttacks.remove(0);
        Node n = new Node(me, opponent, moveToMake.next());
        n.setParent(this);
        for (Pair pair : move) {
            int power = 0;
            if (pair.getFrom().getType() == CardType.MONSTER) {
                power = ((Monster)pair.getFrom()).getAttack();
            } else {
                Spell spell = (Spell)pair.getFrom();
                if (spell.getAction() == SpellAction.DEAL_1_DAMAGE_DRAW_1_CARD) {
                    power = 1;
                }
                if (spell.getAction() == SpellAction.DEAL_2_DAMAGE_RESTORE_2_HEALTH) {
                    power = 2;
                }
            }
            if (pair.getTo() == null) {
                to.receiveAttack(power);
            } else {
                to.receiveAttack(pair.getTo().getUuid(), power);
            }
        }
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

    private List<List<Pair>> getPossibleAttacks(Player from, Player to) {
        int base = to.getMonstersToAttack().size() + 1;

        List<Card> cardsThatCanAttack = new ArrayList<>();
        cardsThatCanAttack.addAll(from.getTable());
        cardsThatCanAttack.addAll(from.getOffensiveCards());

        int[] indexes = new int[cardsThatCanAttack.size()];

        List<List<Pair>> result = new ArrayList<>();
        int rounds = (int) pow(base, indexes.length);

        for (int i = 0; i < rounds; i++) {
            List<Pair> move = new ArrayList<>();
            for (int j = 0; j < cardsThatCanAttack.size(); j++) {
                move.add(new Pair(cardsThatCanAttack.get(j), getCardToPair(indexes, to.getMonstersToAttack(), j)));
            }
            if (move.stream()
                    .filter(c -> c.getFrom().getType() == CardType.SPELL)
                    .mapToInt(c -> c.getFrom().getCost())
                    .sum() <= from.getMana()) {
                result.add(move);
            }
        }

        return result;
    }

    private Monster getCardToPair(int[] state, List<Monster> monsters, int i) {
        return state[i] == monsters.size() ? null : monsters.get(state[i]);
    }

    private void increment(int[] array, int base) {
        for (int i = 0; i < array.length - 1; i++) {
            array[i] += 1;
            if (array[i] >= base) {
                array[i] = array[i] % base;
                array[i + 1] += 1;
            }
        }
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

    public boolean isGameOver() {
        return me.getHealth() <= 0 || opponent.getHealth() <= 0;
    }
}
