package org.insa.graphs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Main graph class.
 * </p>
 * <p>
 * This class acts as a object-oriented <b>adjacency list</b> for a graph, i.e., it
 * holds a list of nodes and each node holds a list of its successors.
 * </p>
 */
public final class Graph {

    // Map identifier.
    private final String mapId;

    // Map name
    private final String mapName;

    // Nodes of the graph.
    private final List<Node> nodes;

    // Graph information of this graph.
    private final GraphStatistics graphStatistics;

    /**
     * Create a new graph with the given ID, name, nodes and information.
     *
     * @param mapId ID of the map corresponding to this graph.
     * @param mapName Name of the map corresponding to this graph.
     * @param nodes List of nodes for this graph.
     * @param graphStatistics Information for this graph.
     */
    public Graph(String mapId, String mapName, List<Node> nodes,
            GraphStatistics graphStatistics) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.nodes = Collections.unmodifiableList(nodes);// normal node but but unmodifiable
        this.graphStatistics = graphStatistics;
    }

    /**
     * @return The GraphStatistics instance associated with this graph.
     */
    public GraphStatistics getGraphInformation() {
        return this.graphStatistics;
    }

    /**
     * Fetch the node with the given ID. Complexity: O(1).
     *
     * @param id ID of the node to fetch.
     * @return Node with the given ID.
     */
    public Node get(int id) {
        return this.nodes.get(id);
    }

    /**
     * @return Number of nodes in this graph.
     */
    public int size() {
        return this.nodes.size();
    }

    /**
     * @return List of nodes in this graph (unmodifiable).
     * @see Collections#unmodifiableList(List)
     */
    public List<Node> getNodes() {
        return this.nodes;
    }

    /**
     * @return ID of the map associated with this graph.
     */
    public String getMapId() {
        return mapId;
    }

    /**
     * @return Name of the map associated with this graph.
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return Transpose graph of this graph. (i.e., reverse the direction of all arcs) i.e transposition of the matrix describing the graph
     */
    public Graph transpose() {
        final ArrayList<Node> trNodes = new ArrayList<>(nodes.size());
        for (Node node : nodes) { // Create a list of trNodes which is a copy of nodes 
            trNodes.add(new Node(node.getId(), node.getPoint()));// Node(int id, Point point) with point being the location of the node 
        }
        for (Node node : nodes) { // Iterate over the nodes of the original graph
            final Node orig = trNodes.get(node.getId()); // Get the actual node from the transposed graph
            for (Arc arc : node.getSuccessors()) { // Iterate over the successors of the node
                if (arc.getRoadInformation().isOneWay()) { // if our arc is one way (i.e., not a two way road) 
                    final Node dest = trNodes.get(arc.getDestination().getId());
                    // Create a new ArcBackward with the original node as the origin and the destination as the destination
                    dest.addSuccessor(
                            new ArcBackward(new ArcForward(orig, dest, arc.getLength(),
                                    arc.getRoadInformation(), arc.getPoints())));
                } 
                else if (arc instanceof ArcForward) { // if our arc is not one way 
                    final Node dest = trNodes.get(arc.getDestination().getId());
                    Arc newArc = new ArcForward(orig, dest, arc.getLength(),
                            arc.getRoadInformation(), arc.getPoints());
                    dest.addSuccessor(new ArcBackward(newArc));
                    orig.addSuccessor(newArc);
                }
            }
        }
        return new Graph("R/" + mapId, mapName, trNodes, graphStatistics);
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, name=%s, #nodes=%d]",
                getClass().getCanonicalName(), getMapId(), getMapName(), size());
    }

}
