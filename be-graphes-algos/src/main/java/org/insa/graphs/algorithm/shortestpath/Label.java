package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Label implements Comparable<Label> {
    private Node currentNode;
    private float currentCost;
    private Node fatherNode;
    private boolean marked;

    public Label(Node current, boolean marked, float currentCost, Node fatherNode) {
        this.currentNode = current;
        this.currentCost = currentCost;
        this.fatherNode = fatherNode;
        this.marked = marked;
    }

    // label
    // Cost combien coute le chemin depuis le start
    // Father le der sommet d'ou on vient
    // Mark pr savoir si on a traité
    public Node getCurrentNode() {
        return currentNode;
    }

    public Node getFatherNode() {
        return fatherNode;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked){
        this.marked=marked;
    }

    public float getCost() {
        return currentCost;
    }

    @Override
    public int compareTo(Label other) {
        // Compare based on the cost (or any other criteria)
        return Double.compare(this.getCost(), other.getCost());
    }

}
