package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.card.*;
import com.github.pjakimow.xenteros.player.IllegalMoveException;
import com.github.pjakimow.xenteros.player.Player;
import com.github.pjakimow.xenteros.player.PlayerDeadException;

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
    private int round;

    public Node(Player me, Player opponent, MoveToMake moveToMake, int round) {
//        System.out.println("Creating node with mtm: " + moveToMake);
//        System.out.println(me);
        this.me = me.deepCopy();
        this.opponent = opponent.deepCopy();
        visited = reward = 0;
        this.moveToMake = moveToMake;
        this.round = round;
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
            if (current >= best) {
                result = child;
                best = current;
            }
        }

        if (children.isEmpty()) {
            Node n = new Node(me, opponent, moveToMake.next(), round);
            addChild(n);
            return n;
        }

        MoveToMake nextMove = moveToMake.next();
        switch (nextMove) {
            case I_DRAW:
                return result.getBestChildDraw();
            case I_PLAY:
                return result.getBestChildPlay(result.getMe());
            case I_ATTACK:
                return result.getBestChildAttack(result.getMe(), result.getOpponent());
            case HE_DRAWS:
                return result.getBestChildDraw();
            case HE_PLAYS:
                return result.getBestChildPlay(result.getOpponent());
            case HE_ATTACKS:
                return result.getBestChildAttack(result.getOpponent(), result.getMe());
        }

        return result;
    }

    Node select() {
        switch (moveToMake) {
            case I_DRAW:
                return getBestChildDraw();
            case I_PLAY:
                return getBestChildPlay(me);
            case I_ATTACK:
                return getBestChildAttack(me, opponent);
            case HE_DRAWS:
                return getBestChildDraw();
            case HE_PLAYS:
                return getBestChildPlay(opponent);
            case HE_ATTACKS:
                return getBestChildAttack(opponent, me);
        }
        return null;
    }

    private Node getBestChildDraw() {

        if (this.possibleDraws.isEmpty()) {
            return getBestChild(Math.sqrt(2));
        }
        shuffle(this.possibleDraws);
        Card c = this.possibleDraws.remove(0);
        Node n = new Node(me, opponent, moveToMake.next(), this.round);
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
        Node n = new Node(me, opponent, moveToMake.next(), this.round);
        this.addChild(n);

        if (moveToMake == MoveToMake.I_PLAY) {
            player = n.me;
        } else {
            player = n.opponent;
        }

        for (Card card : c) {
            try{
                player.playCard(card.getUuid());
            }catch (IllegalMoveException e){}
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
        Node n = new Node(me, opponent, moveToMake.next(), this.round);
        addChild(n);

        if (moveToMake == MoveToMake.I_ATTACK) {
            from = n.me;
            to = n.opponent;
        } else {
            from = n.opponent;
            to = n.me;
        }

        for (Pair pair : move) {
            int power = 0;
            if (pair.getFrom().getType() == CardType.MONSTER) {
                power = ((Monster) pair.getFrom()).getAttack();
            } else {
                Spell spell = (Spell) pair.getFrom();
                if (spell.getAction() == SpellAction.DEAL_1_DAMAGE_DRAW_1_CARD) {
                    power = 1;
                }
                if (spell.getAction() == SpellAction.DEAL_2_DAMAGE_RESTORE_2_HEALTH) {
                    power = 2;
                }
            }
            if (pair.getTo() == null) {
                try {
                    to.receiveAttack(power);
                } catch (PlayerDeadException e) {

                }
            } else {
                to.receiveAttack(pair.getTo().getUuid(), power);
            }
        }
        from.moveMonstersToTable();
        return n;

    }

    private void addChild(Node child) {
        this.children.add(child);
        child.setParent(this);
    }

    private void setParent(Node parent) {
        if (moveToMake == MoveToMake.I_DRAW) {
            this.round++;
        }
        this.parent = parent;
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


    public void simulate() {
        int round = this.round;
        Player me = this.me.deepCopy();
        Player opponent = this.opponent.deepCopy();
        MoveToMake state = this.moveToMake;
        while (me.getHealth() > 0 && opponent.getHealth() > 0) {
//            System.out.println(me.getHealth() + " " + opponent.getHealth());
            switch (state) {
                case I_DRAW:
                    round++;
                    me.beginTurn(round);
                    me.shuffleDeck();
                    me.drawCards(1);
                    break;
                case I_PLAY:
                    List<Set<Card>> possiblePlays = me.getPossiblePlays();
                    shuffle(possiblePlays);
                    if (possiblePlays.isEmpty()) {
                        break;
                    }
                    for (Card card : possiblePlays.get(0)) {
                        me.playCard(card.getUuid());
                        if (card instanceof Spell) {
                            me.drawCards(2);
                        }
                    }

                    break;
                case I_ATTACK:
                    List<List<Pair>> possibleAttacks = getPossibleAttacks(me, opponent);
                    shuffle(possibleAttacks);
                    if (possibleAttacks.isEmpty()) {
                        break;
                    }
                    List<Pair> move = possibleAttacks.get(0);
                    for (Pair pair : move) {
                        int power = 0;
                        if (pair.getFrom().getType() == CardType.MONSTER) {
                            power = ((Monster) pair.getFrom()).getAttack();
                        } else {
                            Spell spell = (Spell) pair.getFrom();
                            if (spell.getAction() == SpellAction.DEAL_1_DAMAGE_DRAW_1_CARD) {
                                power = 1;
                            }
                            if (spell.getAction() == SpellAction.DEAL_2_DAMAGE_RESTORE_2_HEALTH) {
                                power = 2;
                            }
                        }
                        if (pair.getTo() == null) {
                            try {
                                opponent.receiveAttack(power);
                            } catch (PlayerDeadException e) {
                                break;
                            }
                        } else {
                            opponent.receiveAttack(pair.getTo().getUuid(), power);
                        }
                    }
                    me.moveMonstersToTable();
                    break;
                case HE_DRAWS:
                    opponent.beginTurn(round);
                    opponent.shuffleDeck();
                    opponent.drawCards(1);
                    break;
                case HE_PLAYS:
                    List<Set<Card>> hisPossiblePlays = opponent.getPossiblePlays();
                    shuffle(hisPossiblePlays);
                    if (hisPossiblePlays.isEmpty()) {
                        break;
                    }
                    for (Card card : hisPossiblePlays.get(0)) {
                        try {
                            opponent.playCard(card.getUuid());
                        } catch (IllegalMoveException e) {
                        }
                        if (card instanceof Spell) {
                            opponent.drawCards(2);
                        }
                    }
                    break;
                case HE_ATTACKS:
                    List<List<Pair>> hisPossibleAttacks = getPossibleAttacks(opponent, me);
                    shuffle(hisPossibleAttacks);
                    if (hisPossibleAttacks.isEmpty()) {
                        break;
                    }
                    List<Pair> hisMove = hisPossibleAttacks.get(0);
                    for (Pair pair : hisMove) {
                        int power = 0;
                        if (pair.getFrom().getType() == CardType.MONSTER) {
                            power = ((Monster) pair.getFrom()).getAttack();
                        } else {
                            Spell spell = (Spell) pair.getFrom();
                            if (spell.getAction() == SpellAction.DEAL_1_DAMAGE_DRAW_1_CARD) {
                                power = 1;
                            }
                            if (spell.getAction() == SpellAction.DEAL_2_DAMAGE_RESTORE_2_HEALTH) {
                                power = 2;
                            }
                        }
                        if (pair.getTo() == null) {
                            try {
                                me.receiveAttack(power);
                            } catch (PlayerDeadException e) {
                                break;
                            }
                        } else {
                            me.receiveAttack(pair.getTo().getUuid(), power);
                        }
                    }
                    opponent.moveMonstersToTable();
                    break;
            }
            state = state.next();
        }

        boolean won = me.getHealth() >= opponent.getHealth();
        this.visited++;
        if (this.moveToMake.name().startsWith("I")) {
            reward += won ? 1 : 0;
        } else {
            reward += won ? 0 : 1;
        }

        Node n = parent;
        while (n != null) {
            n.visited++;
            if (n.moveToMake.name().startsWith("I")) {
                n.reward += won ? 1 : 0;
            } else {
                n.reward += won ? 0 : 1;
            }
            n = n.parent;
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "visited=" + visited +
                ", reward=" + reward +
                ", moveToMake=" + moveToMake +
                '}';
    }

    public double winRatio() {
        return 1.0 * reward / visited;
    }

    public int getDepth() {
        return children.stream().mapToInt(Node::getDepth).max().orElse(0) + 1;
    }
}
