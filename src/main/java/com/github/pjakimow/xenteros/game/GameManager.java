package com.github.pjakimow.xenteros.game;

import com.github.pjakimow.xenteros.player.Player;
import com.github.pjakimow.xenteros.player.PlayerDeadException;
import com.github.pjakimow.xenteros.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        while(true) {

            playerService.setUp(white, black);

            try {
                playerService.move(white, black);
            }catch (PlayerDeadException e) {
                //white won, as during his turn the exception was thrown
            }try {
                playerService.move(black, white);
            } catch (PlayerDeadException e) {
                //black won, as during his turn the exception was thrown
            }
        }
    }
}
