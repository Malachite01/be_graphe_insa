package org.insa.graphs.gui.simple;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.random.RandomGenerator;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import java.awt.Color;

import org.jfree.data.category.DefaultCategoryDataset;

public class Launch {
    /**
     * Enum to represent the distance mode for the tests.
    */
    public enum DistanceMode {
        SMALL(0, 5000), //Bellman-Ford vs Dijkstra
        MEDIUM(5000, 100000), //Dijkstra vs A*
        LARGE(100000, Double.MAX_VALUE); //Dijkstra vs A*

        private final double minDistance;
        private final double maxDistance;

        DistanceMode(double minDistance, double maxDistance) {
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
        }

        public double getMinDistance() {
            return minDistance;
        }

        public double getMaxDistance() {
            return maxDistance;
        }

        public static String getDistanceModeName(DistanceMode mode) {
            switch (mode) {
                case SMALL:
                    return "SMALL";
                case MEDIUM:
                    return "MEDIUM";
                case LARGE:
                    return "LARGE";
                default:
                    throw new IllegalArgumentException("Unknown DistanceMode: " + mode);
            }
        }

        public boolean isInRange(double distance) {
            return distance >= minDistance && distance <= maxDistance;
        }

        @Override
        public String toString() {
            return getDistanceModeName(this)+"{minDistance="+this.minDistance+", maxDistance="+this.maxDistance+ "}";
        }
    }

    public static void testManualScenario() throws Exception {
        //TODO: faire des scénarios manuels et un petit avec bellman ford
    }

    /**
     * Test a random scenario with the given filter mode and number of tests.
     * @param filterMode The mode of the filter to use (0: NOFILTER_LENGTH, 1: CARS_LENGTH, 2: CARS_TIME, 3: PEDESTRIAN_TIME).
     * @param nbTests The number of tests to perform.
     */
    // BellmanFord a toujours raison, Dijkstra est plus rapide que BellmanFord, A* est plus rapide que Dijkstra 
    public static void testRandomScenarios(int filterMode, int nbTests, String mapToTest, boolean showStats, DistanceMode distanceMode) throws Exception {
        if(filterMode < 0 || filterMode > 3) throw new IllegalArgumentException("Invalid filter mode. Must be between 0 and 3.");
        final Graph graph;

        Path pathDijkstra;
        Path pathAlgorithm;
        ShortestPathSolution solutionDijkstra;
        ShortestPathSolution solutionAlgorithm;
        String algoToCompareTo = (distanceMode == DistanceMode.SMALL ? "Bellman-Ford" : "A*");

        int successfulTests = 0;
        int failedTests = 0;
        int invalidPaths = 0;

        System.out.println("---SCENARIOS ALEATOIRES---\n");
        System.out.println("Carte utilisée : " + mapToTest +"\n");
        ArcInspector filter = ArcInspectorFactory.getAllFilters().get(filterMode);
        System.out.println("Mode de filtrage : " + filter.toString() + "\n");
        System.out.println("Longueur des chemins: " + distanceMode.toString() + "\n");

        // create a graph reader
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(new FileInputStream(mapToTest))))) {
            // Read the graph
            graph = reader.read();
        }
        // create the drawing
        final Drawing drawing = DrawUtilsLaunch.createDrawing(filter.toString());
        drawing.drawGraph(graph);

        //? Statistics variable
        DefaultCategoryDataset objDatasetSolvingTime = new DefaultCategoryDataset();
        long totalTimeDijkstra = 0;
        long totalTimeAlgorithm = 0;

        DefaultCategoryDataset objDatasetNodesMarked = new DefaultCategoryDataset();
        long totalDijkstraNodesMarked = 0;
        long totalAlgorithmNodesMarked = 0;

        for (int i = 0; i < nbTests; i++) {   
            // Pick a random origin and destination node each time
            int randomOrigin = RandomGenerator.getDefault().nextInt(1, graph.size());
            int randomDestination = RandomGenerator.getDefault().nextInt(1, graph.size());
            Node originNode = graph.get(randomOrigin);
            Node destinationNode = graph.get(randomDestination);

            // Get the distance between the origin and destination nodes in meters
            double distanceInMeters = originNode.getPoint().distanceTo(destinationNode.getPoint());
            
            //! TO AVOID TROUBLES WITH BELLMAN FORD AND BIG DISTANCES
            while (!distanceMode.isInRange(distanceInMeters)) {
                // If the distance is not in the range, pick a new random origin and destination
                randomOrigin = RandomGenerator.getDefault().nextInt(1, graph.size());
                randomDestination = RandomGenerator.getDefault().nextInt(1, graph.size());
                originNode = graph.get(randomOrigin);
                destinationNode = graph.get(randomDestination);
                distanceInMeters = originNode.getPoint().distanceTo(destinationNode.getPoint());
            }

            // Create the shortest path data with the random origin and destination
            ShortestPathData data = new ShortestPathData(graph, originNode, destinationNode, filter);

            //? Dijkstra
            ShortestPathAlgorithm dijkstra = new DijkstraAlgorithm(data);
            solutionDijkstra = dijkstra.run(); // run the algorithm
            pathDijkstra = solutionDijkstra.getPath();
            //* LineChart - Solving Time 
            long solvingTimeDijkstraMillis = solutionDijkstra.getSolvingTime().toMillis(); // convert Duration to milliseconds
            objDatasetSolvingTime.setValue(solvingTimeDijkstraMillis, "Dijkstra", ""+(i + 1));
            //* BarChart - Average Time and Nodes
            totalTimeDijkstra += solvingTimeDijkstraMillis; //? avg time
            totalDijkstraNodesMarked += solutionDijkstra.getNodeVisited(); //? avg nodes
            objDatasetNodesMarked.setValue(solutionDijkstra.getNodeVisited(), "Dijkstra", ""+(i + 1));

            //!Dijkstra vs ?algo?
            //? Check if we use Bellman-Ford or A* based on the distance mode (SMALL for Bellman-Ford, MEDIUM and LARGE for A*)
            ShortestPathAlgorithm algorithm = (distanceMode == DistanceMode.SMALL ? new BellmanFordAlgorithm(data) : new AStarAlgorithm(data));
            solutionAlgorithm = algorithm.run(); // run the algorithm
            pathAlgorithm = solutionAlgorithm.getPath();
            //* LineChart - Solving Time
            long solvingTimeAlgorithmMillis = solutionAlgorithm.getSolvingTime().toMillis(); // convert Duration to milliseconds
            objDatasetSolvingTime.setValue(solvingTimeAlgorithmMillis, algoToCompareTo, ""+(i + 1));
            //* BarChart - Average Time and Nodes
            totalTimeAlgorithm += solvingTimeAlgorithmMillis; //? avgtime
            totalAlgorithmNodesMarked += solutionAlgorithm.getNodeVisited(); //? avg nodes
            objDatasetNodesMarked.setValue(solutionAlgorithm.getNodeVisited(), algoToCompareTo, ""+(i + 1));

            //? Test if the path is valid
            if (!testPathValid(pathDijkstra) || !testPathValid(pathAlgorithm)) {
                System.out.println("Test échoué : Chemin invalide (impossible de dessiner)\n");
                invalidPaths++;
                continue; // Skip to the next iteration if the path is invalid
            }

            //? Compare Dijkstra and Bellman-Ford or A*
            boolean costDijkstraVsAlgorithm = compareCosts(solutionDijkstra, solutionAlgorithm);
            boolean arcsDijkstraVsAlgorithm = compareArcs(pathDijkstra, pathAlgorithm);

            // If all comparisons are successful, the test is successful
            // i.e Same costs and same arcs
            if (costDijkstraVsAlgorithm && arcsDijkstraVsAlgorithm) {
                // Valid test, draw the path (all paths are the same so doesn't matter which one we draw)
                drawing.drawPath(pathAlgorithm, Color.GREEN);
                successfulTests++;
                System.out.println("Test " + (i + 1) + " réussi\n");
            } else {
                // Invalid test, draw the paths in red, orange
                drawing.drawPath(pathDijkstra, Color.RED);
                drawing.drawPath(pathAlgorithm, Color.ORANGE);
                // Increment failed tests and print the reason
                failedTests++;
                System.out.println("Test " + (i + 1) + " échoué : Comparaison échouée");
                if (!costDijkstraVsAlgorithm)
                    System.out.println("  - Coûts différents entre Dijkstra et "+algoToCompareTo+"\n");
                if (!arcsDijkstraVsAlgorithm)
                    System.out.println("  - Chemins différents entre Dijkstra et "+algoToCompareTo+"\n");
            }

        }
        
        //? Show the charts with the statistics collected
        if (showStats) {
            DrawUtilsLaunch.showAllCharts(
                objDatasetSolvingTime, 
                totalTimeDijkstra / (double)successfulTests, 
                totalTimeAlgorithm / (double)successfulTests,
                objDatasetNodesMarked,
                totalDijkstraNodesMarked / (double)successfulTests,
                totalAlgorithmNodesMarked / (double)successfulTests,
                filter.toString(),
                distanceMode
            );
        }

        //? Summary of the tests
        System.out.println("---FIN DES SCENARIOS ALEATOIRES---\n");
        System.out.println("Test réussi = les couts et arcs des chemins Dijkstra et"+ algoToCompareTo +" sont égaux. \n");
        System.out.println("REUSSIS : " + successfulTests + "/" + nbTests + "\n");
        System.out.println("Test échoué = les couts et/ou arcs des chemins Dijkstra et"+ algoToCompareTo +" sont différents ou chemin invalide. \n");
        System.out.println("ECHOUES : " + failedTests + "/" + nbTests + "\n");
        System.out.println("INVALIDES : " + invalidPaths + "/" + nbTests + "\n");
        if (failedTests + invalidPaths + successfulTests != nbTests) System.out.println("Oupsi, le nombre de tests réussis et échoués ne correspond pas au nombre de tests effectués...\n");
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
        final String mapToTestForBellman = "C:\\Users\\mathi\\OneDrive\\Cours\\INSA\\S6\\I3MIIL11 - Graphes\\be-graphes\\be-graphes-maps\\insa.mapgr";
        // faire plusieurs tests de chemins aléatoires en fonction du mode choisi (0: NOFILTER_LENGTH, 1: CARS_LENGTH, 2: CARS_TIME, 3: PEDESTRIAN_TIME)
        
        // SMALL = bellman ford vs Dijkstra 0 to 5000m
        // MEDIUM = Dijkstra vs A* 5000 to 100000m
        // LARGE = Dijkstra vs A* 100000m to infinity
        DistanceMode distanceMode = DistanceMode.SMALL;
        // Cars length
        testRandomScenarios(1, 50, (distanceMode==DistanceMode.SMALL?mapToTestForBellman:mapToTest), true, distanceMode);
        // Cars time
        testRandomScenarios(2, 50, (distanceMode==DistanceMode.SMALL?mapToTestForBellman:mapToTest), true, distanceMode);

        testManualScenario();
    }
}
