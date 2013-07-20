package com.tla.foodtracker.client.shared;

import java.util.ArrayList;
import java.util.Collections;


public class FoodList extends ArrayList<Food>
{
	
	/**
	 * Returns the food object associated with the food name.
	 * @param foodName
	 * @return
	 */
	public Food getFood(String foodName)
	{
		for (Food food : this)
		{
			if (food.getName().equals(foodName))
				return food;
		}
		
		return null;
		
	} // return getFood()
	
	
	/**
	 * Sorts the food list based on the comparitor implementation of the food
	 */
	public void sort()
	{
		Collections.sort(this);
		
	} // end sort()

} // end class FoodList
