package com.github.pjakimow.xenteros.card;

import java.util.Comparator;

public class AttackCardComp2 implements Comparator<Card> {

	@Override
	public int compare(Card o1, Card o2) {
		if ( o1 instanceof Monster){
			if (o2 instanceof Monster){

				if ( ((Monster)o1).getMonsterAbility() == MonsterAbility.CHARGE ){
					if ( ((Monster)o2).getMonsterAbility() != MonsterAbility.CHARGE){
						return -1;
					} else {
						return ((Monster)o1).getAttack() == ((Monster)o2).getAttack() ? (((Monster)o1).getCost() < ((Monster)o2).getCost() ? -1 : 1) :
								((Monster)o1).getAttack() > ((Monster)o2).getAttack() ? -1 : 1;
					}
				}
			} else {
				return ((Monster)o1).getMonsterAbility() == MonsterAbility.CHARGE ? (
							((Monster)o1).getAttack() == ((Spell)o2).getAction().getPriority() ? (
								((Monster)o1).getCost() >= ((Spell)o2).getCost() ? 1 : -1
							) : ((Monster)o1).getAttack() > ((Spell)o2).getAction().getPriority() ? -1 : 1) : 1;
			}
		} else if (o2 instanceof Spell){
			return ((Spell)o1).getAction() == ((Spell)o2).getAction() ? ( o1.getCost() < o2.getCost() ? -1 : 1):
				((Spell)o1).getAction().getPriority() > ((Spell)o2).getAction().getPriority() ? -1 : 1;
		}

		return ((Monster)o2).getMonsterAbility() == MonsterAbility.CHARGE ? (
				((Monster)o2).getAttack() == ((Spell)o1).getAction().getPriority() ? (
						((Monster)o2).getCost() >= ((Spell)o1).getCost() ? -1 : 1
				) : ((Monster)o2).getAttack() > ((Spell)o1).getAction().getPriority() ? 1 : -1) : -1;
	}
}
