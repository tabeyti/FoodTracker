package com.tla.foodtracker.client.dayentry;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.FoodEntries;
import com.tla.foodtracker.shared.Measurement;

/**
 * This class provides a visual summary view of the day's total metrics and 
 * other shizznit.
 *
 */
public class DaySummaryPanel extends VerticalPanel
{
	private FlexTable table;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public DaySummaryPanel()
	{
		table = new FlexTable();
		table.setWidth("100%");
		table.setStyleName("subTable");
		
		table.setText(0, 0, "Day Summary");
		table.setText(0, 1, "");
		table.getRowFormatter().addStyleName(0,  "subTableHeader");
		
		int row = 1;
		for (Measurement msm : Measurement.values())
		{
			table.setText(row, 0, msm.toString());
			table.setText(row, 1, "0");
			table.getRowFormatter().addStyleName(row, "subthisRow");
			row++;
		}

		this.add(table);
	    
	} // end DaySummaryPanel()
	
		

	/**
	 * Refreshes the summary with the update log entry values
	 */
	public void updateValues(FoodEntries fes)
	{
		int row = 1;
		for (Measurement msm : Measurement.values())
		{
			table.setText(row, 1, Double.toString(fes.calculateTotal(msm)));
			row++;
		}
				
	} // end updateValues()
		
} // end class DaySummaryPanel
