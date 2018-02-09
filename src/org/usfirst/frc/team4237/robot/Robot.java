package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.control.Xbox;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot
{
	private Teleop teleop = Teleop.getInstance();
	private Autonomous autonomous = Autonomous.getInstance();
		
	public Robot()
	{

	}
	
	@Override
	public void robotInit()
	{
		System.out.println("Starting robot...");
	}
	
	@Override
	public void disabledInit()
	{
		System.out.println("Robot is disabled");
	}
	
	@Override
	public void disabledPeriodic()
	{
			
	}
	
	@Override
	public void teleopInit()
	{
		System.out.println("Entering teleop");
		teleop.init();
	}
	
	@Override
	public void teleopPeriodic()
	{
		teleop.periodic();
	}
	
	@Override
	public void autonomousInit()
	{
		System.out.println("Entering autonomous");
		autonomous.init();
	}
	
	@Override
	public void autonomousPeriodic()
	{
		autonomous.periodic();
	}
}
