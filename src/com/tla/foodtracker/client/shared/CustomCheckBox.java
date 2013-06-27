package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.ui.CheckBox;

public class CustomCheckBox extends CheckBox
{
	private int id = -1;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}


}
