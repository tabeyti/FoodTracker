package com.tla.foodtracker.server;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.tla.foodtracker.client.shared.Data;
import com.tla.foodtracker.client.shared.Message;
import com.tla.foodtracker.client.shared.RPCInterface;
import com.tla.foodtracker.shared.FileType;
import com.tla.foodtracker.shared.Globals;

public class ServerDataManager extends RemoteServiceServlet implements RPCInterface
{
	private static String logPath = "logs/";
	private static String datPath = "dat/";
//	private static String logPath = "C:/ServerFiles/FoodTracker/logs/";
//	private static String datPath = "C:/ServerFiles/FoodTracker/dat/";

	
	@Override
	public Message sendFile(byte[] dat, String fileName, FileType ft)
	{
		// sets resource variables
		try
		{						
			FileOutputStream fos = null;
			
			switch (ft)
			{
				case GOALS:
				case FOODLIST:
					fos = new FileOutputStream(datPath + fileName + Globals.extention);
					break;
				case LOGENTRY:
					fos = new FileOutputStream(logPath + fileName + Globals.extention);
					break;
			}
			
			fos.write(dat);
			fos.close();
		}
		catch (Exception e)
		{
			return new Message(e.getMessage());
		}
	
		return new Message("Data Saved!");
		
	} // end sendFile()


	@Override
	public Data getFile(String fileName, FileType ft)
	{
		byte[] data = null;
		Path path = null;

		// sets resource variables
		try
		{			
			// determines the file path based on the type of file passed in
			switch (ft)
			{
				case GOALS:
				case FOODLIST:
					path = Paths.get(datPath + fileName + Globals.extention);
					break;
				case LOGENTRY:
					path = Paths.get(logPath + fileName + Globals.extention);
					break;
			}
			
			data = Files.readAllBytes(path);
			
		}
		catch (Exception e)
		{
			data = null;
			return new Data(data, fileName);
		}
		return new Data(data, fileName);
		
	} // end getFile()

} // end ServerDataManager
