package com.github.pjakimow.xenteros.player;

import com.github.pjakimow.xenteros.card.*;
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

    public void move(Player player, Player opponent, int round) {
        player.beginTurn(round);
        List<Monster> playedMonsters = new ArrayList<>();


        while (player.canPlayCard()) {
            Card userChoice = askForCard(player);
            if (userChoice == null) {
                break;
            }

            try{
                player.playCard(userChoice.getUuid());
            } catch (IllegalMoveException e) {
                System.out.println("You already have 7 monsters on the table. Pick another one.");
                continue;
            }

            if (userChoice instanceof Monster) {
                playedMonsters.add((Monster) userChoice);
            } else {
                throwSpell((Spell) userChoice, player, opponent);
            }
        }

        List<Monster> table = player.getTable();
        for (Monster monster : table) {
            attackOpponent(monster.getAttack(), opponent);
        }

        player.moveMonstersToTable(playedMonsters);
    }


    public void setUp(Player white, Player black) {
        white.drawCards(3);
        black.drawCards(4);
    }

    private void throwSpell(Spell spell, Player player, Player opponent) {
        SpellAction spellAction = spell.getAction();

        switch (spellAction) {
            case DEAL_1_DAMAGE_DRAW_1_CARD:
                player.drawCards(1);
                attackOpponent(1, opponent);
                break;
            case DEAL_2_DAMAGE_RESTORE_2_HEALTH:
                player.heal(2);
                attackOpponent(2, opponent);
                break;
            case DRAW_2_CARDS:
                player.drawCards(2);
                break;
            default:
                break;
        }
    }

    private Card askForCard(Player player) {
        List<Card> hand = player.getHand();
        System.out.println(format("Your current health is: %d.", player.getHealth()));
        System.out.println(format("Which card would you like to play? You have %d of mana.", player.getMana()));
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ": " + hand.get(i));
        }
        System.out.println("Other number: I don't want to play more cards.");

        int userChoice = sc.nextInt();
        if (userChoice <= 0 || userChoice > hand.size() || player.getMana() < hand.get(userChoice - 1).getCost()) {
            return null;
        }
        return hand.get(userChoice - 1);
    }

    private void attackOpponent(int power, Player opponent) {
        List<Monster> opponentTable = opponent.getTable();
        int opponentHealth = opponent.getHealth();
        System.out.println(format("Who would you like to attack with %d damage?", power));

        for (int i = 0; i < opponentTable.size(); i++) {
            System.out.println((i + 1) + ": " + opponentTable.get(i));
        }
        System.out.println(format("Other number: Character with %d health.", opponentHealth));

        int userChoice = sc.nextInt();
        if (userChoice <= 0 || userChoice > opponentTable.size()) {
            opponent.receiveAttack(power);
        } else {
            opponent.receiveAttack(opponentTable.get(userChoice - 1).getUuid(), power);
        }
    }
}
