package com.tla.foodtracker.client.plots;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.Series;
import com.google.gwt.visualization.client.visualizations.corechart.Series.Type;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.shared.Measurement;

public class MeasurementGraph extends VerticalPanel
{
	private static Vector<LogEntry> currentLogEntries = new Vector<LogEntry>(); // stores pulled in logs
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static Measurement displayChoice = null;
	private static int RANGE = 7;
	private static ComboChart chart;
	private static DataTable data;
	
	private static Goals goals = null;
	private static ComboChart.Options currentOptions;
	
	private static int width = 1000;
	private static int height = 600;
	
	private static final String colors[] = {"FF0000", "0000FF", "FFFF00", "FFA500", "008000", "000000"};
	
	
	/**
	 * CONSTRUCTOR
	 */
	public MeasurementGraph()
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
	    setOptions("Plot", "Units");
	    
        chart = new ComboChart(data, currentOptions);
	
	    Label status = new Label();
	    Label onMouseOverAndOutStatus = new Label();
	    this.add(status);
	    this.add(chart);
	    this.add(onMouseOverAndOutStatus);
	    
	} // end load()
	
	
	/**
	 * Returns the formatting options for the graph axis
	 */
	public void setOptions(String title, String yAxisTitle)
	{
		String axisFontSize = "11";
		
		// set up graph base
		currentOptions = ComboChart.createComboOptions();
		currentOptions.setHeight(height);
		currentOptions.setTitle(title);
		currentOptions.setWidth(width);
		currentOptions.setInterpolateNulls(true);
//		Options backgroundColor = ComboChart.createComboOptions();
//		backgroundColor.set("strokeWidth", "1");
//		currentOptions.set("backgroundColor",  backgroundColor);
	    
		Options chartArea = ComboChart.createComboOptions();
		chartArea.set("left", "115");
		chartArea.set("top", "75");
		chartArea.set("width", "75%");
		chartArea.set("height", "75%");
		currentOptions.set("chartArea", chartArea);
		
	    // set y axis properties
		HorizontalAxisOptions y = HorizontalAxisOptions.create();
		y.setTitle(yAxisTitle);
		Options textStyle = ComboChart.createComboOptions();
		textStyle.set("fontSize",  "10");
		y.set("textStyle", textStyle);
		Options gridLines = ComboChart.createComboOptions();
		gridLines.set("count", "10");
		y.set("gridlines", gridLines);
		Options titleTextStyle = ComboChart.createComboOptions();
		titleTextStyle.set("color", "DarkBlue");
		titleTextStyle.set("italic",  "false");
		titleTextStyle.set("bold",  "true");
		y.set("titleTextStyle",  titleTextStyle);
		
		// set x axis properties
	    AxisOptions x1 = AxisOptions.create();
	    x1.setTitle("Date");
	    textStyle = ComboChart.createComboOptions();
		textStyle.set("fontSize",  axisFontSize);
		x1.set("textStyle", textStyle);
		titleTextStyle = ComboChart.createComboOptions();
		titleTextStyle.set("color", "DarkBlue");
		titleTextStyle.set("italic",  "false");
		titleTextStyle.set("bold",  "true");
		x1.set("titleTextStyle",  titleTextStyle);
		
	    currentOptions.setVAxisOptions(y);
	    //currentOptions.setHAxisOptions(x);
	    currentOptions.setHAxisOptions(x1);
	    
	} // end getXAxisOptions()
	

	
	/**
	 * Plots the data from the global log entry list.
	 */
	private void plot()
	{		
		// measurement line
	    Series measurementSeries = Series.create();
	    measurementSeries.setType(Type.LINE);
	    // alter the color a bit
	    int value = Integer.parseInt(colors[displayChoice.ordinal()], 16);
	    value += 100;
	    String newColor = Integer.toHexString(value);
	    measurementSeries.setColor(newColor);
	    currentOptions.setSeries(0,  measurementSeries);
		
		// measurement bar
	    Series measurementBarSeries = Series.create();
	    measurementBarSeries.setType(Type.BARS);
	    value += 100;
	    newColor = Integer.toHexString(value);
	    measurementBarSeries.setColor(newColor);
	    measurementBarSeries.setLineWidth(20);
	    currentOptions.setSeries(1,  measurementBarSeries);
	    
	    // target line
	    Series targetSeries = Series.create();
	    targetSeries.setType(Type.LINE);
	    targetSeries.setColor(colors[displayChoice.ordinal()]);
	    currentOptions.setSeries(2,  targetSeries);
	    	    
	    // sets up data table
	    DataTable data = DataTable.create();
	    data.addRows(currentLogEntries.size());
	    data.addColumn(ColumnType.STRING, "X");  // x axis labels
	    data.addColumn(ColumnType.NUMBER, displayChoice.toString() + " L"); // line
	    data.addColumn(ColumnType.NUMBER, displayChoice.toString() + " B"); // bar
	    data.addColumn(ColumnType.NUMBER, "Goal"); // goal

	    // main plot data (measurements and workout)
	    int row = 0;
	    for (LogEntry le : currentLogEntries)
	    {
	    	Date date = dateFormat.parse(le.getDate());
	    	
	    	// sets x axis label
	    	data.setValue(row, 0, le.getDate());
	    	
	    	// plots value
	    	data.setValue(row, 1, le.getFoodEntries().calculateTotal(displayChoice));
	    	data.setValue(row, 2, le.getFoodEntries().calculateTotal(displayChoice));

	    	row++;
	    }
	    
	    // first target point
	    data.setValue(0, 3, goals.getMeasurement(displayChoice));
	    // second target point
	    data.setValue(currentLogEntries.size() - 1, 3, goals.getMeasurement(displayChoice));
	    
	    // draw chart
	    chart.draw(data, currentOptions);
	    
	} // end plot()
	
	public void plotMeasurement(Measurement msm, Vector<LogEntry> les, Goals gls, int range)
	{
		displayChoice = msm;
		currentLogEntries = les;
		goals = gls;
		RANGE = range;
		
		// sets the basic graph attributes
		setOptions(displayChoice.toString(), Measurement.getUnit(displayChoice).toString());
		plot();
		
	} // end plotMeasurement()


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
