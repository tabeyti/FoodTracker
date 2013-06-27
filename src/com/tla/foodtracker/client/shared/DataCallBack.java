package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tla.foodtracker.client.dayentry.DayEntryPanel;
import com.tla.foodtracker.client.foodlist.FoodListPanel;
import com.tla.foodtracker.shared.Destination;
import com.tla.foodtracker.shared.ExceptionWindow;
import com.tla.foodtracker.shared.FileType;
import com.tla.foodtracker.client.plots.Graph;


public class DataCallBack implements AsyncCallback<Data>
{
	private byte[] data = null;
	private FileType type = null;
	private Destination destination = null;
	
	public DataCallBack(FileType type, Destination destination)
	{
		this.destination = destination;
		this.type = type;
		
	} // end DataCallBack()
	
	@Override
	public void onFailure(Throwable caught) 
	{
		Window.alert("Unable to retrieve data");
		
	}

	@Override
	public void onSuccess(Data result) 
	{
		data = result.getData();
		LogEntry le = null;
		FoodList fl = null;
		
		try
		{
			switch (type)
			{
				case LOGENTRY:
					le = DataManager.parseLogEntry(result);
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
					fl = DataManager.parseFoodList(result);
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
					Goals goals = DataManager.parseGoals(result);
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
			ExceptionWindow.Error("DataCallBack.onSucess()", e);
		}

	}

	public byte[] getData() 
	{
		return data;
	}

} // end class DataCallBack
