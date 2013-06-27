package com.tla.foodtracker.client.shared;

import java.util.ArrayList;
import java.util.List;

public class LogEntry 
{
	private FoodEntries fes = new FoodEntries();
	private String date;
	private Workout workout = Workout.NONE;
	
	
	/**
	 * CONSTRUCTOR
	 * @param date
	 */
	public LogEntry(String date)
	{
		this.date = date;
		
	} // end LogEntry()
	
	public LogEntry()
	{
		this.date = "01-01-1969";
		
	} // end LogEntry()
	
	
	public void addFoodEntry(FoodEntry fe)
	{
		fes.add(fe);
		
	} // end addFoodEntry()
	
	public void clearFoodEntries()
	{
		fes.clear();
	}
	
	public String getDate()
	{
		return date;		
	}
	
	public FoodEntries getFoodEntries()
	{
		return fes;
		
	}
	
	public Workout getWorkout()
	{
		return workout;
		
	} // getWorkout()
	
	public void setWorkout(Workout workout)
	{
		this.workout = workout;
		
	} // setWorkout()
	
	public void setFoodEntries(FoodEntries fes)
	{
		this.fes = fes;
	}


	public void setDate(String date)
	{
		this.date = date;
	}
	
} // end class LogEntry
