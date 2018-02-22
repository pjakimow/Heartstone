package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import constant.Constant;

public class PaneController {

	@FXML
	private Label deckSize1, deckSize2;
	
	@FXML 
	private Label manaAmound1, manaMaxAmound1, manaAmound2, manaMaxAmound2;
	
	@FXML
	private HBox hand1;
	
	public PaneController() {
		System.out.println("Controller");
	}

	@FXML
	void initialize() {
		//init mana, deckSize and lifePoints
		deckSize1.setText(Constant.maxDeckSize + "");
		deckSize2.setText(Constant.maxDeckSize + "");
		manaAmound1.setText(Constant.manaOnStart + "");
		manaMaxAmound1.setText(Constant.manaOnStart + "");
		manaAmound2.setText(Constant.manaOnStart + "");
		manaMaxAmound2.setText(Constant.manaOnStart + "");
		//TODO: lifePoints
		
		//draw first cards of both heroes
		Rectangle card = new Rectangle();
		card.setWidth(80);
		card.setHeight(100);
		hand1.getChildren().add(card);
	}
	
	@FXML
	public void endOfTurnOnAction() {
		System.out.println("metoda");
	}
}
