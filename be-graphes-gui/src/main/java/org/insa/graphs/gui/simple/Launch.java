package org.insa.graphs.gui.simple;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.BinaryPathReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.io.PathReader;

import com.kitfox.svg.pathcmd.Arc;

public class Launch {

    /**
     * Create a new Drawing inside a JFrame an return it.
     *
     * @return The created drawing.
     * @throws Exception if something wrong happens when creating the graph.
     */
    public static Drawing createDrawing() throws Exception {
        BasicDrawing basicDrawing = new BasicDrawing();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("BE Graphes - Launch");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                frame.setSize(new Dimension(800, 600));
                frame.setContentPane(basicDrawing);
                frame.validate();
            }
        });
        return basicDrawing;
    }

    public static void testScenario(Graph graph, boolean time, boolean car, int nbTests) {
        //TODO
        // if() situation time velo ??
        // ArcInspectorFactory arcInspectorFactory = new ArcInspectorFactory();
        // arcInspectorFactory.getAllFilters().get le bon mode ?
        ArcInspector arcInspector = null;

        for (int i = 0; i < nbTests; i++) {    
            // Pick a random origin and destination node each time
            Node randomOrigin = graph.getNodes().get((int) (Math.random() * graph.size()));
            Node randomDestination = graph.getNodes().get((int) (Math.random() * graph.size()));
            
            // Create the data for the shortest path problem
            ShortestPathData data = new ShortestPathData(graph, randomOrigin, randomDestination, arcInspector);

            // Bellman-Ford
            BellmanFordAlgorithm bellman = new BellmanFordAlgorithm(data);
            ShortestPathSolution solutionBellman = bellman.run();
            Path pathBellman = solutionBellman.getPath();

            // Dijkstra
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
            ShortestPathSolution solutionDijkstra = dijkstra.run();
            Path pathDijkstra = solutionDijkstra.getPath();
            
            // Test if the path is valid
            testPathValid(pathBellman);
            testPathValid(pathDijkstra);

            // Test if the cost of the path built by Dijkstra is equal to the cost of the path built by Bellman-Ford
            testPathEquals(solutionDijkstra, solutionBellman);

            //TODO compter le nombre de tests réussis et échoués en fonction du mode
        }
    }

    public static int testPathValid(Path path) {
        // Check if the path is valid
        if (path.isValid()) {
            return 0;
        } else {
            return -1;
        }
    }

    // Check if the cost of the path built by Dijkstra is equal to the cost of the path built by Path
    // b-a >= 0
    public static int testPathEquals(ShortestPathSolution path1, ShortestPathSolution path2) {
        if (path1.getCost() - path2.getCost() >= 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public static void main(String[] args) throws Exception {

        // visit these directory to see the list of available files on commetud.
        final String mapName =
                //"/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
                "C:/Users/mathi/OneDrive/Cours/INSA/S6/Graphes/be-graphes/be-graphes-maps/insa.mapgr";
        final String pathName =
                //"/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Paths/path_fr31insa_rangueil_r2.path";
                "C:/Users/mathi/OneDrive/Cours/INSA/S6/Graphes/be-graphes/be-graphes-maps/path_fr31insa_rangueil_r2.path";

        final Graph graph;
        final Path path;

        // create a graph reader
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(new FileInputStream(mapName))))) {
            // Read the graph
            graph = reader.read();
        }
        
        // create the drawing
        final Drawing drawing = createDrawing();

        // draw the graph on the drawing
        drawing.drawGraph(graph);

        // create a path reader
        try (final PathReader pathReader = new BinaryPathReader(new DataInputStream(
            new BufferedInputStream(new FileInputStream(pathName))))) {

            // read the path
            path = pathReader.readPath(graph);
        }

        // draw the path on the drawing
        drawing.drawPath(path);
    
        // faire plusieurs tests de chemins avec un for en fonction du mode length ou time
        //testScenario(graph, true, false, Mode.LENGTH, 10);
    }


}
