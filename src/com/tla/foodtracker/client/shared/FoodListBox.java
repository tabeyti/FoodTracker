package com.tla.foodtracker.client.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.ListBox;

public class FoodListBox extends ListBox
{
	int id = -1;

	
	public FoodListBox(boolean bl)
	{
		super(bl);
	}
	
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	
	/**
	 * Updates the list box's food list with the passed in list
	 * @param foodList
	 */
	public void updateFoodList(ArrayList<Food> foodList)
	{
		this.clear();
		for (Food food : foodList)
			this.addItem(food.getName());
		
	} // end updateFoodList()

} // end class FoodListBox