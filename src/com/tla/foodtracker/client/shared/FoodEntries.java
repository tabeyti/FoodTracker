package com.tla.foodtracker.client.shared;

import java.util.ArrayList;

import com.tla.foodtracker.shared.Measurement;

public class FoodEntries extends ArrayList<FoodEntry>
{
	public FoodEntries()
	{}
	
	
	/**
	 * Returns back the total quantity of a measurement for that day.
	 * @param msm
	 * @return the total value of the measurement specified
	 */
	public int calculateTotal(Measurement msm)
	{
		int total = 0;
		for (Food fd : this)
			total += fd.getMeasurement(msm);
		return total;
			
	} // end calculateTotal()

} // end class FoodEntries()
