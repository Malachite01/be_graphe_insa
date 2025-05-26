package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    private int nodeVisited = 0; // Counter for the number of nodes visited

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);    
    }

    /* Function to initialize all the labels
     * @param data the data of the shortest path problem
     * @return an array of labels
    */
    public Label[] initLabel(ShortestPathData data) {
        Graph graph = data.getGraph();
        final int nbNodes = graph.size();
        Label[] labels = new Label[nbNodes];

        
        // Initialize labels for each node in the graph
        // Set the cost to infinity, fathers to null, and marked to false the label of the origin node is set to 0
        for (int i = 0; i < nbNodes; i++) {
            labels[i] = new Label(graph.get(i), false, Float.POSITIVE_INFINITY);
        
        }
        // set the cost of the node at the origin, to 0
        labels[data.getOrigin().getId()] = new Label(data.getOrigin(), false, 0);
        return labels; 
    }

    @Override
    protected ShortestPathSolution doRun() {
        // retrieve data from the input problem (getInputData() is inherited from the parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        final Graph graph = data.getGraph();
        final Node origin = data.getOrigin();
        final Node destination = data.getDestination();

        // Table of labels (initialized to infinity)
        // The label of the origin node is set to 0
        Label[] labels = initLabel(data);
        // Binary heap to store the labels (we will remove the label of minimum cost at each iteration)
        BinaryHeap<Label> heap = new BinaryHeap<>();
        heap.insert(labels[origin.getId()]); // add the label of the origin node to the heap

        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(origin);

        // While the heap is not empty/labels not marked (Main loop of the algorithm)
        while (!heap.isEmpty()) {
            Label current = heap.deleteMin(); // removes the label of minimum cost from the heap and returns it
            Node currentNode = current.getCurrentNode(); // get the cost of the current best label

            // If already marked, ignore
            if (current.isMarked()) continue;

            // Mark the current node
            current.setMarked(true);
            nodeVisited++; // Increment the counter for visited nodes
            notifyNodeMarked(currentNode);


            /* If destination reached, reconstruct the path */
            if (currentNode == destination) {
                notifyDestinationReached(destination);

                // Reconstruct the path from the destination to the origin
                List<Arc> arcs = new ArrayList<>();
                Label label = labels[destination.getId()];
                double totalCost = 0; // Calculate the total cost of the path

                while (label.getFather() != null) {
                    arcs.add(label.getFather());
                    totalCost += label.getCost();
                    label = labels[label.getFather().getOrigin().getId()];
                }
                Collections.reverse(arcs); // Reverse the list to get the path from origin to destination

                // Create the shortest path from the list of arcs
                Path path = new Path(graph, arcs);

                return new ShortestPathSolution(data, Status.OPTIMAL, path, totalCost);
            }

            // if not destination and not marked, explore the outgoing arcs of the current node
            // For each successor of the current node
            for (Arc arc : currentNode.getSuccessors()) {
                // Check if the arc is allowed
                // If not, skip to the next arc
                if (!data.isAllowed(arc)) continue;

                // Get the destination node of the arc
                Node successorNode = arc.getDestination();
                Label successorLabel = labels[successorNode.getId()];

                // If the successor label is already marked, skip to the next arc
                if (successorLabel.isMarked()) continue;

                // Calculate the new cost,//?data is our current path of course
                float newCost = current.getCost() + (float) data.getCost(arc);


                // If the new cost is less than the current cost of the successor label
                // Update the cost and the father of the successor label
                // and insert the successor label into the heap
                //successorLabel.getCOst= current know shortest cost to destinatino node of our arc
                if (newCost < successorLabel.getCost()) {
                    try {
                        heap.remove(successorLabel);
                    } catch (ElementNotFoundException e) {} // successorLabel not in heap

                    // Update by removing the old label and inserting the new one
                    successorLabel.setCost(newCost);
                    successorLabel.setFather(arc);
                    heap.insert(successorLabel);
                    
                    // Notify observers about the new label
                    notifyNodeReached(successorNode);
                }
            }
        }

        // Aucun chemin trouvé
        return new ShortestPathSolution(data, Status.INFEASIBLE);
    }

    public int getNodeVisited() {
        return this.nodeVisited;
    }
}
