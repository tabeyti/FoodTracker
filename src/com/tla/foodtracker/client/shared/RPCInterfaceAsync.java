package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tla.foodtracker.shared.FileType;

public interface RPCInterfaceAsync
{
	public void sendFile(byte[] data, String path, FileType ft, AsyncCallback<Message> callback);
	public void getFile(String fileName, FileType ft, AsyncCallback<Data> callback);
	
} // end interface RPCInterfaceAsync
