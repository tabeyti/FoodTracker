package com.tla.foodtracker.shared;

public enum Measurement 
{
	CALORIES("Calories"),
	PROTEIN("Protein"),
	CARBOHYDRATES("Carbohydrates"),
	FATS("Fats");
	
	private final String str;
	Measurement(String str)
	{
		this.str = str;
	}
	
	public String toString()
	{
		return str;
	}
	
	public static Measurement findEnum(String value)
	{
		for (Measurement msm : Measurement.values())
		{
			if (msm.toString().equals(value))
				return msm;
		}
		
		return null;
	}

} // end enum Measurement
