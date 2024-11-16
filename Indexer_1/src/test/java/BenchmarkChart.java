import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class BenchmarkChart {

	public static void main(String[] args) {
		String[] labels = {"10 books", "50 books", "100 books", "200 books"};
		int[] v1Times = {135, 305, 460, 958};
		int[] v2Times = {30, 117, 247, 503};

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < labels.length; i++) {
			dataset.addValue(v1Times[i], "Indexer V1", labels[i]);
			dataset.addValue(v2Times[i], "Indexer V2", labels[i]);
		}

		JFreeChart chart = ChartFactory.createLineChart(
				"Comparation of Execution Time",
				"Number of Books",
				"Execution Time(ms)",
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
		);


		chart.setBackgroundPaint(Color.white);

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Benchmark Results");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(new ChartPanel(chart), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}

