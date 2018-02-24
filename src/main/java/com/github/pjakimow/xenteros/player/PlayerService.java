package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.Card;
import com.github.pjakimow.xenteros.card.DeckProvider;
import com.github.pjakimow.xenteros.card.Monster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.String.format;

@Component
public class PlayerService {

    private DeckProvider deckProvider;
    private Scanner sc = new Scanner(System.in);

    @Autowired
    public PlayerService(DeckProvider deckProvider) {
        this.deckProvider = deckProvider;
    }

    public Player createPlayer() {
        return new Player(1, deckProvider.getDeck());
    }

    public void move(Player player, Player opponent) {

        List<Monster> playedMonsters = new ArrayList<>();

        while (player.canPlayCard()) {
            //TODO ask which card they want to play. Don't ask for UUID as it's too long to cast.
            Card userChoice = askForCard(player);

            if (userChoice instanceof Monster) {
                playedMonsters.add((Monster) userChoice);
            } else {
                //TODO deal with spell
            }
        }

        List<Monster> table = player.getTable();
        for (Monster monster : table) {
            attackOpponent(monster, opponent);
        }

        player.moveMonstersToTable(playedMonsters);

    }


    public void setUp(Player white, Player black) {
        white.drawCards(3);
        black.drawCards(4);
    }

    private Card askForCard(Player player) {
        List<Card> hand = player.getHand();
        System.out.println("Which card would you like to play?");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ": " + hand.get(i));
        }
        System.out.println("0: I don't want to play more cards.");

        int userChoice = sc.nextInt();
        return hand.get(userChoice - 1);
    }

    private void attackOpponent(Monster monster, Player opponent) {
        List<Monster> opponentTable = opponent.getTable();
        int opponentHealth = opponent.getHealth();
        System.out.println("Who would you like to attack with: " + monster);

        for (int i = 0; i < opponentTable.size(); i++) {
            System.out.println((i+1) + ": " + opponentTable.get(i));
        }
        System.out.println(format("%d: Character with %d health.", 0, opponentHealth));

        int userChoice = sc.nextInt();
        if (userChoice == 0) {
            opponent.receiveAttack(monster.getAttack());
        } else {
            opponent.receiveAttack(opponentTable.get(userChoice-1).getUuid(), monster.getAttack());
        }
    }
}
