package com.github.pjakimow.xenteros.mcts;

import com.github.pjakimow.xenteros.player.Player;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;

public class Tree {

    private Node root;
    private Player me;
    private Player opponent;

    public Tree(Player me, Player he, int round) {
        this.me = me;
        this.opponent = he;
        this.me.drawCards(1);
        this.root = new Node(me, he, MoveToMake.I_PLAY, round);
    }

    public Node move(int seconds) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1000*seconds) {
            Node selection = root.select();
            selection.simulate();
        }

//        System.out.println(root.getChildren());
        double bestWay = -1;
        Node best = null;
        for (Node node : root.getChildren()) {
            for (Node grandChild : node.getChildren()) {

                if (grandChild.winRatio() >= bestWay) {
                    bestWay = grandChild.winRatio();
                    best = grandChild;
                }
            }
        }
        if(best == null) {
            System.out.println("Best == null" + root.getChildren().stream().mapToInt(s -> s.getChildren().size()).sum());
            return move(seconds);
        }
//        System.out.println(best);
        return best;
    }

    public int getPaths() {
        return root.getVisited();
    }

    public int getDepth() {
        return root.getDepth();
    }


    public String getStatistics() {

        List<Integer> leafsDepths = new ArrayList<>();
        root.getLeafStatistics(leafsDepths, 1);
        leafsDepths.sort(Integer::compare);
        IntSummaryStatistics statistics = leafsDepths.stream().mapToInt(Integer::intValue).summaryStatistics();
        StringBuilder sb = new StringBuilder();
        sb.append(root.getVisited())
                .append(",")
                .append(root.getDepth())
                .append(",")
                .append(statistics.getAverage())
                .append(",")
                .append(statistics.getMax())
                .append(",")
                .append(leafsDepths.get(leafsDepths.size()/2));

        return sb.toString();
    }
}
