package com.tla.foodtracker.client.dayentry;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class DayBar extends HorizontalPanel
{
	private FlexTable table;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public DayBar(int day)
	{
		table = new FlexTable();
		table.setStyleName("dayBarTable");
		
		// sets up days
		int col = 0;
		table.setWidget(0, col++, new Label("Sunday"));
		table.setWidget(0, col++, new Label("Monday"));
		table.setWidget(0, col++, new Label("Tuesday"));
		table.setWidget(0, col++, new Label("Wednesday"));
		table.setWidget(0, col++, new Label("Thursday"));
		table.setWidget(0, col++, new Label("Friday"));
		table.setWidget(0, col++, new Label("Saturday"));		
		
		setDay(day);
		
		this.add(table);
		
	} // end DayBar()
	
	
	/**
	 * Highlights the day corresponding the to the passed in index:
	 * 0: Sunday 1: Monday ...6: Saturday
	 * @param day
	 */
	public void setDay(int day)
	{
		if (day > 6 || day < 0)
			return;
		
		for (int col = 0; col < 7; col++)
		{
			Label dayLabel = ((Label) (table.getWidget(0,  col)));
			
			if (day == col)
				dayLabel.setStyleName("dayBarSelectedDay");
			else
				dayLabel.setStyleName("dayBarUnselectedDay");
		}
		
	} // end setDay()
	
	
	public void setDay(String day)
	{
		if (day.equals("Sunday"))
			setDay(0);
		else if (day.equals("Monday"))
			setDay(1);
		else if (day.equals("Tuesday"))
			setDay(2);
		else if (day.equals("Wednesday"))
			setDay(3);
		else if (day.equals("Thursday"))
			setDay(4);
		else if (day.equals("Friday"))
			setDay(5);
		else if (day.equals("Saturday"))
			setDay(6);
		else
			return;
		
	} // end setDay()
	

} // end class DayBar
