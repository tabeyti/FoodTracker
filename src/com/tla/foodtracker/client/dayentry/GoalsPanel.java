package com.tla.foodtracker.client.dayentry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.shared.Measurement;

public class GoalsPanel extends VerticalPanel
{
	private static FlexTable table; 
	private Button editButton;
	private static Goals goals = new Goals();
	
	
	/**
	 * CONSTRUCTOR
	 */
	public GoalsPanel()
	{
		editButton = new Button("Edit");
		editButton.setStyleName("goalsPanelEditButton");
		
		// initializes table with titles and default values
		table = new FlexTable();
		table.setStyleName("subTable");		
		table.setWidth("100%");
		
		table.setText(0, 0, "Goals");
		table.setWidget(0, 1, editButton);
		table.getRowFormatter().addStyleName(0,  "subTableHeader");
				
		int row = 1;		
		for (Measurement msm : Measurement.values())
		{
			table.setText(row, 0, msm.toString());
			table.setText(row, 1, "0");
			table.getRowFormatter().addStyleName(row, "subTableRow");
			row++;
		}
		table.setText(row,  0,  "Notes");
		table.setText(row,  1,  "");
		HTMLTable.CellFormatter formatter = table.getCellFormatter();
		formatter.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT);
		formatter.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
		
		
		this.add(table);
		
		// sets up event listeners
		editButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				EditGoalsDialog dlg = new EditGoalsDialog(goals);
			}
		});
		
	} // end GoalsPanel()
	
	
	/**
	 * Updates the table's values with the passed in goals.
	 * @param goals
	 */
	public static void loadGoals(Goals gls)
	{
		goals = gls;
		int row = 1;
		for (Measurement msm : Measurement.values())
		{
			table.setText(row++, 1, Double.toString(goals.getMeasurement(msm)));
		}
		
		// sets notes
		table.setText(row++, 1, gls.getNotes());
		
	} // end loadGoals()
	
} // end class GoalsPanel
