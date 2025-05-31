package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;


public class LabelStar extends Label implements Comparable<Label>{
    private Float destinationCost;

    // Constructor
    public LabelStar(Node current, Boolean marked, Float currentCost, float destinationCost) {
        super(current, marked, currentCost);
        this.destinationCost=destinationCost;
        }
        
    public float getTotalCost(){
        return this.getCost() + this.destinationCost; // Pass appropriate ArcInspector if needed
                                //destinationCost = heuristic
    }
}