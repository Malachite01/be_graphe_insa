package org.insa.graphs.gui.simple;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.gui.simple.Launch.DistanceMode;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import javax.swing.JPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


class DrawUtilsLaunch {
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
    DefaultCategoryDataset nodesDataset, double avgDijkstraNodes, double avgAStarNodes, String mode, DistanceMode distanceMode) {
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
            avgTimeDataset.setValue(avgAStarTime, "Algorithme", (distanceMode==DistanceMode.SMALL?"Bellman":"A*"));

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
            avgNodesDataset.setValue(avgAStarNodes, "Algorithme", (distanceMode==DistanceMode.SMALL?"Bellman":"A*"));

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
}