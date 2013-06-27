package com.tla.foodtracker.client.plots;

import java.util.Vector;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.shared.Measurement;

public class MetricsPanel extends VerticalPanel
{
	private static FlexTable table; 
	private Label title;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public MetricsPanel()
	{
		title = new Label("Metrics");
		title.setStyleName("sectionTitle");
		
		// initializes table with titles and default values
		table = new FlexTable();
		table.setStyleName("subTable");
		
		table.setText(0, 0, "Averages");
		table.setText(0, 1, "");
		table.getRowFormatter().addStyleName(0,  "subTableHeader");
				
		int row = 1;		
		for (Measurement msm : Measurement.values())
		{
			table.setText(row, 0, "Avg. " + msm.toString());
			table.setText(row, 1, "0");
			table.getRowFormatter().addStyleName(row, "subTableRow");
			row++;
		}
		
		this.add(title);
		this.add(table);
		
	} // end MetricsPanel()
	
	
	/**
	 * Updates the displayed measurement metrics based on the passed in list 
	 * of log entries
	 * @param les
	 */
	public static void updateMetrics(Vector<LogEntry> les)
	{
		double[] totals = new double[Measurement.values().length];
		
		// gets each measurement total
		for (LogEntry le : les)
		{
			int index = 0;
			for (Measurement msm : Measurement.values())
				totals[index++] += le.getFoodEntries().calculateTotal(msm);
		}
		
		// re-calculates averages
		int row = 1;
		for (Measurement msm : Measurement.values())
		{
			table.setText(row, 1, Double.toString(totals[row - 1]/les.size()));
		}
		
	} // end updateMetrics()

} // end class MetricsPanel
