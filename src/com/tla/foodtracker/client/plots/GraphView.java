package com.tla.foodtracker.client.plots;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.NumberSpinner;
import com.tla.foodtracker.shared.Measurement;

public class GraphView extends VerticalPanel
{
	private Graph graph;
	private MetricsPanel metricsPanel;
	private NumberSpinner rangeSpinner;
	private ListBox measurementBox;
	private ArrayList<RadioButton> selectionButtons = new ArrayList<RadioButton>();	
	
	
	/**
	 * CONSTRUCTOR
	 */
	public GraphView()
	{
		//super(Unit.PX);
		
		graph = new Graph();
		
		// creates button panel and adds radio button selections in a 2 column
		// format
		FlexTable buttonTable = new FlexTable();
		int col = 0;
		int row = 0;
		
		// creates list box for all displayable measurements
		measurementBox = new ListBox();
		measurementBox.setStyleName("dropDownBox");
		for (Measurement msm : Measurement.values())
			measurementBox.addItem(msm.toString());
		
		Label measurementLabel = new Label("Measurement");
		measurementLabel.setStyleName("sectionTitle");
		buttonTable.setWidget(0,  0,  measurementLabel);
		buttonTable.setWidget(0,  1,  measurementBox);
		
		// sets up metrics panel
		metricsPanel = new MetricsPanel();
		metricsPanel.setStyleName("metricsPanel");
		
		HorizontalPanel rangePanel = new HorizontalPanel();
		rangePanel.setSpacing(5);
		rangeSpinner = new NumberSpinner(graph.getRange(), 2, 10);
		rangeSpinner.setEnabled(false);
		Label rangeLabel = new Label("Range");
		rangeLabel.setStyleName("sectionTitle");
		rangePanel.add(rangeLabel);
		rangePanel.add(rangeSpinner);
		
		// creates lower graph control panel
		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.add(buttonTable);
		bottomPanel.add(rangePanel);
		bottomPanel.setHeight("20%");
		
		// creates main display panel hosting graph and metrics
		HorizontalPanel bodyPanel = new HorizontalPanel();
		bodyPanel.add(graph);
		bodyPanel.add(metricsPanel);

		//this.addSouth(bottomPanel,  250);
		this.add(bodyPanel);
		this.add(bottomPanel);
		
		rangeSpinner.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				graph.setRange(rangeSpinner.getValue());
				graph.plotMeasurement(Measurement.findEnum(measurementBox.getValue(measurementBox.getSelectedIndex())));
			}
		});
		measurementBox.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				rangeSpinner.setEnabled(true);
				graph.plotMeasurement(Measurement.findEnum(measurementBox.getValue(measurementBox.getSelectedIndex())));				
			}	
		});
		
	} // end  GraphView()
	
	
	private String getSelection()
	{
		for (RadioButton rb : selectionButtons)
		{
			if (rb.getValue() == true)
			{
				return rb.getText();
			}
		}
		
		return "";
		
	} // end getSelection()
	
	
	public void refresh()
	{
		// refreshes the last graph view prior to leaving the tab
		String selection = getSelection();
		 if ("".equals(selection))
			 return;
		 
		 graph.plotMeasurement(Measurement.findEnum(getSelection()));
		
	} // end refresh()

} // end class GraphView
