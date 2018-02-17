package org.usfirst.frc.team4237.robot.network;

public class AutoSelect4237Data
{
	private String selectedPosition;
	private String selectedTarget;
	private String selectedBackupPlan; 
	
	public synchronized String getSelectedPosition()
	{
		return this.selectedPosition;
	}
	
	public synchronized String getSelectedTarget()
	{
		return this.selectedTarget;
	}
	
	public synchronized String getSelectedBackupPlan()
	{
		return this.selectedBackupPlan;
	}
	
}
