package com.tla.foodtracker.client.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tla.foodtracker.shared.FileType;

@RemoteServiceRelativePath("message")
public interface RPCInterface extends RemoteService
{
	public Message sendFile(byte[] data, String fileName, FileType ft);
	public Data getFile(String path, FileType ft);
	
} // end interface RPCInterface