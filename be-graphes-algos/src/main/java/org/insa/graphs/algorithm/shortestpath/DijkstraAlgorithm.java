package org.insa.graphs.algorithm.shortestpath;

import java.util.List;
import org.insa.graphs.algorithm.AbstractSolution.Status;

import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

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
            // Set the cost to infinity for all nodes
            labels[i] = new Label(graph.get(i), false, Float.POSITIVE_INFINITY, null);
        }
        // set the cost of the node at the origin, to 0
        labels[data.getOrigin().getId()] = new Label(data.getOrigin(), false, 0, null);

        return labels;
    }

    // TODO: implement the Dijkstra algorithm
    @Override
    @Deprecated
    protected ShortestPathSolution doRun() {
        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        // retrieve data from the input problem (getInputData() is inherited from the parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData(); //get the data from DijsktraAlgorithm
        Label[] labels = initLabel(data);
        Graph graph = data.getGraph();

        // Binary heap to store the labels (we will remove the label of minimum cost at each iteration)
        BinaryHeap<Label> labelHeap = new BinaryHeap<Label>();
        labelHeap.insert(labels[data.getOrigin().getId()]);
        
        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(data.getOrigin());
        
        float currentCost = 0;

        // While the heap is not empty/labels not marked (Main loop of the algorithm)
        while(!labelHeap.isEmpty()) {
            Label currentBest = labelHeap.deleteMin(); // removes the label of minimum cost from the heap and returns it
            currentCost += currentBest.getCost(); // get the cost of the current best label
            
            // setmarked to true (to skip it in the next iterations)
            currentBest.setMarked(true);
            labels[currentBest.getCurrentNode().getId()] = currentBest; // update the label of the current node
            notifyNodeMarked(currentBest.getCurrentNode());      
            
            Node currentNode = currentBest.getCurrentNode();
            List<Arc> successors = currentNode.getSuccessors(); // get the successors of the current node
            
            for(int j = 0; j < currentNode.getNumberOfSuccessors()-1; j++){
                Arc currentArc = currentNode.getSuccessors().get(j);
                Label successor = new Label(currentArc.getDestination(), successor.isMarked(), currentArc.getLength()+currentCost, currentArc.getOrigin());
                // if the current node is the destination node, we can stop the algorithm destination reached
                if (currentNode == data.getDestination()) {
                    //TODO: Check
                    notifyDestinationReached(currentNode);
                    Path path = new Path(graph, currentNode);
                    solution = new ShortestPathSolution(data, Status.OPTIMAL, path);
                    return solution;
                }
                //if marked
                if(successor.isMarked()==true){
                    //on fait rien du coup??
                }
                //if newCost < oldCost(remove then insert new)
                if(successor.getCost()<labels[currentBest.getCurrentNode().getCost()]){
                    successor.getCurrentNode.setCost(successor.getCost())
                }
                Arc arc = successors.get(j);
                //Node current, boolean marked, float currentCost, Node fatherNode
                labelHeap.insert(new Label(arc.getDestination(), false, arc.getLength()+currentCost, arc.getOrigin()));
                
            }
        
        }

        // when the algorithm terminates, return the solution that has been found
        return solution;
    }



}
