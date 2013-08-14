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
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.client.shared.Workout;

public class WorkoutGraph extends VerticalPanel
{
	private static Vector<LogEntry> currentLogEntries = new Vector<LogEntry>(); // stores pulled in logs
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static int RANGE = 7;
	private static ColumnChart chart;
	private static DataTable data;
	
	private static Goals goals = null;
	private static Options currentOptions;
	
	private static int width = 900;
	private static int height = 100;
	
	private static final String colors[] = {"FF0000", "0000FF", "FFFF00", "FFA500", "008000", "000000"};
	
	
	/**
	 * CONSTRUCTOR
	 */
	public WorkoutGraph()
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
		// blank graph
	    data = DataTable.create(); 
	    data.addColumn(ColumnType.STRING, "X");
	    data.addColumn(ColumnType.NUMBER, "Chanel 1");
	    setOptions("Workout");
	    
        chart = new ColumnChart(data, currentOptions);
	
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
		currentOptions = Options.create();
		currentOptions.setHeight(height);
		currentOptions.setTitle(title);
		currentOptions.setWidth(width);
		currentOptions.setInterpolateNulls(true);

		Options chartArea = Options.create();
		chartArea.set("left", "115");
		chartArea.set("top", "0");
		chartArea.set("width", "75%");
		chartArea.set("height", "20%");
		currentOptions.set("chartArea", chartArea);
		
		Options backgroundColor = Options.create();
		backgroundColor.set("strokeWidth", "0");
		backgroundColor.set("fill", "white");
		currentOptions.set("backgroundColor", backgroundColor);
		
	    // set y axis properties
		HorizontalAxisOptions y = HorizontalAxisOptions.create();
		y.setTitle("");
		Options textStyle = Options.create();
		textStyle.set("fontSize",  "10");
		y.set("textStyle", textStyle);
		Options gridLines = Options.create();
		gridLines.set("count", "0");
		gridLines.set("color", "white");
		y.set("gridlines", gridLines);
		Options titleTextStyle = Options.create();
		titleTextStyle.set("color", "DarkBlue");
		titleTextStyle.set("italic",  "false");
		titleTextStyle.set("bold",  "true");
		y.set("titleTextStyle",  titleTextStyle);
		
		// set x axis properties
	    AxisOptions x = AxisOptions.create();
	    x.setTitle("");
	    textStyle = Options.create();
		textStyle.set("fontSize",  axisFontSize);
		x.set("textStyle", textStyle);
		//x.set("textPosition",  "none");
		gridLines = Options.create();
		gridLines.set("color", "white");
		x.set("gridlines",  gridLines);		
		titleTextStyle = Options.create();
		titleTextStyle.set("color", "DarkBlue");
		titleTextStyle.set("italic",  "false");
		titleTextStyle.set("bold",  "true");
		x.set("titleTextStyle",  titleTextStyle);
	    
	    currentOptions.setVAxisOptions(y);
	    currentOptions.setHAxisOptions(x);
	    
	} // end getXAxisOptions()
	

	
	/**
	 * Plots the data from the global log entry list.
	 */
	private void plot()
	{
	    // sets up data table
	    DataTable data = DataTable.create();
	    data.addRows(currentLogEntries.size());
	    data.addColumn(ColumnType.STRING,"X");  // x axis labels
	    data.addColumn(ColumnType.NUMBER,"Workout");  // x axis labels
	    
//	    for (Workout wo : Workout.values())
//	    {
//	    	if (wo != Workout.NONE)
//	    	data.addColumn(ColumnType.NUMBER, wo.toString());
//	    }
	    

	    // main plot data (measurements and workout)
	    int row = 0;
	    for (LogEntry le : currentLogEntries)
	    {	
	    	if (le.getWorkout() != Workout.NONE)
	    	{
	    		data.setValue(row, 0, le.getWorkout().toString());
	    		data.setValue(row, 1, 1);
	    	}
	    	else
	    	{
	    		data.setValue(row, 0, "");
	    		data.setValue(row, 1, 0);
	    	}

	    	row++;
	    }
	    
	    // draw chart
	    chart.draw(data, currentOptions);
	    
	} // end plot()
	
	public void plotWorkout(Vector<LogEntry> les, Goals gls, int range)
	{
		currentLogEntries = les;
		goals = gls;
		RANGE = range;
		
		// sets the basic graph attributes
		setOptions("Workout");
		plot();
		
	} // end plotWorkout()


	/**
	 * GETTERS AND SETTERS
	 */
	
	public int getRange()
	{
		return RANGE;
		
	} // end getRange()
	
	public void setRange(int range)
	{
		if (range < 0)
		{
			RANGE = 0;
			return;
		}
		
		RANGE = range;
		
	} // end setRange()

} // end class Graph
