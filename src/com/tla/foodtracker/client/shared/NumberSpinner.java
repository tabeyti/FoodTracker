package com.tla.foodtracker.client.shared;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;

/**
* NumberSpinner Custom Control
* 
* @author Pavan Andhukuri
* 
*/
public class NumberSpinner extends Composite implements HasChangeHandlers
{
    private int RATE = 1;
    private IntegerBox integerBox;
    private Button upButton;
    private Button downButton;

    public NumberSpinner() {
        this(1);
    }

    public NumberSpinner(int defaultValue) {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        initWidget(absolutePanel);
        absolutePanel.setSize("55px", "23px");

        integerBox = new IntegerBox();
        absolutePanel.add(integerBox, 0, 0);
        integerBox.setSize("30px", "16px");
        integerBox.setValue(defaultValue);

        upButton = new Button();
        upButton.addClickHandler(new ClickHandler() 
        {
            public void onClick(ClickEvent event) 
            {
                setValue(getValue() + RATE);
                fireChange();
            }
        });
        upButton.setStyleName("dp-spinner-upbutton");

        absolutePanel.add(upButton, 34, 1);
        upButton.setSize("12px", "10px");

        downButton = new Button();
        downButton.addClickHandler(new ClickHandler() 
        {
            public void onClick(ClickEvent event) 
            {
                if (getValue() == 0)
                    return;
                setValue(getValue() - RATE);
                fireChange();
            }
        });
        downButton.setStyleName("dp-spinner-downbutton");
        absolutePanel.add(downButton, 34, 11);
        downButton.setSize("12px", "10px");
    }

    /**
     * Returns the value being held.
     * 
     * @return
     */
    public int getValue() {
        return integerBox.getValue() == null ? 0 : integerBox.getValue();
    }

    /**
     * Sets the value to the control
     * 
     * @param value
     *            Value to be set
     */
    public void setValue(int value) {
        integerBox.setValue(value);
    }

    /**
     * Sets the rate at which increment or decrement is done.
     * 
     * @param rate
     */
    public void setRate(int rate) {
        this.RATE = rate;
    }
    
    
    ///
    /// Additions made by Travis Abeyti
    ///

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler)
	{
		return addDomHandler(handler, ChangeEvent.getType());
	}
	
	private void fireChange()
	{
		NativeEvent nativeEvent = Document.get().createChangeEvent();
	    ChangeEvent.fireNativeEvent(nativeEvent, this);
	}
	
	public void setEnabled(boolean value)
	{
		upButton.setEnabled(value);
		downButton.setEnabled(value);
		integerBox.setEnabled(value);
	}
	    
} // end class NumberSpinner