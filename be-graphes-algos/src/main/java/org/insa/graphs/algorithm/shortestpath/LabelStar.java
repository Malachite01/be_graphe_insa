package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Point;
import org.insa.graphs.algorithm.AbstractInputData.Mode;


public class LabelStar extends Label implements Comparable<Label>{
    private Float destinationCost;

    // Constructor
    public LabelStar(Node current, Boolean marked, Float currentCost, float destinationCost) {
        super(current, marked, currentCost);
        this.destinationCost=destinationCost;
        }

   /* public float getheuristicGoalCost(ArcInspector inspector){
        if(inspector.getMode()==Mode.TIME){
            return (float) (Point.distance(this.getCurrentNode().getPoint(), destinationNode.getPoint()) / 3.6);

        }else if(inspector.getMode()==Mode.LENGTH){
            return (float) Point.distance(this.getCurrentNode().getPoint(), destinationNode.getPoint());
        }
        return  0;
    }
    */
    public float getTotalCost(){
        return this.getCost() + this.destinationCost; // Pass appropriate ArcInspector if needed
    }
}