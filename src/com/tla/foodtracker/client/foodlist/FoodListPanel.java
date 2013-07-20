package com.tla.foodtracker.client.foodlist;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.tla.foodtracker.client.IView;
import com.tla.foodtracker.client.shared.DataManager;
import com.tla.foodtracker.client.shared.DoubleTextBox;
import com.tla.foodtracker.client.shared.ExceptionWindow;
import com.tla.foodtracker.client.shared.Food;
import com.tla.foodtracker.client.shared.FoodList;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.Measurement;

public class FoodListPanel extends DockLayoutPanel implements IView
{
	private static FlexTable table;
	private static FlexTable tableHeader;
	private Button addEntryButton;
	private Button saveButton;
	private static FoodList activeFoodList = new FoodList();
	
	
	/**
	 * CONSTRUCTOR
	 */
	public FoodListPanel()
	{
		super(Unit.PX);
		
		int col = 0;
		
		// inits main table
		table = new FlexTable();
		
		// sets up static header row
		col = 0;
		tableHeader = new FlexTable();
		tableHeader.setWidth("100%");
		
		initializeColumns();
		
		// table body style
		table.setStyleName("table");
		
		// table panel
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.setStyleName("tableBackground");
		tablePanel.setWidth("100%");
		tablePanel.add(table);
		
		// sets up button panel
		HorizontalPanel bottomPanel = new HorizontalPanel();
		addEntryButton = new Button("Add Food");
		saveButton = new Button("Save");		
		bottomPanel.add(addEntryButton);
		bottomPanel.add(saveButton);
		
		int bottomPanelHeight = Window.getClientHeight() / 3;
		
		this.addNorth(tableHeader, 30);
		this.addSouth(bottomPanel, bottomPanelHeight);
		this.add(tablePanel);
		
		
		// sets up event listeners
		addEntryButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				addFood(new Food());
			}
		});
		saveButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				saveFoodList();
			}
		});
		
		requestFoodList();
		
	} // end FoodListPanel()
	
	
	/**
	 * Initializes both table and table header column names and styles.
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
		tableHeader.setText(0, col, "Unit");
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

		// sets overall header style
		tableHeader.getRowFormatter().setStyleName(0,  "tableHeader");
		
	} // end initializeColumns()
	
	
	/**
	 * Adds the passed in food object to the table.
	 * @param food
	 */
	public static void addFood(Food food)
	{
		int row = table.getRowCount();
		int col = 0;
		
		// name of food
		TextBox name = new TextBox();
		name.setText(food.getName());
		name.setStyleName("inputBox");
		table.setWidget(row,  col++, name);
		
		// unit type (oz, lb, etc.)
		TextBox unitType = new TextBox();
		unitType.setText(food.getUnit());
		unitType.setStyleName("inputBox");
		table.setWidget(row,  col++, unitType);
		
		// measurements (cals, fats, etc.)
		for (Measurement msm : Measurement.values()) 
		{
			DoubleTextBox dtb = new DoubleTextBox();
			dtb.setText(Double.toString(food.getMeasurement(msm)));
			dtb.setStyleName("inputBox");
			table.setWidget(row,  col++,  dtb);
		}
		
		// remove button
		Button removeButton = new Button("x");
		removeButton.setStyleName("removeButton");
		removeButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				Cell cell = table.getCellForEvent(event);
				if (null != cell)
					table.removeRow(cell.getRowIndex());
			}
		});
		table.setWidget(row, col, removeButton);
		
		table.getRowFormatter().addStyleName(row, "tableRow");
		
	} // end addFood()
	
	
	/**
	 * Removes the food row from the table using the passed in row index.
	 * @param row
	 */
	public void removeFood(int row)
	{
		if (row < 0 || row >= table.getRowCount())
			return;
		
		// TODO: remove row using index
		
	} // end removeFood()
	
	
	/**
	 * Pulls all row data, creating a list of food items to be sent back to the 
	 * user.
	 * @return
	 */
	private static FoodList getFoodList()
	{
		FoodList foods = new FoodList();
				
		for (int row = 0; row < table.getRowCount(); ++row)
		{
			double[] measurements = new double[Measurement.values().length];
			int col = 0;
			
			// name
			String name = ((TextBox) (table.getWidget(row,  col++))).getText();
			String unit = ((TextBox) (table.getWidget(row,  col++))).getText();
			
			// unit type
			
			// retrieves measurements 
			for (Measurement msm : Measurement.values())
			{
				measurements[col - 2] = Double.parseDouble(((DoubleTextBox) (table.getWidget(row,  col))).getText());
				col++;
			}
			
			// adds new food entry to the list
			foods.add(new Food(name, unit, measurements));
		}
		
		return foods;
		
	} // end getFoodList()
	
	
	/**
	 * Loads the passed in food list to the table.
	 * @param fl
	 */
	public static void loadFoodList(FoodList fl)
	{
		int count = table.getRowCount();
		while (count > 1)
		{
			table.removeRow(0);
			count = table.getRowCount();
		}
		
		activeFoodList = fl;
		for (Food food : fl)
			addFood(food);
		
	} // end loadFoodList()
	
	
	/**
	 * Clears all rows from table
	 */
	@Override
	public void clear()
	{
		int count = table.getRowCount();
		while (count > 1)
		{
			table.removeRow(0);
			count = table.getRowCount();
		}
		
	} // end clear()
	
	
	/**
	 * Requests the food list from the server.
	 */
	private static void requestFoodList()
	{
		try 
		{
			DataManager.requestFoodList(Destination.FOOD_LIST);
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}
		
	}  // requestFoodList()
	
	
	/**
	 * Pulls all foods from the table and attempts to store it on the server.
	 */
	private void saveFoodList()
	{
		FoodList fl = getFoodList();

		// saves log entry
		try 
		{
			fl.sort();
			DataManager.setFoodList(fl);
			activeFoodList = fl;
		} 
		catch (Exception e) 
		{
			ExceptionWindow.Error(e);
		}		

	} // end saveFoodList()


	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub
		
	}

} // end class FoodTable
