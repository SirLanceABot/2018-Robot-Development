package org.usfirst.frc.team4237.robot.network;

public class AutoSelect4237Data
{
	private String selectedPosition = "None";
	private String planA = "Auto Line";
	private String planB = "Auto Line";
	private String planC = "Auto Line";
	private boolean grabSecondCube = false;
	private boolean placeSecondCubeInSwitch = false;
	private boolean placeSecondCubeInScale = false;
	
	public String getSelectedPosition()
	{
		return this.selectedPosition;
	}
	
	public String getPlanA()
	{
		return this.planA;
	}
	
	public String getPlanB()
	{
		return this.planB;
	}
	
	public String getPlanC()
	{
		return this.planC;
	}
	
	public boolean getGrabSecondCube()
	{
		return this.grabSecondCube;
	}
	
	public boolean getPlaceSecondCubeInSwitch()
	{
		return this.placeSecondCubeInSwitch;
	}
	
	public boolean getPlaceSecondCubeInScale()
	{
		return this.placeSecondCubeInScale;
	}
	
}
