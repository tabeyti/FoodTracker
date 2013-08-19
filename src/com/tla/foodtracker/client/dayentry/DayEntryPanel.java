package com.tla.foodtracker.client.dayentry;

import java.util.Date;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.tla.foodtracker.client.IView;
import com.tla.foodtracker.client.plots.GraphView;
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
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static DateTimeFormat dayFormat = DateTimeFormat.getFormat("EEEE");	
	private static FoodEntryTable table;
	private static DayBar dayBar;
	private static LoadingPanel loadingPanel;
	private static ListBox workoutListBox;
	private static NotesPanel notesPanel;
	
	private static Button addButton;
	private static Button saveButton;

	private DaySummaryPanel daySummaryPanel;
	private static GoalsPanel goalsPanel;
	
	private int statsPanelWidth = 300;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public DayEntryPanel()
	{
		super(Unit.PX);
		this.setStyleName("temp");
		
		// sets the lower data panel
		VerticalPanel statsPanel = new VerticalPanel();
		statsPanel.setStyleName("statsPanel");
		statsPanel.setWidth(Integer.toString(statsPanelWidth - 10) + "px");
		statsPanel.setSpacing(5);
		
		daySummaryPanel = new DaySummaryPanel();
		daySummaryPanel.setStyleName("statsPanelItem");
		daySummaryPanel.setWidth("100%");
		goalsPanel = new GoalsPanel();
		goalsPanel.setStyleName("statsPanelItem");
		goalsPanel.setWidth("100%");
		goalsPanel.setHeight("100%");
		notesPanel = new NotesPanel();
		notesPanel.setStyleName("statsPanelItem");
		notesPanel.setWidth("100%");
		notesPanel.setHeight("250px");
		statsPanel.add(daySummaryPanel);
		statsPanel.add(goalsPanel);
		statsPanel.add(notesPanel);
		
		// sets up the upper bar containing date chooser and such
		HorizontalPanel topPanel = new HorizontalPanel();
		topPanel.setWidth("100%");
		
	    dayBar = new DayBar(0);
	    dayBar.setStyleName("dayBar");
	    dayBar.setDay(dayFormat.format(new Date()));
	    
	    // populates workout list box
	    FlexTable workoutTable = new FlexTable();
	    workoutTable.getElement().setAttribute("align",  "right");
	    workoutListBox = new ListBox();
	    workoutListBox.setStyleName("dropDownBox");
	    for (Workout wo : Workout.values())
	    	workoutListBox.addItem(wo.toString());
	    workoutTable.setText(0,  0,  "Workout");
	    workoutTable.setWidget(0, 1, workoutListBox);
	    workoutTable.setText(0, 2, " ");
	    workoutTable.getRowFormatter().addStyleName(0,  "workoutTable");

	    // adds top items to top panel
	    topPanel.add(dayBar);
	    topPanel.add(workoutTable);

	    // initializes main entry table
	    table = new  FoodEntryTable(daySummaryPanel);
	    table.setWidth("100%");
	    table.setStyleName("shadow");
	    
	    // sets up control buttons
	    VerticalPanel buttonPanel = new VerticalPanel();
	    
		addButton = new Button("Add");
		addButton.setStyleName("button-link");
		addButton.setEnabled(false);
		saveButton = new Button("Save");
		saveButton.setEnabled(false);
		saveButton.setStyleName("button-link");
		buttonPanel.add(addButton);
		buttonPanel.add(saveButton);

		// right bar to hold control button content and other things
		VerticalPanel rightBar = new VerticalPanel();
		rightBar.setSpacing(5);
		rightBar.setHeight("100%");
		rightBar.add(buttonPanel);		
		
		this.addEast(statsPanel, statsPanelWidth);
		this.addNorth(topPanel, 30);
		this.addWest(rightBar, 70);		
		this.addSouth(new HorizontalPanel(), 5);
		this.add(table);
		
		// sets up event listeners
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
		dayBar.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				table.clear();
				requestLogEntry();
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
		goalsPanel.loadGoals(goals);
		
	} // end loadGoals()

	
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
			DataManager.requestLogEntry(dateFormat.format(dayBar.getDate()), Destination.DAY_ENTRY);
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
			// load food entries
			for (FoodEntry fe : le.getFoodEntries())
				table.addFoodEntry(fe);
			
			// set workout
			workoutListBox.setSelectedIndex(le.getWorkout().ordinal());
			
			// sets notes
			notesPanel.setText(le.getNotes());
			
		}
		
		// updates food entries with proper data
		table.updateFoodEntries();
		
		loadingPanel.hide();
		loadingPanel = null;
		
		dayBar.setFired(false);
		
	} // end loadLogEntry()

	
	/**
	 * Saves the current log entry.
	 */
	private void saveLogEntry()
	{
		LogEntry le = new LogEntry(dateFormat.format(dayBar.getDate()));
		
		// pulls food entries from table
		FoodEntries fes = table.getFoodEntries();
		le.setFoodEntries(fes);
		
		// pulls workout data
		le.setWorkout(Workout.findByValue(workoutListBox.getItemText(workoutListBox.getSelectedIndex())));
		
		// pulls notes data
		le.setNotes(notesPanel.getText());
		
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
