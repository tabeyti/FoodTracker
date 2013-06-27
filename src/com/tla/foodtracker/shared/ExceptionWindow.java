package com.tla.foodtracker.shared;

import com.google.gwt.user.client.Window;

public class ExceptionWindow
{
	
	public static void Error(Exception e)
	{
		Window.alert(e.getMessage() + "\n\n" + e.getStackTrace());
	}
	
	public static void Error(String message, Exception e)
	{
		Window.alert(message + "\n\n" + e.getMessage() + "\n\n" + e.getStackTrace());
	}
	
	public static void UserAlert(Exception e)
	{
		Window.alert(e.getMessage());
	}
	
	public static void UserAlert(String message)
	{
		Window.alert(message);
	}


}
