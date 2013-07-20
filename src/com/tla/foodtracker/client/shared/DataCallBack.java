package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tla.foodtracker.client.dayentry.DayEntryPanel;
import com.tla.foodtracker.client.foodlist.FoodListPanel;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.FileType;
import com.tla.foodtracker.client.plots.Graph;


public class DataCallBack implements AsyncCallback<Data>
{
	private byte[] data = null;
	private FileType type = null;
	private Destination destination = null;
	
	private LogEntry le;
	private FoodList fl;
	
	public DataCallBack(FileType type, Destination destination)
	{
		this.destination = destination;
		this.type = type;
		
	} // end DataCallBack()
	
	@Override
	public void onFailure(Throwable caught) 
	{
		//Window.alert("Unable to retrieve data");
		ExceptionWindow.Error(caught.getMessage());
		
	}

	@Override
	public void onSuccess(Data result) 
	{
		String debugStr = "Entered\n";
		
		data = result.getData();
		le = null;
		fl = null;
		
		debugStr += "Post Init variables\n";
		
		try
		{
			debugStr += "Entered try \n";
			switch (type)
			{
				case LOGENTRY:
					debugStr += "LOGENTRY\n";
					debugStr += "pre parse\n";
					le = DataManager.parseLogEntry(result);
					debugStr += "post parse\n";
					if (null == le)
						return;
					switch (destination)
					{
						case DAY_ENTRY:
							DayEntryPanel.loadLogEntry(le);
							break;
						case PLOT_LINE:
							Graph.loadLogEntry(le);
							break;
					}
					break;
				case FOODLIST:
					debugStr += "FOODLIST\n";
					debugStr += "pre parse\n";
					fl = DataManager.parseFoodList(result);
					debugStr += "post parse\n";
					if (null == fl)
						return;
					switch (destination)
					{
						case FOOD_LIST:
							FoodListPanel.loadFoodList(fl);
							break;
						case DAY_ENTRY:
							DayEntryPanel.loadFoodList(fl);
							break;
						default:
							break;						
					}					
					break;
				case GOALS:
					debugStr += "GOALS\n";
					debugStr += "pre parse\n";
					Goals goals = DataManager.parseGoals(result);
					debugStr += "post parse\n";
					if (null == goals)
						return;
					switch (destination)
					{
						case DAY_ENTRY:
							DayEntryPanel.loadGoals(goals);
							break;
						case PLOT_LINE:
							Graph.loadGoals(goals);
							break;
						default:
							break;
					}
				default:
					break;
					
			}
			
		}
		catch (Exception e)
		{
			ExceptionWindow.Error("DataCallBack.onSuccess()\n" + debugStr, e);
			//ExceptionWindow.Error("DataCallBack.onSucess()", e);
		}

	}

	public byte[] getData() 
	{
		return data;
	}

} // end class DataCallBack
