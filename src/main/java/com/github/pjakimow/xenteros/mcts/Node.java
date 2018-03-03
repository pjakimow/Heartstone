package com.github.pjakimow.xenteros.mcts;

import java.util.ArrayList;
import java.util.List;

import com.github.pjakimow.xenteros.player.Player;

public class Node {
	private List<Node> children = null;
	private Node parent = null;
	private int visited, reward;
	
	private Player player1, player2;
	
	public Node(Player player1, Player player2){
		this.player1 = player1;
		this.player2 = player2;
		visited = reward = 0;
		children = new ArrayList<Node>();
	}

	public Node getBestChild(double c){
		Node result = null;
		double best = 0, current = 0;
		
		for (Node child: children){
			if (child.visited == 0)
				return child;
			current = child.reward / child.visited + (c * Math.sqrt(2 * Math.log(this.visited) / child.visited));
			if ( current > best	){
				result = child;
				best = current;
			}	
		}
		
		return result;
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
	
	public void incrementReward() {
		this.reward ++;
	}
	
	public void incrementVisited() {
		this.visited ++;
	}
	
	public List<Node> getChildren() {
		return children;
	}

	public Node getParent() {
		return parent;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public int getVisited() {
		return visited;
	}

	public int getReward() {
		return reward;
	}	
		
}
