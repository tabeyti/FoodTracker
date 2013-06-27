package com.tla.foodtracker.client.shared;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DoubleTextBox extends TextBox 
{
	private int id = -1;
	private boolean decimal = false;
	private String oldValue = "0.0";
	private Label tooltip;
	private PopupPanel popup;
	private boolean ttActive = false;
	
	
	/**
	 * CONSTRUCTOR
	 */
	public DoubleTextBox()
	{
		popup = new PopupPanel(true);
		tooltip = new Label("");
		popup.add(tooltip);
		
		// focus lost event
		this.addBlurHandler(new BlurHandler()
		{
			@Override
			public void onBlur(BlurEvent event) 
			{
				String cellValue = DoubleTextBox.this.getText(); 
				
				// remove any leading zeros
				if (cellValue.length() > 1 && cellValue.charAt(0) == '0')
				{
					cellValue = cellValue.replaceFirst("^0+(?!$)", "");
					DoubleTextBox.this.setText(cellValue);
				}
				
				// verifies a valid number was entered into the box
				if (!cellValue.matches("^[0-9]*[\\.]?[0-9]+$"))
				{
					DoubleTextBox.this.setText(oldValue);
					return;
				}		
			}
			
		});
		
		this.addFocusHandler(new FocusHandler()
		{

			@Override
			public void onFocus(FocusEvent event) 
			{
				if (DoubleTextBox.this.getText().trim() == "")
					return;				
				// grabs old value
				oldValue = DoubleTextBox.this.getText();
			}
			
		});
		this.addMouseOverHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(MouseOverEvent event)
			{
				if (!ttActive)
					return;
				
	            Widget source = (Widget) event.getSource();
	            int x = source.getAbsoluteLeft() + 10;
	            int y = source.getAbsoluteTop() + 10;

	            popup.setPopupPosition(x, y);
	            popup.show();
			}
		});
		this.addMouseOutHandler(new MouseOutHandler()
		{
			@Override
			public void onMouseOut(MouseOutEvent event)
			{
				popup.hide();
			}	
		});
		
	} // end DoubleTextBox()

	
	public void setToolTip(String tt)
	{
		ttActive = true;
		tooltip.setText(tt);
		
	} // end setToolTip
	

	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}

} // end class DoubleTextBox
