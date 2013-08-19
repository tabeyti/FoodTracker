package com.tla.foodtracker.client.dayentry;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.FoodEntries;
import com.tla.foodtracker.shared.Measurement;

/**
 * This class provides a visual summary view of the day's total metrics and 
 * other shizznit.
 *
 */
public class NotesPanel extends VerticalPanel
{
	private FlexTable table;
	private TextArea notesArea;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public NotesPanel()
	{
		notesArea = new TextArea();
		notesArea.setStyleName("notesArea");
		notesArea.setWidth("100%");
		notesArea.setHeight("100%");
		
		table = new FlexTable();
		table.setStyleName("subTable");
		table.setWidth("100%");
		table.setHeight("100%");
		table.setText(0, 0,  "Day Notes");
		table.setWidget(1, 0, notesArea);
		table.getRowFormatter().addStyleName(0,  "subTableHeader");		
		
		this.add(table);
	    
	} // end NotesPanel()
	
	
	public String getText()
	{
		return notesArea.getText();
	}
	
	
	public void setText(String text)
	{
		notesArea.setText(text);
	}
	
} // end class NotesPanel