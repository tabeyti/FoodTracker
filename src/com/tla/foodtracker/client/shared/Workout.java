package com.tla.foodtracker.client.shared;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.tla.foodtracker.shared.Measurement;

public enum Workout 
{
	NONE("NONE"),
	LIFT("Lift"),
	CARDIO("Cardio");
	
	private final String str;
	Workout(String str)
	{
		this.str = str;
	}
	
	public String toString()
	{
		return str;
	}
	
	public static Workout findByValue(String value)
	{
		for (Workout wo : Workout.values())
		{
			if (value.equals(wo.toString()))
				return wo;
		}
		return null;
	}
		
} // end enum Workout