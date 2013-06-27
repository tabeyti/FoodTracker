package com.tla.foodtracker.client.shared;

import com.tla.foodtracker.shared.Measurement;

public class FoodEntry extends Food
{
	private double units;
	
	public FoodEntry(String name, String unt, double unts, double[] msms)
	{
		super(name, unt, msms);
		units = unts;
		
	} // end FoodEntry()
	
	public FoodEntry()
	{
		super("", "", new double[Measurement.values().length]);
		units = 1;
	}
	
	public FoodEntry(Food food)
	{
		super(food.getName(), food.getUnit(), new double[Measurement.values().length]);
		
		for (Measurement msm : Measurement.values())
			this.setMeasurement(msm,  food.getMeasurement(msm));
		
		units = 1;
		
	}
	
	public double getUnits()
	{
		return units;
	}
	
	public void setUnits(double units)
	{
		this.units = units;		
	}
	
	
	/**
	 * Takes in the passed food and copies over all food information.
	 * @param food
	 */
	public void setFood(Food food)
	{
		this.name = food.name;
		this.unit = food.unit;
		for (int index = 0; index < Measurement.values().length; ++index)
			this.measurements[index] = food.measurements[index];
		
	} // end updateFood()
	
} // end class FoodEntry
