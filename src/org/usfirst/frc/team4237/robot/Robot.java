package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.control.Xbox;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot
{
	public Xbox xbox = Xbox.getInstance();
	public RaspberryPiReceiver raspberryPiReceiver = RaspberryPiReceiver.getInstance();
	
	public Robot()
	{
		raspberryPiReceiver.start();
	}
	
	@Override
	public void robotInit()
	{
		
	}
	
	@Override
	public void disabledInit()
	{
		System.out.println("Entering Disabled");
	}
	
	@Override
	public void disabledPeriodic()
	{
		System.out.println(raspberryPiReceiver.getValue());
	}
	
	@Override
	public void teleopPeriodic()
	{
		
	}
}
