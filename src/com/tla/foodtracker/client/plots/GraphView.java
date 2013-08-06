package com.tla.foodtracker.client.plots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.tla.foodtracker.client.shared.DataManager;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.client.shared.NumberSpinner;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.Measurement;

public class GraphView extends VerticalPanel
{
	private static MeasurementGraph msmGraph = new MeasurementGraph();
	private static WorkoutGraph woGraph = new WorkoutGraph();
	private MetricsPanel metricsPanel;
	private NumberSpinner rangeSpinner;
	private ListBox measurementBox;
	private ArrayList<RadioButton> selectionButtons = new ArrayList<RadioButton>();	
	
	private static Goals goals = null;
	
	private static Vector<LogEntry> currentLogEntries = new Vector<LogEntry>(); // stores pulled in logs
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static Measurement displayChoice = null;
	
	// tracks start and end of log ranges
	private static int logCount = 0;
	private static int RANGE = 7;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public GraphView()
	{
		// creates button panel and adds radio button selections in a 2 column
		// format
		FlexTable buttonTable = new FlexTable();
		int col = 0;
		int row = 0;
		
		// creates list box for all displayable measurements
		measurementBox = new ListBox();
		measurementBox.setStyleName("dropDownBox");
		for (Measurement msm : Measurement.values())
			measurementBox.addItem(msm.toString());
		
		Label measurementLabel = new Label("Measurement");
		measurementLabel.setStyleName("sectionTitle");
		buttonTable.setWidget(0,  0,  measurementLabel);
		buttonTable.setWidget(0,  1,  measurementBox);
		
		// sets up metrics panel
		metricsPanel = new MetricsPanel();
		metricsPanel.setStyleName("metricsPanel");
		
		HorizontalPanel rangePanel = new HorizontalPanel();
		rangePanel.setSpacing(5);
		rangeSpinner = new NumberSpinner(RANGE, 2, 20);
		rangeSpinner.setEnabled(false);
		Label rangeLabel = new Label("Range");
		rangeLabel.setStyleName("sectionTitle");
		rangePanel.add(rangeLabel);
		rangePanel.add(rangeSpinner);
		
		// creates lower graph control panel
		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.add(buttonTable);
		bottomPanel.add(rangePanel);
		bottomPanel.setHeight("20%");
		
		// creates graphs panel
		VerticalPanel graphPanel = new VerticalPanel();
		graphPanel.add(msmGraph);
		graphPanel.add(woGraph);
		
		// creates main display panel hosting graph and metrics
		HorizontalPanel bodyPanel = new HorizontalPanel();
		bodyPanel.add(graphPanel);
		bodyPanel.add(metricsPanel);

		//this.addSouth(bottomPanel,  250);
		this.add(bottomPanel);
		this.add(bodyPanel);
		
		
		rangeSpinner.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				RANGE = rangeSpinner.getValue();
				GraphView.this.plotMeasurement(Measurement.findEnum(measurementBox.getValue(measurementBox.getSelectedIndex())));
			}
		});
		measurementBox.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				rangeSpinner.setEnabled(true);
				GraphView.this.plotMeasurement(Measurement.findEnum(measurementBox.getValue(measurementBox.getSelectedIndex())));				
			}	
		});
		
	} // end  GraphView()
	
	
	private String getSelection()
	{
		for (RadioButton rb : selectionButtons)
		{
			if (rb.getValue() == true)
			{
				return rb.getText();
			}
		}
		
		return "";
		
	} // end getSelection()
	
	
	public void refresh()
	{
		// refreshes the last graph view prior to leaving the tab
		String selection = getSelection();
		 if ("".equals(selection))
			 return;
		 
		 this.plotMeasurement(Measurement.findEnum(getSelection()));
		
	} // end refresh()

	
	
	///////////////////////////////////////////////////////////////////////////
	// Data request chain
	// 1. plotMeasurement
	// 2. loadGoals
	// 3. requestLogEntries 
	// 4. loadLogEntry
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
		
		for (int index = 0; index < span; ++index)
		{
			String dateStr = dateFormat.format(date);
			DataManager.requestLogEntry(dateStr, Destination.PLOT_LINE);
			date.setDate(date.getDate() + 1);
		}
		date.setDate(date.getDate() - 1);
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
			
			// select which view to plot
			msmGraph.plotMeasurement(displayChoice, currentLogEntries, goals, RANGE);
			woGraph.plotWorkout(currentLogEntries, goals, RANGE);
		}
		
	} // end loadLogEntry()
	
	
	/**
	 * Stores the goals object from the server then makes a request to 
	 * retrieve the log entry
	 */
	public static void loadGoals(Goals gls)
	{
		goals = gls;
		
		requestLogEntries(RANGE);
		
	} // loadGoals()	

} // end class GraphView
