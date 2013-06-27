package com.tla.foodtracker.client.shared;

import com.tla.foodtracker.shared.Measurement;

public class Goals extends Food // since food as all measurement shizz (hacky i know, need better class heiarchy)
{
	private String notes = "";
	
	public String getNotes() 
	{
		return notes;
	}
	public void setNotes(String notes) 
	{
		this.notes = notes;
	}
		
} // end class Goals
