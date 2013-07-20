package com.tla.foodtracker.client.dayentry;

import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.tla.foodtracker.client.IView;
import com.tla.foodtracker.client.shared.DataManager;
import com.tla.foodtracker.client.shared.ExceptionWindow;
import com.tla.foodtracker.client.shared.FoodEntries;
import com.tla.foodtracker.client.shared.FoodEntry;
import com.tla.foodtracker.client.shared.FoodList;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.client.shared.LoadingPanel;
import com.tla.foodtracker.client.shared.LogEntry;
import com.tla.foodtracker.client.shared.Workout;
import com.tla.foodtracker.shared.Destination;


public class DayEntryPanel extends DockLayoutPanel implements IView
{
	private Button leftArrowDateButton;
	private Button rightArrowDateButton;
	private static DateBox dateBox;
	private static Label dayOfWeekLabel;
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static DateTimeFormat dayFormat = DateTimeFormat.getFormat("EEEE");	
	private static FoodEntryTable table;
	private static DayBar dayBar;
	private static LoadingPanel loadingPanel;
	private static ListBox workoutListBox;
	
	private static Button addButton;
	private static Button saveButton;

	private DaySummaryPanel daySummaryPanel;
	private static GoalsPanel goalsPanel;
	private static Goals currentGoals;

	private LogEntry currentLogEntry = null;
	private FoodList currentFoodList = null;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public DayEntryPanel()
	{
		super(Unit.PX);
		this.setStyleName("temp");
		
		// sets the lower data panel
		HorizontalPanel statsPanel = new HorizontalPanel();
		statsPanel.setSpacing(10);
		statsPanel.setStyleName("statsPanel");
		daySummaryPanel = new DaySummaryPanel();
		daySummaryPanel.setStyleName("statsPanelItem");
		goalsPanel = new GoalsPanel();
		goalsPanel.setStyleName("statsPanelItem");
		goalsPanel.setWidth("400px");
		statsPanel.add(daySummaryPanel);
		statsPanel.add(goalsPanel);
		
		// sets up the upper bar containing date chooser and such
		HorizontalPanel topPanel = new HorizontalPanel();
		topPanel.setSpacing(5);
		
	    dateBox = new DateBox();
	    dateBox.setValue(new Date());
	    dateBox.getTextBox().setReadOnly(true);
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));

	    leftArrowDateButton = new Button("<<");
	    leftArrowDateButton.setStyleName("flatButton");
	    rightArrowDateButton = new Button(">>");
	    rightArrowDateButton.setStyleName("flatButton");
	    dayOfWeekLabel = new Label(dayFormat.format(new Date()));
	    dayOfWeekLabel.setStyleName("sectionTitle");
	    dayBar = new DayBar(0);
	    dayBar.setStyleName("dayBar");
	    dayBar.setDay(dayFormat.format(new Date()));
	    
	    // populates workout list box
	    workoutListBox = new ListBox();
	    workoutListBox.setStyleName("dropDownBox");
	    for (Workout wo : Workout.values())
	    	workoutListBox.addItem(wo.toString());
	    
	    topPanel.add(leftArrowDateButton);
	    topPanel.add(dateBox);
	    topPanel.add(rightArrowDateButton);
//	    topPanel.add(dayOfWeekLabel);
	    topPanel.add(dayBar);
	    topPanel.add(workoutListBox);

	    table = new  FoodEntryTable(daySummaryPanel);
	    table.setWidth("100%");
	    
	    // sets up control buttons
	    HorizontalPanel buttonPanel = new HorizontalPanel();
		addButton = new Button("Add");
		addButton.setEnabled(false);
		saveButton = new Button("Save");
		saveButton.setEnabled(false);
		buttonPanel.add(addButton);
		buttonPanel.add(saveButton);
		
		// bottom panel
		DockLayoutPanel bottomPanel = new DockLayoutPanel(Unit.PX);
		bottomPanel.addNorth(buttonPanel, 40);
		bottomPanel.add(statsPanel);
		bottomPanel.setStyleName("shadow");
		
		int bottomPanelHeight = Window.getClientHeight() / 3;
		
		this.addNorth(topPanel, 30);
		this.addSouth(bottomPanel, bottomPanelHeight);		
		this.add(table);
		
		// sets up event listeners
		leftArrowDateButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				incDecDate(false);				
			}
			
		});
		rightArrowDateButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				incDecDate(true);				
			}
			
		});
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Date> event)
			{
				setDate(dateBox.getValue());				
			}
		});
		addButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				if (0 == table.getFoodList().size())
				{
					ExceptionWindow.UserAlert("No food list available");
				}
				
				FoodEntry fe = new FoodEntry(table.getFoodList().get(0));
				table.addFoodEntry(fe);		
			}
		});
		saveButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				saveLogEntry();
			}
		});
		
		requestGoals();
		requestFoodList();		
		
	} // end DayEntryPanel()

	
	/**
	 * Requests the food list from the server.
	 */
	private static void requestFoodList()
	{
		try 
		{
			DataManager.requestFoodList(Destination.DAY_ENTRY);
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}
		
	} // end requestFoodList()
	
	
	/**
	 * Requests the goals object from the server.
	 */
	public static void requestGoals()
	{
		loadingPanel = new LoadingPanel();
		
		try 
		{
			DataManager.requestGoals(Destination.DAY_ENTRY);
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}
		
	} // end requestGoals()
	
	
	/**
	 * Loads the food list then requests to load the log entry
	 * @param fl
	 */
	public static void loadFoodList(FoodList fl)
	{
		table.setFoodList(fl);
		requestLogEntry();
		
	} // end loadFoodList()
	
	
	/**
	 * Sets the current goals to the goals panel.
	 * @param goals
	 */
	public static void loadGoals(Goals goals)
	{
		currentGoals = goals;
		goalsPanel.loadGoals(goals);
		
	} // end loadGoals()

	
	/**
	 * Increments or decrements the date based on the passed in boolean.
	 * @param inc
	 */
	private void incDecDate(boolean inc)
	{
		Date date = dateBox.getValue();
		
		if (inc)
			date.setDate(date.getDate() + 1);
		else
			date.setDate(date.getDate() - 1);
		
		setDate(date);
		
	} // end incDecDate()
	
	
	/**
	 * Sets the passed date to the date box and does a new log entry 
	 * lookup.
	 * @param date
	 */
	private void setDate(Date date)
	{
		dateBox.setValue(date);
		dayOfWeekLabel.setText(dayFormat.format(date));
		dayBar.setDay(dayFormat.format(date));
		
		table.clear();
		requestLogEntry();
		
	} // end setDate(Date date)
	
	
	/**
	 * Sends the request for the specified log entry (determined by date).
	 */
	public static void requestLogEntry()
	{
		if (null == loadingPanel)
			loadingPanel = new LoadingPanel();
			
		
		try 
		{
			// disables control buttons while request is in progress
			addButton.setEnabled(false);
			saveButton.setEnabled(false);			
			DataManager.requestLogEntry(dateFormat.format(dateBox.getValue()), Destination.DAY_ENTRY);
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}
		
	} // end requestLogEntry()
	
	
	/**
	 * Loads the passed in log entry.
	 * @param le
	 */
	public static void loadLogEntry(LogEntry le)
	{
		// clear table
		table.clear();
		
		// reenable control buttons
		addButton.setEnabled(true);
		saveButton.setEnabled(true);
		
		if (null != le)
		{	
			// set date
//			Date date = dateFormat.parse(le.getDate());
//			dateBox.setValue(date);
//			dayOfWeekLabel.setText(dayFormat.format(date));
			
			// load food entries
			for (FoodEntry fe : le.getFoodEntries())
				table.addFoodEntry(fe);
			
			// set workout
			workoutListBox.setSelectedIndex(le.getWorkout().ordinal());
			
		}
		
		// updates food entries with proper data
		table.updateFoodEntries();
		
		loadingPanel.hide();
		loadingPanel = null;
		
	} // end loadLogEntry()

	
	/**
	 * Saves the current log entry.
	 */
	private void saveLogEntry()
	{
		LogEntry le = new LogEntry(dateFormat.format(dateBox.getValue()));
		
		// pulls food entries from table
		FoodEntries fes = table.getFoodEntries();
		le.setFoodEntries(fes);
		
		// pulls workout data
		le.setWorkout(Workout.findByValue(workoutListBox.getItemText(workoutListBox.getSelectedIndex())));
		
		// saves log entry
		try 
		{
			DataManager.setLogEntry(le);
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}
		
	} // end saveLogEntry()


	@Override
	public void refresh()
	{
		requestFoodList();	
	}
	
} // end class DayEntryPanel
