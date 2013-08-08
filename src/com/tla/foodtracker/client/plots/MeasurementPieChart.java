package com.tla.foodtracker.client.plots;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Series;
import com.google.gwt.visualization.client.visualizations.corechart.Series.Type;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.shared.Measurement;

public class MeasurementPieChart extends VerticalPanel
{
	private static Vector<LogEntry> currentLogEntries = new Vector<LogEntry>(); // stores pulled in logs
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static Measurement displayChoice = null;
	private static int RANGE = 7;
	private static PieChart chart;
	private static DataTable data;
	
	private static Goals goals = null;
	private static PieOptions currentOptions;
	
	private static int width = 400;
	private static int height = 280;
	
	private static final String colors[] = {"FF0000", "0000FF", "FFFF00", "FFA500", "008000", "000000"};
	
	
	/**
	 * CONSTRUCTOR
	 */
	public MeasurementPieChart()
	{
		Runnable onLoadCallback = new Runnable()
		{
			public void run() 
			{
				load();
			}
		};
		
		// attempts to load the graph
		VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);
		
	} // end Graph()
	
	
	/**
	 * Loads the default graph attributes.
	 */
	public void load()
	{
//		Window.alert("Graph loaded");
	
		// blank graph
	    data = DataTable.create(); 
	    data.addColumn(ColumnType.STRING, "X");
	    data.addColumn(ColumnType.NUMBER, "Chanel 1");
	    setOptions("");
	    
        chart = new PieChart(data, currentOptions);
	
	    Label status = new Label();
	    Label onMouseOverAndOutStatus = new Label();
	    this.add(status);
	    this.add(chart);
	    this.add(onMouseOverAndOutStatus);
	    
	} // end load()
	
	
	/**
	 * Returns the formatting options for the graph axis
	 */
	public void setOptions(String title)
	{
		String axisFontSize = "11";
		
		// set up graph base
		currentOptions = PieOptions.create();
		currentOptions.setHeight(height);
		currentOptions.setTitle(title);
		currentOptions.setWidth(width);
		currentOptions.setInterpolateNulls(true);
		//currentOptions.set3D(true);

	    
	} // end getXAxisOptions()
	

	
	/**
	 * Plots the data from the global log entry list.
	 */
	public void plot(double cals, double protein, double carbs, double fats)
	{		
		setOptions("Macros");
		
		// sets up data table
	    DataTable data = DataTable.create();
	    data.addRows(3);
	    data.addColumn(ColumnType.STRING, "Measurement");  
	    data.addColumn(ColumnType.NUMBER, "Value");
	   	    
	    data.setValue(0,  0,  "Protein");
	    data.setValue(0,  1,  protein);
	    data.setValue(1,  0,  "Carbohydrates");
	    data.setValue(1,  1,  carbs);
	    data.setValue(2,  0,  "Fats");
	    data.setValue(2,  1,  fats);
	    
	    // draw chart
	    chart.draw(data, currentOptions);
	    
	} // end plot()

} // end class Graph
