package com.tla.foodtracker.client.shared;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.FileType;
import com.tla.foodtracker.shared.Globals;
import com.tla.foodtracker.shared.Measurement;


public class DataManager
{
	// log entry xml values
	static final String LOG_ENTRY = "LogEntry";
	static final String FOOD_ENTRY = "FoodEntry";
	static final String NAME = "Name";
	static final String UNITS = "Units";
	static final String WORKOUT = "Workout";
	static final String DATE = "Date";
	
	// food list xml values
	static final String FOOD_LIST = "FoodList";
	static final String FOOD = "Food";
	static final String UNIT = "Unit";
	
	// goals xml values
	static final String GOALS = "Goals";
	static final String NOTES = "Notes";
	
	private static DataCallBack dataCallBack;
	
	
	/**
	 * Creates an xml representation of the passed in log entry and saves it to the server.
	 * @param le
	 * @throws Exception
	 */
	public static void setLogEntry(LogEntry le) throws Exception
	{
		Document doc = XMLParser.createDocument();
		Element root;
		Element foodEntryElem;
		Element child;
		Element workoutElem;
	   		
		// root element
		root = doc.createElement(LOG_ENTRY);
		doc.appendChild(root);
		
		Element dateElem = doc.createElement(DATE);
		dateElem.appendChild(doc.createTextNode(le.getDate()));
		root.appendChild(dateElem);

		// inner elements
		for (FoodEntry fe : le.getFoodEntries())
		{
			// food entry
			foodEntryElem = doc.createElement(FOOD_ENTRY);
			
			// name a units
			child = doc.createElement(NAME);
			child.appendChild(doc.createTextNode(fe.getName()));
			foodEntryElem.appendChild(child);
			child = doc.createElement(UNITS);
			child.appendChild(doc.createTextNode(Double.toString(fe.getUnits())));
			foodEntryElem.appendChild(child);
			
			// all measurements
			for (Measurement msm : Measurement.values())
			{
				child = doc.createElement(msm.toString());
				child.appendChild(doc.createTextNode(Double.toString(fe.getMeasurement(msm))));
				foodEntryElem.appendChild(child);				
			}
					
			// close food entry
			root.appendChild(foodEntryElem);
		}
		
		// workout data
		workoutElem = doc.createElement(WORKOUT);
		workoutElem.appendChild(doc.createTextNode(le.getWorkout().toString()));
		root.appendChild(workoutElem);
		
		// send byte stream to the server
		RPCInterfaceAsync rpci = GWT.create(RPCInterface.class);
		rpci.sendFile(doc.toString().getBytes(), le.getDate(), FileType.LOGENTRY, new MessageCallBack());
		
	} // end setLogEntry()
	
	
	/**
	 * Requests a log entry from the server.
	 * @param key
	 */
	public static void requestLogEntry(String key, Destination dest)
	{		
		dataCallBack = new DataCallBack(FileType.LOGENTRY, dest);
		
		// get byte stream from the server
		RPCInterfaceAsync rpci = GWT.create(RPCInterface.class);
		rpci.getFile(key, FileType.LOGENTRY, dataCallBack);
		
	} // end requestLogEntry()
	

	/**
	 * Parses the log entry from the received Data object. DONE
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static LogEntry parseLogEntry(Data data) throws Exception
	{
		// creates empty log entry
		LogEntry logEntry = new LogEntry(data.getFileName());
		
		if (null == data.getData())
			return logEntry;
		
		Document doc;
		String dat = new String(data.getData(), "UTF-8");
		doc = XMLParser.parse(dat);
		
		// parse date 
		if (null != doc.getElementsByTagName(DATE).item(0))
			logEntry.setDate(doc.getElementsByTagName(DATE).item(0).getFirstChild().getNodeValue());
				
		// pulls in food entries
		NodeList nodes = doc.getElementsByTagName(FOOD_ENTRY);
		for (int index = 0; index < nodes.getLength(); ++index)
		{
			FoodEntry fe = new FoodEntry();
			Element foodEntry = (Element)nodes.item(index);

			fe.setName(getElementTextValue(foodEntry, NAME));
			fe.setUnits(Double.parseDouble(getElementTextValue(foodEntry, UNITS)));
			// cycles through all measurements
			for (Measurement msm : Measurement.values())
			{
				String value = getElementTextValue(foodEntry, msm.toString());
				if (null == value)
					continue;
				fe.setMeasurement(msm, Double.parseDouble(value));
			}
			
			logEntry.addFoodEntry(fe);
		}
		
		// pulls in workout
		if (null != doc.getElementsByTagName(WORKOUT).item(0))
			logEntry.setWorkout(Workout.findByValue(doc.getElementsByTagName(WORKOUT).item(0).getFirstChild().getNodeValue()));
		
		return logEntry;
		
	} // end parseLogEntry()
	
	
	/**
	 * Sends a request for the food list to the server.
	 */
	public static void requestFoodList(Destination dest)
	{
		dataCallBack = new DataCallBack(FileType.FOODLIST, dest);
		
		// get byte stream from the server
		RPCInterfaceAsync rpci = GWT.create(RPCInterface.class);
		rpci.getFile(Globals.foodListFileName, FileType.FOODLIST, dataCallBack);
		
	} // end requesFoodList()
	
	
	/**
	 * Parses and generates a food list from the passed byte data. DONE
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static FoodList parseFoodList(Data data) throws Exception
	{
		// creates empty food list
		FoodList fl = new FoodList();
		
		if (null == data)
			return fl;
		
		Document doc;
		String dat = new String(data.getData(), "UTF-8");
		doc = XMLParser.parse(dat);
				
		// pulls in food entries
		NodeList nodes = doc.getElementsByTagName(FOOD);
		for (int index = 0; index < nodes.getLength(); ++index)
		{
			Food food = new Food();
			String value = "";
			Element foodElement = (Element)nodes.item(index);

			value = getElementTextValue(foodElement, NAME);
			if (null == value)
				continue;
			food.setName(value);
			
			value = getElementTextValue(foodElement, UNIT);
			if (null == value)
				continue;
			food.setUnit(getElementTextValue(foodElement, UNIT));
			// cycles through all measurements
			for (Measurement msm : Measurement.values())
			{
				value = getElementTextValue(foodElement, msm.toString());
				if (null == value)
					continue;
				food.setMeasurement(msm, Double.parseDouble(value));
			}
			
			fl.add(food);
		}
		
		return fl;
			
	} // end parseFoodList()
	
	
	/**
	 * Saves list of foods to the output file. 
	 * @param fl
	 */
	public static void setFoodList(FoodList fl) throws Exception
	{
		Document doc = XMLParser.createDocument();
		Element root;
		Element foodElem;
		Element child;
		Element workoutElem;
	   		
		// root element
		root = doc.createElement(FOOD_LIST);
		doc.appendChild(root);

		// inner elements
		for (Food food : fl)
		{
			// food element
			foodElem = doc.createElement(FOOD);
			
			// name a units
			child = doc.createElement(NAME);
			child.appendChild(doc.createTextNode(food.getName()));
			foodElem.appendChild(child);
			child = doc.createElement(UNIT);
			child.appendChild(doc.createTextNode(food.getUnit()));
			foodElem.appendChild(child);
			
			// all measurements
			for (Measurement msm : Measurement.values())
			{
				child = doc.createElement(msm.toString());
				child.appendChild(doc.createTextNode(Double.toString(food.getMeasurement(msm))));
				foodElem.appendChild(child);				
			}
					
			// close food entry
			root.appendChild(foodElem);
		}
		
		// send byte stream to the server
		RPCInterfaceAsync rpci = GWT.create(RPCInterface.class);
		rpci.sendFile(doc.toString().getBytes(), Globals.foodListFileName, 
				FileType.FOODLIST, new MessageCallBack());
		
		
	} // end setFoodList()
	
	
	/**
	 * Requests the goals data from the server.
	 * @param dest
	 */
	public static void requestGoals(Destination dest)
	{
		dataCallBack = new DataCallBack(FileType.GOALS, dest);
		
		// get byte stream from the server
		RPCInterfaceAsync rpci = GWT.create(RPCInterface.class);
		rpci.getFile(Globals.goalsFileName, FileType.GOALS, dataCallBack);

	} // end requestGoals

	
	/**
	 * Parses the xml data and creates a goals object to be returned to the 
	 * user. DONE
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Goals parseGoals(Data data) throws Exception
	{
		// creates empty log entry
		Goals goals = new Goals();
		
		if (null == data)
			return goals;
		
		Document doc;
		String dat = new String(data.getData(), "UTF-8");
		doc = XMLParser.parse(dat);
				
		// cycles through all measurements, pulling in all the goal values for 
		// each
		Element root = (Element)doc.getElementsByTagName(GOALS).item(0);
		for (Measurement msm : Measurement.values())
		{
			String value = getElementTextValue(root, msm.toString());
			if (null == value)
				continue;
			goals.setMeasurement(msm, Double.parseDouble(value));			
		}
		
		// sets the notes section of the goals
		String notes = getElementTextValue(root,  NOTES);
		if (null != notes)
			goals.setNotes(notes);
		
		return goals;
		
	} // end parseGoals()
	
	
	/**
	 * Creates goals xml from the Goals object and sends it to the server to 
	 * be stored.
	 * @param goals
	 * @throws Exception
	 */
	public static void setGoals(Goals goals) throws Exception
	{
		Document doc = XMLParser.createDocument();
	   		
		// root element
		Element root = doc.createElement(GOALS);
		doc.appendChild(root);

		// measurements
		for (Measurement msm : Measurement.values())
		{
			// food element
			Element msmElem = doc.createElement(msm.toString());
			msmElem.appendChild(doc.createTextNode(Double.toString(goals.getMeasurement(msm))));
					
			// close measurement entry
			root.appendChild(msmElem);
		}
		
		// sets notes
		Element notesElem = doc.createElement(NOTES);
		notesElem.appendChild(doc.createTextNode(goals.getNotes()));
		root.appendChild(notesElem);
		
		// send byte stream to the server
		RPCInterfaceAsync rpci = GWT.create(RPCInterface.class);
		rpci.sendFile(doc.toString().getBytes(), 
				Globals.goalsFileName, 
				FileType.GOALS, 
				new MessageCallBack());		
		
	} // end setGoals()
	
	
	/**
	 * Returns the textual data of an xml element.
	 * @param parent
	 * @param tag
	 * @return
	 */
	private static String getElementTextValue(Element parent, String tag)
	{
		if (null == parent.getElementsByTagName(tag).item(0))
			return null;
		return parent.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
		 
	} // end getElementTextValue() 

} // end class DataManager
