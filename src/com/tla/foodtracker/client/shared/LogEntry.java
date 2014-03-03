package com.tla.foodtracker.client.shared;


public class LogEntry implements Comparable<LogEntry>
{
	private FoodEntries fes = new FoodEntries();
	private String date;
	private Workout workout = Workout.NONE;
	private String notes = "";
	
	
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
	
	public String getNotes()
	{
		return this.notes;
	}
	
	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	

	public void setDate(String date)
	{
		this.date = date;
	}

	@Override
	public int compareTo(LogEntry arg0)
	{
		return date.compareTo(arg0.date);
	}
	
	public double getFoodUnits(Food food)
	{
		for (FoodEntry fe : fes)
		{
			if (food.getName().equals(fe.getName()))
				return fe.getUnits();
		}
		return 0;
	}
	
} // end class LogEntry
