package com.tla.foodtracker.client.dayentry;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class OverviewPanel extends DockLayoutPanel
{
	private FlexTable table;
	
	/**
	 * CONSTRUCTOR
	 */
	public OverviewPanel()
	{
		super(Unit.PX);
		
		// initializes table with titles and default values
		table = new FlexTable();
		table.setStyleName("subTable");
		table.setWidth("100%");
		
	} // end OverviewPanel()

} // end class OverviewPanel
