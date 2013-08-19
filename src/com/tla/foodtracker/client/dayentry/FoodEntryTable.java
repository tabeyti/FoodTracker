package com.tla.foodtracker.client.dayentry;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.tla.foodtracker.client.shared.CustomCheckBox;
import com.tla.foodtracker.client.shared.DoubleTextBox;
import com.tla.foodtracker.client.shared.ExceptionWindow;
import com.tla.foodtracker.client.shared.Food;
import com.tla.foodtracker.client.shared.FoodEntries;
import com.tla.foodtracker.client.shared.FoodEntry;
import com.tla.foodtracker.client.shared.FoodList;
import com.tla.foodtracker.client.shared.FoodListBox;
import com.tla.foodtracker.shared.Measurement;

public class FoodEntryTable extends DockLayoutPanel implements BlurHandler, ChangeHandler
{
	private static FlexTable table;
	private static FlexTable tableHeader;
	private DaySummaryPanel dsp;
	
	private static FoodList foodList = new FoodList();
	private static FoodEntries foodEntries = new FoodEntries();

	
	/**
	 * CONSTRUCTOR
	 */
	public FoodEntryTable(DaySummaryPanel dsp)
	{
		super(Unit.PX);
		this.dsp = dsp;
	
		// creates header
		tableHeader = new FlexTable();
		tableHeader.setWidth("100%");
		
		table = new FlexTable();
		table.setStyleName("table");
		initializeColumns();
		// initializes header style
		tableHeader.getRowFormatter().setStyleName(0,  "tableHeader");
		// body style
				
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.setStyleName("tableBackground");
		tablePanel.add(table);
	
		// adds components to the main panel
		this.addNorth(tableHeader, 30);
		this.add(tablePanel);		
		
		updateFoodEntries();
		
	} // end FoodEntryTable()
	

	
	/**
	 * Initializes the table's columns, both main table and header in conjuction.
	 */
	private void initializeColumns()
	{
		int col = 0;
		
		// name
		tableHeader.setText(0, col, "Food");
		tableHeader.getColumnFormatter().setStyleName(col,  "tableFirstColumn");
		table.getColumnFormatter().setStyleName(col,  "tableFirstColumn");
		col++;
		
		// units
		tableHeader.setText(0, col, "Units");
		tableHeader.getColumnFormatter().setStyleName(col,  "tableStandardColumn");
		table.getColumnFormatter().setStyleName(col,  "tableStandardColumn");
		col++;
		
		// measurement cols
		for (Measurement msm : Measurement.values())
		{
			tableHeader.setText(0,  col,  msm.toString());
			tableHeader.getColumnFormatter().setStyleName(col,  "tableStandardColumn");
			table.getColumnFormatter().setStyleName(col,  "tableStandardColumn");
			col++;
		}
		
		// remove row column
		tableHeader.setText(0, col, "Remove");
		
		col++;
		
		// custom option column
		tableHeader.setText(0, col, "Custom");
		tableHeader.getColumnFormatter().setStyleName(col,  "tableSecondToLastColumn");
		table.getColumnFormatter().setStyleName(col,  "tableSecondToLastColumn");
		col++;

	} // end initializeColumns()
	
	
	/**
	 * Updates the summary panel with the current values from the food entries.
	 */
	private void updateSummary()
	{
		dsp.updateValues(getFoodEntries());
		
	} // end updateSummary()
	
	
	/**
	 * Adds the passed in food entry into the table at the specified row.
	 * 
	 * @param fe
	 */
	public void addFoodEntry(int row, FoodEntry fe)
	{		
		// adds food entry to end of list OR to the specified index
		if (row >= foodEntries.size())
			foodEntries.add(fe);
		else
			foodEntries.set(row,  fe);
		
		//int row = table.getRowCount();		
		int col = 0;
		boolean custom = true;
		
		// sets up name combo box and selects the name based on the food entry
		FoodListBox nameBox = new FoodListBox(false);
		nameBox.setId(row); // assigns row as the widget id for later retrieval
		nameBox.setStyleName("inputBox");
		for (Food food : foodList)
		{
			// add food to the list box and check if a match was found
			nameBox.addItem(food.getName());
			if (!food.getName().equals("") && food.getName().equals(fe.getName()))
			{
				fe.setUnit(food.getUnit()); // assign unit type to the food entry (not saved when food entry is saved)
				// TODO: save the unit type 
				nameBox.setSelectedIndex(nameBox.getItemCount() - 1);
				custom = false;
			}
		}
		
		// checks if food match was found, and if not, creates a custom row
		if (custom)
		{
			addCustomFoodEntry(row, fe);
			return;
		}
		
		// gets associated food and sets its current values to the food entry (updates if necessary)
		fe.setFood(foodList.getFood(fe.getName()));
				
		// sets up editable portions of the row
		DoubleTextBox unitsBox = new DoubleTextBox();
		unitsBox.setToolTip(fe.getUnit()); // sets unit type tooltip
		unitsBox.setId(row); // assigns row as the widget id for later retrieval
		unitsBox.setText(Double.toString(fe.getUnits()));
		unitsBox.setStyleName("inputBox");
		
		CustomCheckBox customRowCheckBox = new CustomCheckBox();
		customRowCheckBox.setId(row);
				
		// applies values to the table
		table.setWidget(row,  col++,  nameBox);
		table.setWidget(row, col++, unitsBox);
		for (Measurement msm : Measurement.values())
		{
			table.setText(row, col++, Double.toString(fe.getMeasurement(msm) * fe.getUnits()));
		}
		
		Button removeButton = new Button("x");
		removeButton.setStyleName("removeButton");
		table.setWidget(row, col++, removeButton);
		table.setWidget(row, col++, customRowCheckBox);
		
		
		// initializes the event listeners for the editable portions of the row
		nameBox.addChangeHandler(this);
		unitsBox.addBlurHandler(this);
		removeButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				Cell cell = table.getCellForEvent(event);
				if (null != cell)
					removeRow(cell.getRowIndex());
			}
		});
		customRowCheckBox.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				CustomCheckBox box = ((CustomCheckBox) event.getSource());
				convertRow(box.getId(), box.getValue());
			}
		});
		
		// sets style
		table.getRowFormatter().addStyleName(row, "tableRow");
		
		// sets focus
		nameBox.setFocus(true);
		
		updateSummary();
		
	} // end addFoodEntry()
	
	
	/**
	 * Adds a custom row to the table
	 * @param row
	 * @param fe
	 */
	public void addCustomFoodEntry(int row, FoodEntry fe)
	{
		foodEntries.set(row,  fe);
		
		int col = 0;
		
		// name box
		TextBox customNameBox = new TextBox();
		customNameBox.setStyleName("inputBox");
		customNameBox.setText(fe.getName());		
		
		// units field
		DoubleTextBox unitsBox = new DoubleTextBox();
		unitsBox.setId(row); // assigns row as the widget id for later retrieval
		unitsBox.setText(Double.toString(fe.getUnits()));
		unitsBox.setStyleName("inputBox");
		
		CustomCheckBox customRowCheckBox = new CustomCheckBox();
		customRowCheckBox.setId(row);
		customRowCheckBox.setValue(true);
				
		// applies values to the table
		table.setWidget(row,  col++,  customNameBox);
		table.setWidget(row, col++, unitsBox);
		for (Measurement msm : Measurement.values())
		{
			DoubleTextBox msmBox = new DoubleTextBox();
			msmBox.setId(row);
			msmBox.setStyleName("inputBox");
			msmBox.setText(Double.toString(fe.getMeasurement(msm)));
			table.setWidget(row,  col++, msmBox);
			
			// sets event listener for the measurement box
			msmBox.addBlurHandler(new BlurHandler()
			{
				@Override
				public void onBlur(BlurEvent event)
				{
					updateSummary();
				}				
			});

		}
		Button removeButton = new Button("x");
		removeButton.setStyleName("removeButton");
		table.setWidget(row, col++, removeButton);
		table.setWidget(row, col++, customRowCheckBox);		
		
		// initializes the event listeners for the editable portions of the row
		removeButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				Cell cell = table.getCellForEvent(event);
				if (null != cell)
					removeRow(cell.getRowIndex());
			}
		});
		customRowCheckBox.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				CustomCheckBox box = ((CustomCheckBox) event.getSource());
				convertRow(box.getId(), box.getValue());
			}
		});
		
		// sets style
		table.getRowFormatter().addStyleName(row, "tableRow");
				
		// sets focus
		customNameBox.setFocus(true);
		
		updateSummary();
		
	} // end addCustomFoodEntry()
	
	
	/**
	 * Overloaded add food entry that adds the entry to the end of the table.
	 * @param fe
	 */
	public void addFoodEntry(FoodEntry fe)
	{
		addFoodEntry(table.getRowCount(), fe);
		
	} // end addFoodEntyr()
	
	
	/**
	 * Removes the row at the specified index.
	 * @param row
	 */
	public void removeRow(int row)
	{
		if (row < 0 || row > (table.getRowCount() - 1))
			return;
		table.removeRow(row);
		updateSummary();
		
	} // end removeRow()
	
	
	/**
	 * Clears all rows from table
	 */
	@Override
	public void clear()
	{
		foodEntries.clear();
		int count = table.getRowCount();
		while (count > 0)
		{
			table.removeRow(0);
			count = table.getRowCount();
		}
		
	} // end clear()
	
	
	/**
	 * Converts the row in the table to either a auto fill food selection row,
	 * or into a manual entering of foods row
	 * @param row
	 * @param type: TRUE: custom; FALSE: standard
	 */
	private void convertRow(int row, boolean type)
	{
		if (type)
		{
			addCustomFoodEntry(row, new FoodEntry());
		}
		else
		{
			addFoodEntry(row, new FoodEntry(foodList.get(0)));
		}	
		
	} // end convertRow
	
	
	/**
	 * Gets a list of all the food entries in the table
	 * @return
	 */
	public FoodEntries getFoodEntries()
	{
		FoodEntries fes = new FoodEntries();
		
		// cycles through all data rows, creating food entries from the cell data
		for (int row = 0; row < table.getRowCount(); ++row)
		{
			double[] measurements = new double[Measurement.values().length];
			int col = 0;
			double units = 0;
			String name = "";
			
			// checks if it is a custom row and will pull in name accordingly
			if (isRowCustom(row))
			{
				name = ((TextBox) (table.getWidget(row,  col++))).getText();
			}
			else
			{
				FoodListBox nameBox = ((FoodListBox) (table.getWidget(row,  col++)));
				name = nameBox.getValue(nameBox.getSelectedIndex());
			}
						
			// gets editable cells values first);
			units = Double.parseDouble(((DoubleTextBox) (table.getWidget(row,  col++))).getText());
			
			// retrieves measurements 
			for (Measurement msm : Measurement.values())
			{
				// pulls in measurement data based on if the row is custom or down
				if (isRowCustom(row))
					measurements[col - 2] = Double.parseDouble(((DoubleTextBox)table.getWidget(row,  col)).getText());
				else
					measurements[col - 2] = Double.parseDouble(table.getText(row,  col));
				col++;
			}
			
			// adds new food entry to the list
			fes.add(new FoodEntry(name, "", units, measurements));
		}
		
		return fes;
		
	} // end getLogEntry()
	
	
	/**
	 * Returns the current food list of this module.
	 * @return
	 */
	public FoodList getFoodList()
	{
		return foodList;
		
	} // end getFoodList()
	
	
	/**
	 * Returns the count of the table rows
	 * @return
	 */
	public int getRowCount()
	{
		return table.getRowCount();
		
	} // end getRowCount()
	
	
	/**
	 * Checks if the passed in row is a custom row.
	 * @param row
	 * @return
	 */
	public boolean isRowCustom(int row)
	{
		CustomCheckBox box = ((CustomCheckBox)table.getWidget(row,  table.getCellCount(row) - 1));
		return box.getValue();
		
	} // end isRowCustom()

	
	/**
	 * Updates the values of the specified row based on food item values and 
	 * the units entered.
	 */
	private void updateRow(int row)
	{
		// doesn't update if it is a custom row
		CustomCheckBox box = ((CustomCheckBox)table.getWidget(row,  table.getCellCount(row) - 1));
		if (box.getValue() == true)
			return;
		
		// retrieves units value and updates all measurements accordingly based on original food item data
		
		// locates food item
		int col = 0;		
		FoodListBox nameBox = ((FoodListBox) (table.getWidget(row,  col++)));
		String name = nameBox.getValue(nameBox.getSelectedIndex());
		double units = Double.parseDouble(((DoubleTextBox) (table.getWidget(row,  col++))).getText());
		
		Food food = foodList.getFood(name);
		if (null == food)
			return;
		
		// updates measurement values by multiplying base with the units specified
		FoodEntry fe = foodEntries.get(row);
		fe.setFood(food);
		for (Measurement msm : Measurement.values())
		{
			double value = units * food.getMeasurement(msm);
			fe.setMeasurement(msm,  value);
			table.setText(row,  col, Double.toString(value)); // units * base measurement value for that food
			col++;
		}
		
		// refresh units tooltip box
		((DoubleTextBox)table.getWidget(row,  1)).setToolTip(fe.getUnit());
		
		// update summary
		updateSummary();
		
	} // end updateRow()
	
	
	/**
	 * Applies the current food list to the passed in listbox.
	 * @param nameBox
	 */
	private static void setFoodListToListBox(ListBox nameBox)
	{
		for (Food food : foodList)
		{
			nameBox.addItem(food.getName());
		}
		
	} // end setFoodListToListBox()

	
	/**
	 * Updates the food entries with correct food information.
	 */
	public void updateFoodEntries()
	{
		// updates entries
		for (int row = 0; row < table.getRowCount(); ++row)
		{
			updateRow(row);
		}
		
		// updates summary just in case there were no rows to update
		updateSummary();
		
	} // end setFoodNameColumn()
	
	
	/**
	 * Updates the food list and refreshes all food name combo boxes.
	 */
	public void setFoodList(FoodList fl)
	{
		foodList = fl;
				
	} // end setFoodList()
		
	
	
	///////////////////////////////////////////////////////////////////////////
	// Event Listeners
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Focus lost event for the editable cells of the table.
	 */
	@Override
	public void onBlur(BlurEvent event) 
	{
		try
		{
			if (event.getSource() instanceof DoubleTextBox)
				updateRow(((DoubleTextBox)event.getSource()).getId());
		}
		catch (Exception e)
		{
			ExceptionWindow.Error("FoodEntryTable.onBlur()", e);
		}
		
	} // end onBlur()


	/**
	 * Change event handler for the food list box
	 */
	@Override
	public void onChange(ChangeEvent event)
	{
		if (event.getSource() instanceof FoodListBox)
			updateRow(((FoodListBox)event.getSource()).getId());		
	}	
	
} // end class FoodEntryTable
