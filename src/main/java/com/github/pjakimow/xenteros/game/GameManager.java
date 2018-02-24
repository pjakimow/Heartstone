package com.github.pjakimow.xenteros.game;

import com.github.pjakimow.xenteros.player.Player;
import com.github.pjakimow.xenteros.player.PlayerDeadException;
import com.github.pjakimow.xenteros.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
class GameManager {

    private PlayerService playerService;

    @Autowired
    public GameManager(PlayerService playerService) {
        this.playerService = playerService;
        this.white = playerService.createPlayer();
        this.black = playerService.createPlayer();
        run();
    }

    private Player white;
    private Player black;

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
}
