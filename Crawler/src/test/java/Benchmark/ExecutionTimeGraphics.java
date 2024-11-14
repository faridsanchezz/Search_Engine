package Benchmark;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;

public class ExecutionTimeGraphics extends JFrame {

	public ExecutionTimeGraphics(String title) {
		super(title);
		JFreeChart lineChart = ChartFactory.createLineChart(
				"Execution Time Of Crawler In Java",
				"Number Of Books",
				"ExecutionTime (ms)",
				createDataset(),
				PlotOrientation.VERTICAL,
				true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new Dimension(800, 600));
		setContentPane(chartPanel);
	}

	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(10760.431, "Execution Time", "5");
		dataset.addValue(23173.630, "Execution Time", "10");
		dataset.addValue(54955.294, "Execution Time", "20");
		dataset.addValue(114320.843, "Execution Time", "50");
		return dataset;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ExecutionTimeGraphics chart = new ExecutionTimeGraphics("Results Of Crawler's Benchmark");
			chart.setSize(800, 600);
			chart.setLocationRelativeTo(null);
			chart.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			chart.setVisible(true);
		});
	}
}
