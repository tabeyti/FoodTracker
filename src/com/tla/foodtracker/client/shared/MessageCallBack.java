package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MessageCallBack implements AsyncCallback<Message>
{

	@Override
	public void onFailure(Throwable caught)
	{
		Window.alert("Unable to obtain server response: "
			      + caught.getMessage());	
		
	}

	@Override
	public void onSuccess(Message result)
	{
		Window.alert(result.getMessage());
		
	}

} // end class MessageCallBack
