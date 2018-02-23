package com.github.pjakimow.xenteros;

import com.github.pjakimow.xenteros.card.DeckProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class Application {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        DeckProvider deckProvider = context.getBean(DeckProvider.class);
        System.out.println(deckProvider.getDeck().size());


    }

}
