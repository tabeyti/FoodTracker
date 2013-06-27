package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPanel extends PopupPanel
{
	private static Image loadingImage = new Image("images/loading.png");
	
	
	/**
	 * CONSTRUCTOR
	 */
	public LoadingPanel()
	{		
		this.add(loadingImage);
		
		this.center();
		this.show();
		
	} // end LoadinPanel()

} // end class LoadingPanel
