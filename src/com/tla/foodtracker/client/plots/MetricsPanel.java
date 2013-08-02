package com.tla.foodtracker.client.plots;

import java.util.Vector;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.client.shared.Workout;
import com.tla.foodtracker.shared.Measurement;

public class MetricsPanel extends VerticalPanel
{
	private static FlexTable averagesTable; 
	private static FlexTable workoutTable;
	private Label title;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public MetricsPanel()
	{
		String tableWidth = "200px";
		
		title = new Label("Metrics");
		title.setStyleName("sectionTitle");
		
		// initializes table with titles and default values
		averagesTable = new FlexTable();
		averagesTable.setStyleName("subTable");
		averagesTable.setWidth(tableWidth);
		averagesTable.setText(0, 0, "Averages");
		averagesTable.setText(0, 1, "");
		averagesTable.getRowFormatter().addStyleName(0,  "subTableHeader");
						
		int row = 1;		
		for (Measurement msm : Measurement.values())
		{
			averagesTable.setText(row, 0, "Avg. " + msm.toString());
			averagesTable.setText(row, 1, "0");
			averagesTable.getRowFormatter().addStyleName(row, "subTableRow");
			row++;
		}
		
		workoutTable = new FlexTable();
		workoutTable.setStyleName("subTable");
		workoutTable.setWidth(tableWidth);
		workoutTable.setText(0, 0, "Workout Instances");
		workoutTable.setText(0, 1, "");
		workoutTable.getRowFormatter().addStyleName(0,  "subTableHeader");
		row = 1;
		for (Workout wo : Workout.values())
		{
			workoutTable.setText(row, 0, wo.toString());
			workoutTable.setText(row, 1, "0");
			workoutTable.getRowFormatter().addStyleName(row, "subTableRow");
			row++;
		}
		
		this.add(title);
		this.add(averagesTable);
		this.add(workoutTable);
		
	} // end MetricsPanel()
	
	
	/**
	 * Updates the displayed measurement metrics based on the passed in list 
	 * of log entries
	 * @param les
	 */
	public static void updateMetrics(Vector<LogEntry> les)
	{
		double[] msmTotals = new double[Measurement.values().length];
		int[] woTotals = new int[Workout.values().length];
		
		
		// gets each measurement total
		for (LogEntry le : les)
		{
			int index = 0;
			for (Measurement msm : Measurement.values())
				msmTotals[index++] += le.getFoodEntries().calculateTotal(msm);
			
			Workout wo = le.getWorkout();
			woTotals[wo.ordinal()]++;			
		}
		
		// re-calculates averages
		int row = 1;
		for (Measurement msm : Measurement.values())
		{
			averagesTable.setText(row, 1, NumberFormat.getFormat("#.00").format(msmTotals[row - 1]/les.size()));
			row++;
		}
		
		// re-calculates workouts
		row = 1;
		for (Workout wo : Workout.values())
		{
			workoutTable.setText(row,  1,  Integer.toString(woTotals[row - 1]));
			row++;
		}
		
	} // end updateMetrics()

} // end class MetricsPanel
