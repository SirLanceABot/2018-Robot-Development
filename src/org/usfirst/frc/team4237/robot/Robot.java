package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.components.Elevator;
import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.control.Xbox;
import org.usfirst.frc.team4237.robot.network.AutoSelect4237;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot
{
	private Autonomous autonomous = Autonomous.getInstance();
	private Gripper gripper = Gripper.getInstance();
	
	public Robot()
	{
		
	}
	
	@Override
	public void robotInit()
	{
		System.out.println("Starting robot!");
		Thread drivetrain = new Thread(Drivetrain.getInstance());
		drivetrain.start();
		System.out.println("Drivetrain thread started!");
		
		Elevator.getInstance().start();
		System.out.println("Elevator thread started!");
		
		Gripper.getInstance().start();
		System.out.println("Gripper thread started!");
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
		gripper.setTeleopLimits();
	}
	
	@Override
	public void teleopPeriodic()
	{

	}
	
	@Override
	public void autonomousInit()
	{
		System.out.println("Entering autonomous");
		gripper.setAutoLimits();
		autonomous.init();
	}
	
	@Override
	public void autonomousPeriodic()
	{
		autonomous.periodic();
	}
}
