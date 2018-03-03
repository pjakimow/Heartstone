package com.github.pjakimow.xenteros.card;

public enum SpellAction {
    DRAW_2_CARDS(0), DEAL_2_DAMAGE_RESTORE_2_HEALTH(2), DEAL_1_DAMAGE_DRAW_1_CARD(1);
	
	private final int priority;
	
	SpellAction(int priority){
		this.priority = priority;
	}
	
	public int getPriority(){
		return priority;
	}
}
