package com.tla.foodtracker.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.dayentry.DayEntryPanel;
import com.tla.foodtracker.client.foodlist.FoodListPanel;
import com.tla.foodtracker.client.plots.GraphView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FoodTracker implements EntryPoint 
{
	private DockLayoutPanel mainPanel;
	private TabLayoutPanel tabPanel;
	private DayEntryPanel dayEntryPanel;
	private FoodListPanel foodListPanel;
	private GraphView	graphPanel;
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		LoginBox lb = new LoginBox(this);
		lb.setStyleName("loginBox");
		lb.getElement().getStyle().setMarginLeft(10, Unit.PX);
		lb.getElement().getStyle().setMarginTop(10, Unit.PX);
		
		VerticalPanel dlp = new VerticalPanel();
		dlp.setBorderWidth(1);
		dlp.setWidth("100%");
		dlp.setHeight("100%");
		dlp.add(lb);
				
		RootLayoutPanel.get().add(dlp);
		
	} // end onModuleLoad()
	
	
	public void loadApp()
	{
		RootLayoutPanel.get().clear();
		
		mainPanel = new DockLayoutPanel(Unit.PX);
		mainPanel.setStyleName("mainPanel");
		
		// individual views
		dayEntryPanel = new DayEntryPanel();
//		dayEntryPanel.setWidth("100%");
		dayEntryPanel.setStyleName("tab");
		foodListPanel = new FoodListPanel();
//		foodListPanel.setWidth("100%");
		foodListPanel.setStyleName("tab");
		graphPanel = new GraphView();
		graphPanel.setWidth("100%");
		graphPanel.setStyleName("tab");
		
		tabPanel = new TabLayoutPanel(30, Unit.PX);
		tabPanel.add(dayEntryPanel, "Day Entry");
		tabPanel.add(foodListPanel, "Food List");
		tabPanel.add(graphPanel, "Plots");
		
		HorizontalPanel bannerBar = new HorizontalPanel();
		bannerBar.setStyleName("banner");
		Label title = new Label("Nutrition Tracker");
		title.setStyleName("bannerText");
		bannerBar.add(title);
		
		
		mainPanel.addNorth(bannerBar,  50);
		mainPanel.add(tabPanel);
				
		RootLayoutPanel.get().add(mainPanel);
		
		// sets up tab change listeners to refresh each page when selected
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() 
		{
			 @Override
			public void onSelection(SelectionEvent<Integer> event) 
			{
				if (event.getSelectedItem() == 0)
					dayEntryPanel.refresh();
				else if (event.getSelectedItem() == 2)
					graphPanel.refresh();
			}

		});
	}
	
} // end class FoodTracker
