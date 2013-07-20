package com.tla.foodtracker.client.dayentry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.DataManager;
import com.tla.foodtracker.client.shared.DoubleTextBox;
import com.tla.foodtracker.client.shared.ExceptionWindow;
import com.tla.foodtracker.client.shared.Goals;
import com.tla.foodtracker.shared.Measurement;

public class EditGoalsDialog extends DialogBox
{
	private Button closeButton;
	private Button saveButton;
	private VerticalPanel dialogContents;
	private FlexTable table;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public EditGoalsDialog(Goals goals)
	{
		// sets main panel properties
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
	    this.setText("Goals");
	    
	    // inits the goals table
	    table = new FlexTable();
	    int row = 0;
	    for (Measurement msm : Measurement.values())
	    {
	    	table.setText(row, 0, msm.toString());
	    	DoubleTextBox textBox = new DoubleTextBox();
	    	textBox.setText(Double.toString(goals.getMeasurement(msm)));
	    	table.setWidget(row++, 1, textBox);
	    }
	    
	    TextArea notesBox = new TextArea();
	    notesBox.setText(goals.getNotes());
	    table.setText(row, 0, "Notes");
	    table.setWidget(row++, 1, notesBox);
	    
	    // sets up button control panel
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    closeButton = new Button("Close");
	    saveButton = new Button("Save");
	    buttonPanel.add(closeButton);
	    buttonPanel.add(saveButton);
	    
	    dialogContents = new VerticalPanel();
	    dialogContents.add(table);
	    dialogContents.add(buttonPanel);
	    
	    this.add(dialogContents);
	    
	    // sets up event listeners
	    closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				// close window
				EditGoalsDialog.this.hide();
			}
			
		});
	    saveButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Goals goals = EditGoalsDialog.this.getGoals();
				
				// save goals
				try 
				{
					DataManager.setGoals(goals);
				} 
				catch (Exception e) 
				{
					ExceptionWindow.Error(e);
				}
				
				// update goals panel
				DayEntryPanel.requestGoals();				
			}
			
		});
	    
	    this.center();
		this.show();
		
	} // end EditGoalsDialog()
	
	
	/**
	 * Creates a goals object from the input data in the window.
	 * @return
	 */
	public Goals getGoals()
	{
		Goals goals = new Goals();
		
		// retrieves measurements
		int row = 0;
		for (Measurement msm : Measurement.values())
			goals.setMeasurement(msm,  Double.parseDouble(((DoubleTextBox)table.getWidget(row++,  1)).getText()));
		
		// retrieves notes
		goals.setNotes(((TextArea)table.getWidget(row++,  1)).getText());
		
		return goals;
				
	} // end getGoals()
	
} // end class EditGoalsDialog
