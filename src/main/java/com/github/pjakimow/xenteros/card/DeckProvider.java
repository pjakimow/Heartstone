package com.github.pjakimow.xenteros.card;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Component
public class DeckProvider {

    public List<Card> getDeck() {

        List<Card> result = new LinkedList<>();
        try {
            result.addAll(readMonsters());
            result.addAll(readSpells());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Set<Monster> readMonsters() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("monsters.json").toURI());
        StringBuilder sb = new StringBuilder();
        Files.lines(path).forEach(s -> sb.append(s).append("\n"));
        Gson gson = new Gson();

        Monster[] monsters = gson.fromJson(sb.toString(), Monster[].class);

        return Arrays.stream(monsters)
                .flatMap(m -> Stream.of(new Monster(CardType.MONSTER, m.getCost(), m.getAttack(), m.getHealth(), m.getMonsterAbility()), m))
                .map(Monster::fromMonster)
                .collect(toSet());
    }

    private Set<Spell> readSpells() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("spells.json").toURI());
        StringBuilder sb = new StringBuilder();
        Files.lines(path).forEach(s -> sb.append(s).append("\n"));
        Gson gson = new Gson();

        Spell[] monsters = gson.fromJson(sb.toString(), Spell[].class);

        return Arrays.stream(monsters)
                .flatMap(s -> Stream.of(s, s))
                .map(s->new Spell(CardType.SPELL, s.getCost(), s.getAction()))
                .collect(toSet());
    }

}
