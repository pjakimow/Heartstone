package com.github.pjakimow.xenteros.game;

import com.github.pjakimow.xenteros.mcts.Node;
import com.github.pjakimow.xenteros.mcts.Tree;
import com.github.pjakimow.xenteros.player.ControllingPlayerService;
import com.github.pjakimow.xenteros.player.Player;
import com.github.pjakimow.xenteros.player.PlayerDeadException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static java.lang.String.format;

@Component
class GameManager {

    //private PlayerService playerService;
    private ControllingPlayerService playerService;
    private Player white;
    private Player black;

    @Autowired
    public GameManager(ControllingPlayerService playerService) {
        this.playerService = playerService;
        //run();
//        run2();
//        run3();

        for (int i = 0; i < 150; i++) {
            System.out.println(i);
            this.white = null;
            this.black = null;
            System.gc();

            this.white = playerService.createPlayer();
            white.setName("WHITE");
            this.black = playerService.createPlayer();
            black.setName("BLACK");
            try {
                run3();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void run() {
        playerService.setUp(white, black);
        int round = 1;
        while (true) {
            System.out.println(format("--------ROUND %d--------",round));
            System.out.println("Black:");
            black.printHand();
            black.printTable();
            System.out.println("White move:");
            try {
                playerService.move(white, black, round);
            } catch (PlayerDeadException e) {
                System.out.println("White won!");
                return;
            }
            System.out.println("Black move:");
            System.out.println("White:");
            white.printHand();
            white.printTable();
            try {
                playerService.move(black, white, round);
            } catch (PlayerDeadException e) {
                System.out.println("Black won!");
                return;
            }
            round++;
        }
    }
    
    private void run2() {
        playerService.setUp(white, black);
        int round = 1;
        while (true) {
            System.out.println(format("--------ROUND %d--------",round));
            System.out.println(format(">>White (%d HP) move:", white.getHealth()));
//            white.printHand();
//            white.printTable();
            System.out.println(format(">Black (%d HP):", black.getHealth()));
//            black.printHand();
//            black.printTable();
            try {
                playerService.move(white, black, round);
            } catch (PlayerDeadException e) {
                System.out.println("White won!");
                return;
            }
//            System.out.println("--------------");
            System.out.println(format(">>Black (%d HP) move:", black.getHealth()));
//            black.printHand();
//            black.printTable();
            System.out.println(format(">White (%d HP):", white.getHealth()));
//            white.printHand();
//            white.printTable();
            try {
                playerService.move(black, white, round);
            } catch (PlayerDeadException e) {
                System.out.println("Black won!");
                return;
            }
            round++;
        }
    }

    private void run3() throws IOException {


        playerService.setUp(white, black);
        int round = 1;
        File result = new File("results-controlling-1s-summary.txt");
        while (true) {
            File file = new File("results-controlling-1s-round-" + round +".txt");
//            System.out.println(format("--------ROUND %d--------",round));
//            System.out.println(format(">>White (%d HP) move:", white.getHealth()));
            white.beginTurn(round);
//            white.printHand();
//            white.printTable();
//            System.out.println(format(">Black (%d HP):", black.getHealth()));
//            black.printHand();
//            black.printTable();
            System.gc();
            Tree tree = new Tree(white, black, round);
            Node move = tree.move(1);
            String statistics = tree.getStatistics();
            System.out.println(statistics);
            FileUtils.writeStringToFile(file, statistics + "\n", Charset.forName("UTF-8"), true);
//            System.out.println(tree.getPaths());
//            System.out.println(tree.getDepth());
            this.white = move.getMe();
            this.black = move.getOpponent();
            if (black.getHealth() <= 0) {
                System.out.println("White won " + white.getHealth() + " " + black.getHealth());
                FileUtils.writeStringToFile(result, white.getHealth() + " " + black.getHealth() + "\n", Charset.forName("UTF-8"), true);

                return;
            }
//            System.out.println("--------------");
//            System.out.println(format(">>Black (%d HP) move:", black.getHealth()));
//            System.out.println(format(">White (%d HP):", white.getHealth()));
//            white.printHand();
//            white.printTable();
            try {
                playerService.move(black, white, round);
                if (white.getHealth() < 0) {
                    throw new PlayerDeadException();
                }
            } catch (PlayerDeadException e) {
                System.out.println("Black won!" + white.getHealth() + " " + black.getHealth());
                FileUtils.writeStringToFile(result, white.getHealth() + " " + black.getHealth() + "\n", Charset.forName("UTF-8"), true);

                return;
            }
//            black.printHand();
//            black.printTable();
            round++;
        }

    }
}
