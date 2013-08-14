package com.tla.foodtracker.client.plots;

import java.util.Collections;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.DataManager;
import com.tla.foodtracker.client.shared.ExceptionWindow;
import com.tla.foodtracker.client.shared.Food;
import com.tla.foodtracker.client.shared.FoodList;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.client.shared.NumberSpinner;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.Measurement;

public class GraphView extends VerticalPanel implements ClickHandler, ChangeHandler
{
	private static MeasurementGraph msmGraph = new MeasurementGraph();
	private static WorkoutGraph woGraph = new WorkoutGraph();
	private MetricsPanel metricsPanel;
	private NumberSpinner rangeSpinner;
	private static ListBox measurementBox;
	private static ListBox foodBox;
	
	private RadioButton measurementRadioButton;
	private RadioButton foodRadioButton;
	
	private static Goals goals = null;
	
	// general variables
	private static Vector<LogEntry> currentLogEntries = new Vector<LogEntry>(); // stores pulled in logs
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static Measurement msmDisplayChoice = null;
	private static Food foodDisplayChoice = null;
	private static FoodList foodList = null;
	
	// tracks start and end of log ranges
	private static int logCount = 0;
	private static int RANGE = 7;
	
	private static GraphType selection = GraphType.MEASUREMENT;
	
		
	/**
	 * CONSTRUCTOR
	 */
	public GraphView()
	{
		// creates button panel and adds radio button selections in a 2 column
		// format
		FlexTable buttonTable = new FlexTable();
		buttonTable.getElement().getStyle().setBorderColor("silver");
		buttonTable.getElement().getStyle().setBorderWidth(1, Unit.PX);
		int row = 0;
		
		// creates list box for all displayable measurements
		measurementBox = new ListBox();
		measurementBox.setStyleName("dropDownBox");
		measurementBox.addItem("");
		for (Measurement msm : Measurement.values())
			measurementBox.addItem(msm.toString());
		measurementRadioButton = new RadioButton("graphTypeGroup", "Measurement:");
//		measurementRadioButton.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		measurementRadioButton.setValue(true);
		buttonTable.setWidget(row, 0, measurementRadioButton);
		//buttonTable.setWidget(row,  0,  measurementLabel);
		buttonTable.setWidget(row++, 1,  measurementBox);
		
		// sets up metrics panel
		metricsPanel = new MetricsPanel();
		metricsPanel.setStyleName("metricsPanel");
		
		// creates food selection panel
		HorizontalPanel foodPanel = new HorizontalPanel();
		foodPanel.setSpacing(5);
		foodBox = new ListBox();
		foodBox.setStyleName("dropDownBox");
		foodBox.setEnabled(false);  // disabled since measurement box is the current selection
		
		foodRadioButton = new RadioButton("graphTypeGroup", "Food:");
//		foodRadioButton.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
		buttonTable.setWidget(row, 0, foodRadioButton);
		//buttonTable.setWidget(row,  0,  foodLabel);
		buttonTable.setWidget(row++, 1,  foodBox);
		
		HorizontalPanel rangePanel = new HorizontalPanel();
		rangePanel.setSpacing(5);
		rangeSpinner = new NumberSpinner(RANGE, 2, 20);
		rangeSpinner.setEnabled(false);
		Label rangeLabel = new Label("Range:");
		rangePanel.add(rangeLabel);
		rangePanel.add(rangeSpinner);
		
		// creates lower graph control panel
		HorizontalPanel topPanel = new HorizontalPanel();
		topPanel.add(buttonTable);
		topPanel.add(rangePanel);
		topPanel.setHeight("20%");
		
		// creates graphs panel
		VerticalPanel graphPanel = new VerticalPanel();
		graphPanel.getElement().setAttribute("overflow",  "scroll");
		graphPanel.setStyleName("graphPanel");
		graphPanel.add(msmGraph);
		graphPanel.add(woGraph);
		
		// creates main display panel hosting graph and metrics
		HorizontalPanel bodyPanel = new HorizontalPanel();
		bodyPanel.add(graphPanel);
		bodyPanel.add(metricsPanel);

		//this.addSouth(topPanel,  250);
		this.add(topPanel);
		this.add(bodyPanel);
		
		
		rangeSpinner.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				// pull in range value and plot
				RANGE = rangeSpinner.getValue();
				GraphView.this.plot();
			}
		});
		measurementBox.addChangeHandler(this);
		foodBox.addChangeHandler(this);
		measurementRadioButton.addClickHandler(this);
		foodRadioButton.addClickHandler(this);
		
	} // end  GraphView()

	
	/**
	 * Returns back the selected radio button for the graph display.
	 * @return
	 */
	private GraphType getGraphSelection()
	{
		if (measurementRadioButton.getValue())
			return GraphType.MEASUREMENT;
		else 
			return GraphType.FOOD;
		
	} // end getGraphSelection()
	
	
	/**
	 * Refreshes information on this page.
	 */
	public void refresh()
	{
		// re-request food list
		requestFoodList();
		
		// re-pull log entry data
//		if (measurementBox.getSelectedIndex() == 0)
//			return;
//		rangeSpinner.setEnabled(true);
//		GraphView.this.plot(Measurement.findEnum(measurementBox.getValue(measurementBox.getSelectedIndex())));
		
	} // end refresh()

	
	
	///////////////////////////////////////////////////////////////////////////
	// Data request chain
	// 1. plot
	// 2. loadGoals
	// 3. requestLogEntries 
	// 4. loadLogEntry
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts the plotting request chain based on the selected view and item.
	 * @param msm
	 */
	public void plot()
	{
		// checks if there is a selection made and stores the selection type
		switch (getGraphSelection())
		{
			case MEASUREMENT:
				selection = GraphType.MEASUREMENT;
				if (measurementBox.getSelectedIndex() == 0)
					return;
				msmDisplayChoice = Measurement.findEnum(measurementBox.getValue(measurementBox.getSelectedIndex()));
				break;
			case FOOD:
				selection =  GraphType.FOOD;
				if (foodBox.getSelectedIndex() == 0)
					return;
				foodDisplayChoice = foodList.getFood(foodBox.getItemText(foodBox.getSelectedIndex()));
				break;
		}
		
		
		goals = null;		
		DataManager.requestGoals(Destination.PLOT_LINE);

	} // end plot()
	
	
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
	 * Collects log entries as they are passed in.  Once all log entries have 
	 * been received, plot the log entry data based on the selection.
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
			MetricsPanel.updateMetrics(currentLogEntries, goals);
			
			// select which view to plot
			switch (selection)
			{
				case MEASUREMENT:
					msmGraph.plotMeasurement(msmDisplayChoice, currentLogEntries, goals, RANGE);
					break;
				case FOOD:
					msmGraph.plotFood(foodDisplayChoice, currentLogEntries, goals, RANGE);
					break;
			}			
			
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
	
	
	/**
	 * Requests the food list from the server.
	 */
	private static void requestFoodList()
	{
		try 
		{
			DataManager.requestFoodList(Destination.PLOT_LINE);
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}
		
	}  // requestFoodList()
	
	
	/**
	 * Loads the passed in food list to the table.
	 * @param fl
	 */
	public static void loadFoodList(FoodList fl)
	{
		foodList = fl;
		
		foodBox.clear();
		foodBox.addItem("");
		for (Food food : fl)
			foodBox.addItem(food.getName());
			
	} // end loadFoodList()


	
	///////////////////////////////////////////////////////////////////////////
	// LISTNERS
	///////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onClick(ClickEvent event)
	{
		if (measurementRadioButton == event.getSource())
		{
			// make this list box enabled and disable all others
			measurementBox.setEnabled(true);
			foodBox.setEnabled(false);		
		}
		else if (foodRadioButton == event.getSource())
		{
			// make this list box enabled and disable all others
			foodBox.setEnabled(true);
			measurementBox.setEnabled(false);
		}
		
		rangeSpinner.setEnabled(true);
		GraphView.this.plot();		
		
	} // end onClick()
	
	@Override
	public void onChange(ChangeEvent event)
	{
		rangeSpinner.setEnabled(true);
		GraphView.this.plot();		
		
	} // end onChange()
	
	
	///////////////////////////////////////////////////////////////////////////
	// Inner classes
	///////////////////////////////////////////////////////////////////////////
		
	/**
	 * Enum for the graph type display.
	 * @author abeyti_t
	 *
	 */
	private enum GraphType
	{
		MEASUREMENT("Measurement"),
		FOOD("Food");
		
		private final String str;
		GraphType(String str)
		{
			this.str = str;
		}
		public String toString()
		{
			return str;
		}
		
	} // end enum GraphType


	

} // end class GraphView
