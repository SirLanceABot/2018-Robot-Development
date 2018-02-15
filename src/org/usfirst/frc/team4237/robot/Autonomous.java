package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.components.Elevator;
import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;

public class Autonomous
{
	private Drivetrain drivetrain = Drivetrain.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Gripper gripper = Gripper.getInstance();
	private RaspberryPiReceiver raspberryPiReceiver = RaspberryPiReceiver.getInstance();

	/*
	 * This class will handle switching between
	 * raspberry pi and roborio for vision
	 */
	
	private static Autonomous instance = new Autonomous();
	public static Autonomous getInstance()
	{
		return instance;
	}
	
	private Autonomous()
	{
		raspberryPiReceiver.start();
	}
	
	/**
	 * Autonomous init method
	 */
	public void init()
	{
		
	}
	
	/**
	 * Autonomous loop
	 */
	public void periodic()
	{
		
	}
}
