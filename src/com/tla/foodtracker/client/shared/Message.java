package com.tla.foodtracker.client.shared;

import java.io.Serializable;

public class Message implements Serializable
{
	private String message;
	
	
	public Message(){};
	public Message(String msg)
	{
		this.message = msg;
		
	} // end Message()
	
	
	public void setMessage(String message)
	{
		this.message = message;
		
	} // end setMessage()
	
	public String getMessage()
	{
		return this.message;
	}

} // end class Message
