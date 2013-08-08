package com.tla.foodtracker.client.plots;

import java.util.Vector;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.client.shared.Workout;
import com.tla.foodtracker.shared.Measurement;

public class MetricsPanel extends VerticalPanel
{
	private static FlexTable averagesTable; 
	private static FlexTable workoutTable;
	private static MeasurementPieChart msmPieChart = new MeasurementPieChart();
	private Label title;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public MetricsPanel()
	{
		String tableWidth = "270px";
		
		title = new Label("Metrics");
		title.setStyleName("sectionTitle");
		
		// initializes table with titles and default values
		averagesTable = new FlexTable();
		averagesTable.setStyleName("subTable");
		averagesTable.setWidth(tableWidth);
		averagesTable.setText(0, 0, "Measurement Averages");
		averagesTable.setText(0, 1, "");
		averagesTable.setText(0, 2, "");
		averagesTable.setText(1, 0, "Measurement");
		averagesTable.setText(1, 1, "Average");
		averagesTable.setText(1, 2, "Goal");
		averagesTable.getRowFormatter().addStyleName(0,  "subTableHeader");
		averagesTable.getRowFormatter().addStyleName(1,  "subTableHeader2");
						
		int row = 2;		
		for (Measurement msm : Measurement.values())
		{
			averagesTable.setText(row, 0, msm.toString());
			averagesTable.setText(row, 1, "0");
			averagesTable.setText(row, 2, "0");
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
			if (wo == Workout.NONE)
			{
				row++;
				continue;
			}
			workoutTable.setText(row, 0, wo.toString());
			workoutTable.setText(row, 1, "0");
			workoutTable.getRowFormatter().addStyleName(row, "subTableRow");
			row++;
		}
		
		this.add(title);
		this.add(averagesTable);
		this.add(workoutTable);
//		this.add(msmPieChart);
		
	} // end MetricsPanel()
	
	
	/**
	 * Updates the displayed measurement metrics based on the passed in list 
	 * of log entries
	 * @param les
	 */
	public static void updateMetrics(Vector<LogEntry> les, Goals goals)
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
		int row = 2;
		for (Measurement msm : Measurement.values())
		{
			averagesTable.setText(row, 1, NumberFormat.getFormat("#.00").format(msmTotals[row - 2]/les.size()));
			averagesTable.setText(row, 2, Double.toString(goals.getMeasurement(msm)));
			row++;
		}
		
		// updates macro pie chart
//		msmPieChart.plot(msmTotals[0]/les.size(), msmTotals[1]/les.size(), msmTotals[2]/les.size(), msmTotals[3]/les.size());
		
		// re-calculates workouts
		row = 1;
		for (Workout wo : Workout.values())
		{
			if (wo == Workout.NONE)
			{
				row++;
				continue;
			}
			workoutTable.setText(row,  1,  Integer.toString(woTotals[row - 1]));
			row++;
		}
		
		
	} // end updateMetrics()

} // end class MetricsPanel
