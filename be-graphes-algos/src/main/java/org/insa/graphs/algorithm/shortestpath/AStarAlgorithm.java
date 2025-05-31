package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
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
        float maxSpeed = (graph.getGraphInformation().getMaximumSpeed())/3.6f; // Retrieve maximum speed from graph information in m/s

        // Initialize labels for each node in the graph
        // Set the cost to infinity, fathers to null, and marked to false the label of the origin node is set to 0
        for (int i = 0; i < nbNodes; i++) {
            float Distanceheuristic = (float) Point.distance(graph.get(i).getPoint(), destinationNode.getPoint());// IN METERS! 
            if(mode==Mode.LENGTH){
            // length mode: the heuristic is : the straightforward distance between the current node and the destination node units: meters
            labels[i] = new LabelStar(graph.get(i), false, Float.POSITIVE_INFINITY,Distanceheuristic);
            }else if (mode==Mode.TIME){
            //time mode: the heuristic is: the straightforward distance between the current node and the destination node, divided by the max possible speed on the graph units:seconds
                labels[i] = new LabelStar(graph.get(i), false, Float.POSITIVE_INFINITY, Distanceheuristic /maxSpeed);

            }
        }
        // set the cost of the node at the origin, to 0
        if(mode==Mode.LENGTH){
        labels[data.getOrigin().getId()] = new LabelStar(data.getOrigin(), false, (float)0,(float) Point.distance(originNode.getPoint(), destinationNode.getPoint()));
            }else if (mode==Mode.TIME){
        labels[data.getOrigin().getId()] = new LabelStar(data.getOrigin(), false, (float)0,(float) (Point.distance(originNode.getPoint(), destinationNode.getPoint()))/maxSpeed);

            }
        return labels;
    }
}
