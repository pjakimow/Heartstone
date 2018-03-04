package com.github.pjakimow.xenteros.card;

import java.util.Comparator;

public class AttackCardComp implements Comparator<Card> {

	@Override
	public int compare(Card o1, Card o2) {
		if ( o1 instanceof Monster){
			if (o2 instanceof Monster){
				return ((Monster)o1).getAttack() == ((Monster)o2).getAttack() ? (((Monster)o1).getCost() < ((Monster)o2).getCost() ? -1 : 1) : 
					((Monster)o1).getAttack() > ((Monster)o2).getAttack() ? -1 : 1;
			}	
		} else if (o2 instanceof Spell){
			return ((Spell)o1).getAction() == ((Spell)o2).getAction() ? ( o1.getCost() < o2.getCost() ? -1 : 1):
				((Spell)o1).getAction().getPriority() > ((Spell)o2).getAction().getPriority() ? -1 : 1;
		}
		
		return o1.getCost() == o2.getCost() ? 0 : o1.getCost() > o2.getCost() ? 1 : -1;
	}

}
