package com.tla.foodtracker.shared;

public enum Unit
{
	UNIT("Unit"),
	CALORIE("Calorie"),
	GRAM("Gram"),
	MILLIGRAM("Milligram");
	
	private final String str;
	Unit(String str)
	{
		this.str = str;
	}
	
	public String toString()
	{
		return str;
	}
	
	public static Unit findEnum(String value)
	{
		for (Unit unt : Unit.values())
		{
			if (unt.toString().equals(value))
				return unt;
		}
		
		return null;
	}
	
} // end enum Unit
