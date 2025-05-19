package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
     /* Function to initialize all the labels
     * @param data the data of the shortest path problem
     * @return an array of labels
    */
    public LabelStar[] initLabel(ShortestPathData data) {
        Graph graph = data.getGraph();
        Node destinationNode=data.getDestination();
        Node originNode=data.getOrigin();
        final int nbNodes = graph.size();
        LabelStar[] labels = new LabelStar[nbNodes];
        Mode mode= data.getMode();
        // Initialize labels for each node in the graph
        // Set the cost to infinity, fathers to null, and marked to false the label of the origin node is set to 0
        for (int i = 0; i < nbNodes; i++) {
            if(mode==Mode.LENGTH){
            labels[i] = new LabelStar(graph.get(i), false, Float.POSITIVE_INFINITY,(float) Point.distance(graph.get(i).getPoint(), destinationNode.getPoint()));
            }else if (mode==Mode.TIME){
                labels[i] = new LabelStar(graph.get(i), false, Float.POSITIVE_INFINITY,(float) Point.distance(graph.get(i).getPoint(), destinationNode.getPoint()));

            }
        }
        // set the cost of the node at the origin, to 0
        if(mode==Mode.LENGTH){
        labels[data.getOrigin().getId()] = new LabelStar(data.getOrigin(), false, (float)0,(float) Point.distance(originNode.getPoint(), destinationNode.getPoint()));
            }else if (mode==Mode.TIME){
        labels[data.getOrigin().getId()] = new LabelStar(data.getOrigin(), false, (float)0,(float) Point.distance(originNode.getPoint(), destinationNode.getPoint()));

            }
        return labels;
    }
}
