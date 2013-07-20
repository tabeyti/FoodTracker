package com.tla.foodtracker.client.plots;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.tla.foodtracker.client.shared.NumberSpinner;
import com.tla.foodtracker.shared.Measurement;

public class GraphView extends VerticalPanel implements ValueChangeHandler
{
	private Graph graph;
	private MetricsPanel metricsPanel;
	private NumberSpinner rangeSpinner;
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
		for (Measurement msm : Measurement.values())
		{
			RadioButton rb = new RadioButton("selectionGroup", msm.toString());
			selectionButtons.add(rb);
			buttonTable.setWidget(row, col++, rb);
			if (col == 1)
				buttonTable.setWidget(row++, col--, rb);
			
			// adds change listener to the radio button
			rb.addValueChangeHandler(GraphView.this);
		}
		
		// sets up metrics panel
		metricsPanel = new MetricsPanel();
		
		HorizontalPanel rangePanel = new HorizontalPanel();
		rangePanel.setSpacing(5);
		rangeSpinner = new NumberSpinner();
		rangeSpinner.setEnabled(false);
		rangeSpinner.setValue(graph.getRange());
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
				graph.plotMeasurement(Measurement.findEnum(getSelection()));
			}
		});
		
		// sets default view
//		selectionButtons.get(0).setValue(true);
//		graph.plotMeasurement(Measurement.findEnum(getSelection()));
		
	} // end  GraphView()

	
	@Override
	public void onValueChange(ValueChangeEvent event)
	{
		rangeSpinner.setEnabled(true);
		graph.plotMeasurement(Measurement.findEnum(getSelection()));
			
	} // end onValueChange()
	
	
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
