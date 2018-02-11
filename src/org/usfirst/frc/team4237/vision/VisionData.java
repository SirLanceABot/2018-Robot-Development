package org.usfirst.frc.team4237.vision;


import java.util.HashMap;

public class VisionData
{	
	private String idNum;
	private HashMap<String, VisionTarget> data;
	
	private static VisionData instance = new VisionData();
	public static VisionData getInstance()
	{
		return instance;
	}
	
	public VisionData()
	{
		
	}
	
	/**
	 * Sets the idNum and data of the instance of VisionData.
	 * @param visionData The VisionData object to copy.
	 */
	public void set(VisionData visionData)
	{
		idNum = visionData.getIdNum();
		data = visionData.getData();
	}
	
	/**
	 * Returns the current idNum of the instance of VisionData.
	 * @return The current idNum.
	 */
	public String getIdNum()
	{
		return this.idNum;
	}
	
	/**
	 * Returns the current data (vision targets and their numerical IDs).
	 * @return The current data.
	 */
	public HashMap<String, VisionTarget> getData()
	{
		return data;
	}
	
	/**
	 * Prints the current data in an easy-to-read form.
	 */
	public void debugOutput()
	{
		System.out.println("idNum: " + idNum);
		for (String i : data.keySet())
		{
			System.out.println("Target " + i);
			System.out.println("\tHeight:    " + data.get(i).getHeight());
			System.out.println("\tMidPointX: " + data.get(i).getMidpointX() + "\n");
		}
	}
}
