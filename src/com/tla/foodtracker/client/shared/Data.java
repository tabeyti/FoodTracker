package com.tla.foodtracker.client.shared;

import java.io.Serializable;

public class Data implements Serializable
{
	private byte[] data = null;
	private String fileName = null;
	
	public Data() 
	{}
	
	public Data(byte[] data, String fileName)
	{
		this.data = data;
		this.fileName = fileName;
	}
	
	
	public byte[] getData()
	{
		return data;
	}
	public void setData(byte[] data)
	{
		this.data = data;
	}


	public String getFileName()
	{
		return fileName;
	}


	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

}
