package com.github.pjakimow.xenteros.mcts;

import java.util.ArrayList;
import java.util.List;

import com.github.pjakimow.xenteros.player.Player;

public class Node {
	private List<Node> children = null;
	private Node parent = null;
	    
	private Player player1;
	private Player player2;

	private int wins;
	private int playouts;
	
	public Node(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
		wins = playouts = 0;
		children = new ArrayList<Node>();
	}

	public boolean isRoot(){
		return parent == null;
	}
	
	public void addChild(Node child) {
		this.children.add(child);
        child.setParent(this);
    }

	public void setParent(Node parent) {
		this.parent = parent;
        parent.addChild(this);
    }

	public void addChildren(List<Node> children) {
		this.children.addAll(children);
		for ( Node child: children)
			child.setParent(this);
	}
	 
	public List<Node> getChildren() {
		return children;
	}

	public Node getParent() {
		return parent;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getPlayouts() {
		return playouts;
	}

	public void setPlayouts(int playouts) {
		this.playouts = playouts;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}
		
}
