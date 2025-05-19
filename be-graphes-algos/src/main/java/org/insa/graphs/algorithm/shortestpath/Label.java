package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;
import org.insa.graphs.model.Arc;

public class Label implements Comparable<Label> {
    private Node currentNode;
    private float currentCost;
    private Arc father;
    private boolean marked;

    public Label(Node current, boolean marked, float currentCost) {
        this.currentNode = current;
        this.currentCost = currentCost;
        this.marked = marked;
        this.father = null;
    }

    // label
    // Cost combien coute le chemin depuis le start
    // Father le der sommet d'ou on vient
    // Mark pr savoir si on a traité
    public Node getCurrentNode() {
        return this.currentNode;
    }

    public Arc getFather() {
        return this.father;
    }

    public void setFather(Arc father) {
        this.father = father;
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void setMarked(boolean marked){
        this.marked = marked;
    }

    public float getCost() {
        return this.currentCost;
    }
     public float getTotalCost() {
        return this.currentCost;
    }

    public void setCost(float cost) {
        this.currentCost = cost;
    }

    @Override
    public int compareTo(Label other) {
        // Compare based on the cost (or any other criteria)
        return Double.compare(this.getCost(), other.getCost());
    }

}
