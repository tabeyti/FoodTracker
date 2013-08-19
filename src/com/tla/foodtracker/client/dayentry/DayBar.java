package com.tla.foodtracker.client.dayentry;

import java.util.Date;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;
import com.tla.foodtracker.shared.Measurement;

public class DayBar extends HorizontalPanel implements HasChangeHandlers, ClickHandler
{
	private FlexTable table;
	private Button leftArrowDateButton;
	private Button rightArrowDateButton;
	private static DateBox dateBox;
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM-dd-yyyy");
	private static DateTimeFormat dayFormat = DateTimeFormat.getFormat("EEEE");	
	
	// TEMP
	private boolean fired = false;
	
	/**
	 * CONSTRUCTOR
	 */
	public DayBar(int day)
	{
		HorizontalPanel dateChooserBar = new HorizontalPanel();
		dateChooserBar.setSpacing(5);
	    dateBox = new DateBox();
	    dateBox.setValue(new Date());
	    dateBox.getTextBox().setReadOnly(true);
	    dateBox.setFormat(new DateBox.DefaultFormat(dateFormat));
	    leftArrowDateButton = new Button("<<");
	    leftArrowDateButton.setStyleName("flatButton");
	    rightArrowDateButton = new Button(">>");
	    rightArrowDateButton.setStyleName("flatButton");
	    dateChooserBar.add(leftArrowDateButton);
	    dateChooserBar.add(dateBox);
	    dateChooserBar.add(rightArrowDateButton);
		
		table = new FlexTable();
		table.setStyleName("dayBarTable");
		
		// sets up days
		int col = 0;
		Anchor sundayLabel = new Anchor("Sunday");
		Anchor mondayLabel = new Anchor("Monday");
		Anchor tuesdayLabel = new Anchor("Tuesday");
		Anchor wednesdayLabel = new Anchor("Wednesday");
		Anchor thursdayLabel = new Anchor("Thursday");
		Anchor fridayLabel = new Anchor("Friday");
		Anchor saturdayLabel = new Anchor("Saturday");
			
		table.setWidget(0, col++, sundayLabel);
		table.setWidget(0, col++, mondayLabel);
		table.setWidget(0, col++, tuesdayLabel);
		table.setWidget(0, col++, wednesdayLabel);
		table.setWidget(0, col++, thursdayLabel);
		table.setWidget(0, col++, fridayLabel);
		table.setWidget(0, col++, saturdayLabel);	
		dateChooserBar.add(table);
		
		setDay(day);
		
		this.add(dateChooserBar);
		
		// sets up event listeners
		sundayLabel.addClickHandler(this);
		mondayLabel.addClickHandler(this);
		tuesdayLabel.addClickHandler(this);
		wednesdayLabel.addClickHandler(this);
		thursdayLabel.addClickHandler(this);
		fridayLabel.addClickHandler(this);
		saturdayLabel.addClickHandler(this);	
		leftArrowDateButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				incDecDate(false);				
			}
			
		});
		rightArrowDateButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				incDecDate(true);				
			}
			
		});
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Date> event)
			{
				if (fired)
					return;
				fired = true;
				setDay(dayFormat.format(dateBox.getValue()));
				fireChange();
			}
		});
		
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
			Anchor dayLabel = ((Anchor) (table.getWidget(0,  col)));
			
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
	
	
	/**
	 * Increments or decrements the date based on the passed in boolean.
	 * @param inc
	 */
	private void incDecDate(boolean inc)
	{
		Date date = dateBox.getValue();
		
		if (inc)
			date.setDate(date.getDate() + 1);
		else
			date.setDate(date.getDate() - 1);
		
		setDate(date);
		fireChange();
		
	} // end incDecDate()
	
	public Date getDate()
	{
		return dateBox.getValue();
		
	} // end getDate()
	
	
	public void setFired(boolean fired)
	{
		this.fired = fired;
		
	} // end setFired()
	

	/**
	 * Sets the passed date into the date box and sets the day.
	 * @param date
	 */
	private void setDate(Date date)
	{
		dateBox.setValue(date);
		setDay(dayFormat.format(date));
		
	} // end setDate()
		
	
	@Override
	public void onClick(ClickEvent event)
	{
		Anchor dayLabel = (Anchor)event.getSource();
		
		// get day int of the currently selected date and the clicked date
		Date currentDate = dateBox.getValue();
		int currentDay = Day.getInt(dayFormat.format(currentDate));
		int clickedDay = Day.getInt(dayLabel.getText());
				
		if (currentDay == clickedDay || currentDay == -1 || clickedDay == -1)
			return;
		
		// moves current date to clicked date based on the day difference
		currentDate.setDate(currentDate.getDate()  + (clickedDay - currentDay));
		setDate(currentDate);
		fireChange();
		
	} // end onClick()
	
	
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
	
	
	///////////////////////////////////////////////////////////////////////////
	// INNER CLASSES
	///////////////////////////////////////////////////////////////////////////
	private enum Day
	{
		SUNDAY(0, "Sunday"),
		MONDAY(1, "Monday"),
		TUESDAY(2, "Tuesday"),
		WEDNESDAY(3, "Wednesday"),
		THURSDAY(4, "Thursday"),
		FRIDAY(5, "Friday"),
		SATURDAY(6, "Saturday");
		
		private final int dayInt;
		private final String dayStr;
		Day(int dayInt, String dayStr)
		{
			this.dayInt = dayInt;
			this.dayStr = dayStr;
		}
		
		public String toString()
		{
			return dayStr;
		}
		
		public static int getInt(String value)
		{
			for (Day day : Day.values())
			{
				if (day.toString().equals(value))
					return day.dayInt;
			}
			
			return -1;
		}
		
		
		
	} // end enum Day	

} // end class DayBar
