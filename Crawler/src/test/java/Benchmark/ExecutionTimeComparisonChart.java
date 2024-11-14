package Benchmark;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;

public class ExecutionTimeComparisonChart extends JFrame {

	public ExecutionTimeComparisonChart(String title) {
		super(title);

		// Create the line chart with both datasets
		JFreeChart lineChart = ChartFactory.createLineChart(
				"Crawler Execution Time Comparison: Java vs Python",
				"Number of Books",
				"Execution Time (ms)",
				createDataset(),
				PlotOrientation.VERTICAL,
				true, true, false);

		// Set up the chart panel
		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		setContentPane(chartPanel);
	}

	private CategoryDataset createDataset() {
		// Create a dataset for the comparison chart
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Python execution times (in milliseconds)
		dataset.addValue(5344.1, "Execution Time (Python)", "1");
		dataset.addValue(28154.7, "Execution Time (Python)", "5");
		dataset.addValue(63512.5, "Execution Time (Python)", "10");
		dataset.addValue(126520.6, "Execution Time (Python)", "20");
		dataset.addValue(293559.1, "Execution Time (Python)", "50");

		// Java execution times (in milliseconds)
		dataset.addValue(10760.431, "Execution Time (Java)", "5");
		dataset.addValue(23173.630, "Execution Time (Java)", "10");
		dataset.addValue(54955.294, "Execution Time (Java)", "20");
		dataset.addValue(114320.843, "Execution Time (Java)", "50");

		return dataset;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ExecutionTimeComparisonChart chart = new ExecutionTimeComparisonChart("Crawler Execution Time Comparison");
			chart.setSize(800, 600);
			chart.setLocationRelativeTo(null); // Center the frame on the screen
			chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			chart.setVisible(true);
		});
	}
}