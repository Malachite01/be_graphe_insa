package org.insa.graphs.gui.simple;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.random.RandomGenerator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import java.awt.Color;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.JPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Launch {
    /**
     * Show all charts together in a JFrame.
     * @param solvingTimeDataset Dataset for the solving time line chart.
     * @param avgDijkstraTime Average solving time for Dijkstra algorithm.
     * @param avgAStarTime Average solving time for A* algorithm.
     * @param nodesDataset Dataset for the nodes visited line chart.
     * @param avgDijkstraNodes Average number of nodes visited for Dijkstra algorithm.
     * @param avgAStarNodes Average number of nodes visited for A* algorithm.
     * @param mode The mode of the test (NOFILTER_LENGTH, CARS_LENGTH, CARS_TIME, PEDESTRIAN_TIME)
     */
    public static void showAllCharts(DefaultCategoryDataset solvingTimeDataset, double avgDijkstraTime, double avgAStarTime, 
    DefaultCategoryDataset nodesDataset, double avgDijkstraNodes, double avgAStarNodes, String mode) {
        SwingUtilities.invokeLater(() -> {
            //? Line Chart - Solving Time
            JFreeChart timeLineChart = ChartFactory.createLineChart(
                    "Comparaison des temps de résolution",
                    "Test",
                    "Temps (ms)",
                    solvingTimeDataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );

            //? Bar Chart - Average Time
            DefaultCategoryDataset avgTimeDataset = new DefaultCategoryDataset();
            avgTimeDataset.setValue(avgDijkstraTime, "Algorithme", "Dijkstra");
            avgTimeDataset.setValue(avgAStarTime, "Algorithme", "A*");

            JFreeChart avgTimeChart = ChartFactory.createBarChart(
                    "Temps moyen d'exécution",
                    "Algorithme",
                    "Temps (ms)",
                    avgTimeDataset,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );

            //? Line Chart - Nodes visited
            JFreeChart nodesLineChart = ChartFactory.createLineChart(
                    "Comparaison du nombre de noeuds marqués",
                    "Test",
                    "Noeuds marqués",
                    nodesDataset,
                    PlotOrientation.VERTICAL,
                    true, true, false
            );

            //? Bar Chart - Average Nodes
            DefaultCategoryDataset avgNodesDataset = new DefaultCategoryDataset();
            avgNodesDataset.setValue(avgDijkstraNodes, "Algorithme", "Dijkstra");
            avgNodesDataset.setValue(avgAStarNodes, "Algorithme", "A*");

            JFreeChart avgNodesChart = ChartFactory.createBarChart(
                    "Nombre moyen de noeuds marqués",
                    "Algorithme",
                    "Noeuds",
                    avgNodesDataset,
                    PlotOrientation.VERTICAL,
                    false, true, false
            );

            //? Pannels
            JPanel panel = new JPanel(new java.awt.GridLayout(2, 2));
            panel.add(new ChartPanel(timeLineChart));
            panel.add(new ChartPanel(avgTimeChart));
            panel.add(new ChartPanel(nodesLineChart));
            panel.add(new ChartPanel(avgNodesChart));
            //? Frame
            JFrame frame = new JFrame(mode);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }


    /**
     * Create a new Drawing inside a JFrame an return it.
     *
     * @return The created drawing.
     * @throws Exception if something wrong happens when creating the graph.
     */
    public static Drawing createDrawing(String mode) throws Exception {
        BasicDrawing basicDrawing = new BasicDrawing();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("BE Graphes - Launch : " + mode);
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

    public static void testManualScenarios() throws Exception {
        //TODO: faire des scénarios manuels et un petit avec bellman ford
    }

    /**
     * Test a random scenario with the given filter mode and number of tests.
     * @param filterMode The mode of the filter to use (0: NOFILTER_LENGTH, 1: CARS_LENGTH, 2: CARS_TIME, 3: PEDESTRIAN_TIME).
     * @param nbTests The number of tests to perform.
     */
    // BellmanFord a toujours raison, Dijkstra est plus rapide que BellmanFord, A* est plus rapide que Dijkstra 
    public static void testRandomScenarios(int filterMode, int nbTests, String mapToTest, boolean showStats) throws Exception {
        if(filterMode < 0 || filterMode > 3) throw new IllegalArgumentException("Invalid filter mode. Must be between 0 and 3.");;
        final Graph graph;

        Path pathDijkstra;
        Path pathAStar;
        ShortestPathSolution solutionDijkstra;
        ShortestPathSolution solutionAStar;

        int successfulTests = 0;
        int failedTests = 0;

        System.out.println("---SCENARIOS ALEATOIRES---\n");
        System.out.println("Carte utilisée : " + mapToTest +"\n");
        ArcInspector filter = ArcInspectorFactory.getAllFilters().get(filterMode);
        System.out.println("Mode de filtrage : " + filter.toString() + "\n");

        // create a graph reader
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(new FileInputStream(mapToTest))))) {
            // Read the graph
            graph = reader.read();
        }
        // create the drawing
        final Drawing drawing = createDrawing(filter.toString());
        drawing.drawGraph(graph);

        // Statistics variable
        DefaultCategoryDataset objDatasetSolvingTime = new DefaultCategoryDataset();
        long totalTimeDijkstra = 0;
        long totalTimeAStar = 0;

        DefaultCategoryDataset objDatasetNodesMarked = new DefaultCategoryDataset();
        long totalDijkstraNodesMarked = 0;
        long totalAStarNodesMarked = 0;

        for (int i = 0; i < nbTests; i++) {   
            // Pick a random origin and destination node each time
            int randomOrigin = RandomGenerator.getDefault().nextInt(1, graph.size());
            int randomDestination = RandomGenerator.getDefault().nextInt(1, graph.size());
            // Create the shortest path data with the random origin and destination
            ShortestPathData data = new ShortestPathData(graph, graph.get(randomOrigin), graph.get(randomDestination), filter);

            //? Dijkstra
            ShortestPathAlgorithm dijkstra = new DijkstraAlgorithm(data);
            solutionDijkstra = dijkstra.run(); // run the algorithm
            pathDijkstra = solutionDijkstra.getPath();
            // Statistics solving time for Dijkstra algorithm
            long solvingTimeDijkstraMillis = solutionDijkstra.getSolvingTime().toMillis(); // convert Duration to milliseconds
            totalTimeDijkstra += solvingTimeDijkstraMillis; //? time
            objDatasetSolvingTime.setValue(solvingTimeDijkstraMillis, "Dijkstra", ""+(i + 1));
            totalDijkstraNodesMarked += solutionDijkstra.getNodeVisited(); //? nodes
            objDatasetNodesMarked.setValue(solutionDijkstra.getNodeVisited(), "Dijkstra", ""+(i + 1));

            //? A*
            ShortestPathAlgorithm astar = new AStarAlgorithm(data);
            solutionAStar = astar.run(); // run the algorithm
            pathAStar = solutionAStar.getPath();
            // Statistics solving time for A* algorithm
            long solvingTimeAStarMillis = solutionAStar.getSolvingTime().toMillis(); // convert Duration to milliseconds
            totalTimeAStar += solvingTimeAStarMillis; //? time
            objDatasetSolvingTime.setValue(solvingTimeAStarMillis, "A*", ""+(i + 1));
            totalAStarNodesMarked += solutionAStar.getNodeVisited(); //? nodes
            objDatasetNodesMarked.setValue(solutionAStar.getNodeVisited(), "A*", ""+(i + 1));

            //? Test if the path is valid
            if (!testPathValid(pathDijkstra) || !testPathValid(pathAStar)) {
                System.out.println("Test échoué : Chemin invalide (impossible de dessiner)\n");
                failedTests++;
                continue; // Skip to the next iteration if the path is invalid
            }

            //? Compare Dijkstra and A*
            boolean costDijkstraVsAStar = compareCosts(solutionDijkstra, solutionAStar);
            boolean arcsDijkstraVsAStar = compareArcs(pathDijkstra, pathAStar);

            // If all comparisons are successful, the test is successful
            // i.e Same costs and same arcs for Bellman-Ford vs Dijkstra, and Dijkstra vs A*
            if (costDijkstraVsAStar && arcsDijkstraVsAStar) {
                // Valid test, draw the path (all paths are the same so doesn't matter which one we draw)
                drawing.drawPath(pathAStar, Color.GREEN);
                successfulTests++;
                System.out.println("Test " + (i + 1) + " réussi\n");
            } else {
                // Invalid test, draw the paths in red, light red, lighter red
                drawing.drawPath(pathAStar, new Color(255, 0, 0));
                drawing.drawPath(pathDijkstra, new Color(255, 77, 77));
                failedTests++;
                System.out.println("Test " + (i + 1) + " échoué : Comparaison échouée");
                if (!costDijkstraVsAStar)
                    System.out.println("  - Coûts différents entre Dijkstra et A*\n");
                if (!arcsDijkstraVsAStar)
                    System.out.println("  - Chemins différents entre Dijkstra et A*\n");
            }

        }
        
        //? Show the charts with the statistics collected
        if (showStats) {
            showAllCharts(
                objDatasetSolvingTime, 
                totalTimeDijkstra / (double)successfulTests, 
                totalTimeAStar / (double)successfulTests,
                objDatasetNodesMarked,
                totalDijkstraNodesMarked / (double)successfulTests,
                totalAStarNodesMarked / (double)successfulTests,
                filter.toString()
            );
        }

        //? Summary of the tests
        System.out.println("---FIN DES SCENARIOS ALEATOIRES---\n");
        System.out.println("Test réussi = les couts et arcs des chemins Dijkstra et A* sont égaux (l'heuristique de A* est vérifiée). \n");
        System.out.println("REUSSIS : " + successfulTests + "/" + nbTests + "\n");
        System.out.println("Test échoué = les couts et/ou arcs des chemins Dijkstra et A* sont différents ou chemin invalide. \n");
        System.out.println("ECHOUES : " + failedTests + "/" + nbTests + "\n");
        if (failedTests+ successfulTests != nbTests) System.out.println("Oupsi, le nombre de tests réussis et échoués ne correspond pas au nombre de tests effectués...\n");
    }

    /**
     * Check if the given path is valid.
     * @param path The path to check.
     * @return true if the path is valid, false otherwise.
     **/
    public static boolean testPathValid(Path path) {
        // Check if the path is valid
        if (path == null || !path.isValid()) return false; 
        return true;
    }

    /**
     * Compare the costs of two shortest path solutions.
     * @param path1 The first shortest path solution.
     * @param path2 The second shortest path solution.
     * @return true if the cost of path1 is greater than or equal to the cost of path2, false otherwise.
     */
    public static boolean compareCosts(ShortestPathSolution path1, ShortestPathSolution path2) {
        if (path1.getCost() - path2.getCost() >= 0) return true; 
        return false;
    }

    /**
     * Compare the arcs of two paths.
     * @param path1 The first path.
     * @param path2 The second path.
     * @return true if the arcs of path1 are equal to the arcs of path2, false otherwise.
     * */
    public static boolean compareArcs(Path path1, Path path2) {
        if(path1 != null && path2 != null) {
            return path1.getArcs().equals(path2.getArcs());
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        final String mapToTest = "C:\\Users\\mathi\\OneDrive\\Cours\\INSA\\S6\\I3MIIL11 - Graphes\\be-graphes\\be-graphes-maps\\haute-garonne.mapgr";
        // faire plusieurs tests de chemins aléatoires en fonction du mode choisi (0: NOFILTER_LENGTH, 1: CARS_LENGTH, 2: CARS_TIME, 3: PEDESTRIAN_TIME)
        // Cars length
        testRandomScenarios(1, 50, mapToTest, true);
        // Cars time
        testRandomScenarios(2, 50, mapToTest, true);

        testManualScenarios();
    }
}
