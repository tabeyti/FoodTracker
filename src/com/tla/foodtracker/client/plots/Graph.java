package com.tla.foodtracker.client.plots;

import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.tla.foodtracker.client.shared.DataManager;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.Measurement;

public class Graph extends VerticalPanel
{
	private static Vector<LogEntry> currentLogEntries = new Vector<LogEntry>(); // stores pulled in logs
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static Measurement displayChoice = null;
	private static int RANGE = 7;
	private static LineChart chart;
	private static DataTable data;
	
	private static Goals goals = null;
	private static Options currentOptions;
	
	// tracks start and end of log ranges
	private static String startDate = "01-01-1969";
	private static String endDate = "01-01-1969";
	private static int logCount = 0;
	
	private static int width = 1000;
	private static int height = 650;
	
	private static final String colors[] = {"FF0000", "0000FF", "FFFF00", "FFA500", "008000", "000000"};
	
	
	/**
	 * CONSTRUCTOR
	 */
	public Graph()
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
	    setOptions();
        chart = new LineChart(data, currentOptions);
	
	    Label status = new Label();
	    Label onMouseOverAndOutStatus = new Label();
	    this.add(status);
	    this.add(chart);
	    this.add(onMouseOverAndOutStatus);
	    
	} // end load()
	
	
	/**
	 * Returns the formatting options for the graph axis
	 */
	public static void setOptions()
	{
		// set up graph base
		currentOptions = Options.create();
		currentOptions.setHeight(height);
		currentOptions.setTitle("Plot");
		currentOptions.setWidth(width);
		currentOptions.setInterpolateNulls(true);
	    
	    // set axis properties
		HorizontalAxisOptions y = HorizontalAxisOptions.create();
		y.setTitle("Potatos");
		y.set("vAxis.gridlines",  "count: 10");
		
		
	    AxisOptions yAxis = AxisOptions.create();
	    yAxis.setTitle("Units");
	    yAxis.set("vAxis.gridlines.count",  "10");
	    currentOptions.setVAxisOptions(y);
	    AxisOptions xAxis = AxisOptions.create();
	    xAxis.setTitle("Date");
	    currentOptions.setHAxisOptions(xAxis);
	    
	} // end getXAxisOptions()
	

	
	/**
	 * Plots the data from the global log entry list.
	 */
	private static void plot()
	{
		// creates series line options
		Options series = Options.create();
		
		// target
	    Options targetSeries = Options.create();
	    targetSeries.set("color", "#" + colors[displayChoice.ordinal()]);
	    series.set("0",  targetSeries);
	    
	    // plot
	    Options series1 = Options.create();
	    
	    // alter the color a bit
	    int value = Integer.parseInt(colors[displayChoice.ordinal()], 16);
	    value += 100;
	    String newColor = Integer.toHexString(value);
	    
	    series1.set("color", "#" + newColor);
	    series.set("1",  series1);
	    currentOptions.set("series",  series);
	    
	    // sets up data table
	    DataTable data = DataTable.create();
	    data.addRows(currentLogEntries.size());
	    data.addColumn(ColumnType.DATE,"X");  // x axis labels
	    data.addColumn(ColumnType.NUMBER, displayChoice.toString()); // line
	    data.addColumn(ColumnType.NUMBER, "Goal");	    

	    // main plot data
	    int row = 0;
	    for (LogEntry le : currentLogEntries)
	    {
	    	Date date = dateFormat.parse(le.getDate());
	    	
	    	// sets x axis label
	    	data.setValue(row, 0, date);
	    	
	    	// plots value
	    	data.setValue(row++, 1, le.getFoodEntries().calculateTotal(displayChoice));
	    }
	    
	    // first target point
	    data.setValue(0, 2, goals.getMeasurement(displayChoice)); // TEMP
	    
	    // second target point
	    data.setValue(currentLogEntries.size() - 1, 2, goals.getMeasurement(displayChoice));
	    
	    chart.draw(data, currentOptions);
	    
	} // end plot()
	
	
	
	///////////////////////////////////////////////////////////////////////////
	// Data request chain
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Plots the specified measurement on the graph.
	 * @param msm
	 */
	public void plotMeasurement(Measurement msm)
	{
		goals = null;
		
		displayChoice = msm;
		DataManager.requestGoals(Destination.PLOT_LINE);
		//requestLogEntries(RANGE);

	} // end plotMeasurement()
	
	
	/**
	 * Sends the request for a window of log entries based on the span passed in.
	 * @param span
	 */
	private static void requestLogEntries(int span)
	{
		currentLogEntries = new Vector<LogEntry>();
		
		Date date = new Date();
		date.setDate(date.getDate() - span); // moves date back to the beginning of the range
		startDate = dateFormat.format(date); // saves start date
		
		for (int index = 0; index < span; ++index)
		{
			String dateStr = dateFormat.format(date);
			DataManager.requestLogEntry(dateStr, Destination.PLOT_LINE);
			date.setDate(date.getDate() + 1);
		}
		date.setDate(date.getDate() - 1);
		endDate = dateFormat.format(date); // saves the end date
		logCount = span;
		
	} // end requestLogEntries()
	
	
	/**
	 * Graphs the passed in log entry and stored it in the list.  Once list 
	 * has reached its limit, draw the plotizzle nizzle.
	 * @param le
	 */
	public static void loadLogEntry(LogEntry le)
	{
		currentLogEntries.add(le);
		
		// if we have received all the logs, plot the data
		if (logCount == currentLogEntries.size() && goals != null) // ensures goals were pulled in before moving forward
		{
			// sorts the log entries by date
			//sortLogEntries();
			Collections.sort(currentLogEntries);
			
			// update the metrics display
			MetricsPanel.updateMetrics(currentLogEntries);
			
			// sets the basic graph attributes
			setOptions();
			
			// select which view to plot
			plot();
		}
		
	} // end loadLogEntry()
	
	
	/**
	 * This method's sole purpose is to create emtpy log entries to fill in the
	 * spots where a log entry wasn't created.  For instance, if we requested
	 * log range from 01-01-2010 to 01-03-2010, and we only received 
	 * 01-01-2010 and 01-03-2010, we would need a blank log entry for 
	 * 01-02-2010 to be created.
	 */
	public static void adjustLogEntries()
	{
		// creates a date object and sets it to the start date
		Date date = dateFormat.parse(startDate);
		
		// empty log entry array
		Vector<LogEntry> newLogEntryList = new Vector<LogEntry>();
		
		for (int index = 0, cindex = 0; index < RANGE; ++index)
		{
			LogEntry cle = currentLogEntries.get(cindex);
			String dateStr = dateFormat.format(date);
			
			// matches up the date of the current log entry to the date 
			// following the range and if it doesn't match, make an empty
			// log entry
			if (!cle.getDate().equals(dateStr))
			{
				newLogEntryList.add(new LogEntry(dateStr));
			}
			else
			{
				newLogEntryList.add(cle);
			}				
			
			date.setDate(date.getDate() + 1);
		}
		
		currentLogEntries = newLogEntryList;
		
	} // end adjustLogEntries()
	
	/**
	 * Stores the goals object from the server then makes a request to 
	 * retrieve the log entry
	 */
	public static void loadGoals(Goals gls)
	{
		goals = gls;
		
		requestLogEntries(RANGE);
		
	} // loadGoals()
	
	
	/**
	 * Sorts the list of log entries by date.
	 */
	private static void sortLogEntries()
	{
		
		
	} // sortLogEntries
	
	

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
