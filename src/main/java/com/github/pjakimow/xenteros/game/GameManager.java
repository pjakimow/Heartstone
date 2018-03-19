package com.github.pjakimow.xenteros.game;

import com.github.pjakimow.xenteros.mcts.Tree;
import com.github.pjakimow.xenteros.player.AggressivePlayerService;
import com.github.pjakimow.xenteros.player.Player;
import com.github.pjakimow.xenteros.player.PlayerDeadException;
import com.github.pjakimow.xenteros.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
class GameManager {

    private PlayerService playerService;
//    private AggressivePlayerService playerService;
    private Player white;
    private Player black;

    @Autowired
    public GameManager(AggressivePlayerService playerService) {
        this.playerService = playerService;
        this.white = playerService.createPlayer();
        this.black = playerService.createPlayer();
        //run();
//        run2();
        run3();
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
//            System.out.println(format("--------ROUND %d--------",round));
//            System.out.println(format(">>White (%d HP) move:", white.getHealth()));
//            white.printHand();
//            white.printTable();
//            System.out.println(format(">Black (%d HP):", black.getHealth()));
//            black.printHand();
//            black.printTable();
            try {
                playerService.move(white, black, round);
            } catch (PlayerDeadException e) {
                System.out.println("White won!");
                return;
            }
//            System.out.println("--------------");
//            System.out.println(format(">>Black (%d HP) move:", black.getHealth()));
//            black.printHand();
//            black.printTable();
//            System.out.println(format(">White (%d HP):", white.getHealth()));
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

    private void run3() {
        playerService.setUp(white, black);
        Tree mcts = new Tree(white, black, 1);
        mcts.move(10);
    }
}
