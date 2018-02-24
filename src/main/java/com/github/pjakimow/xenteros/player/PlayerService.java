package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.DeckProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerService {

    private DeckProvider deckProvider;

    @Autowired
    public PlayerService(DeckProvider deckProvider) {
        this.deckProvider = deckProvider;
    }

    public Player createPlayer() {
        return new Player(1, deckProvider.getDeck());
    }

    public void move(Player player) {

        while (player.canPlayCard()) {
            //TODO ask which card they want to play. Don't ask for UUID as it's too long to cast.
        }
        //TODO attack with all cards on table

        //TODO add played cards to table

    }


    public void setUp(Player white, Player black) {
        white.drawCards(3);
        black.drawCards(4);
    }
}
