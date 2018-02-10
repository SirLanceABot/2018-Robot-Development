package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.components.Elevator;
import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.control.DriverXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;

public class Teleop
{
	private Drivetrain drivetrain = Drivetrain.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Gripper gripper = Gripper.getInstance();
	private DriverXbox xbox = DriverXbox.getInstance();
	
	private boolean aButton = false;
	private boolean bButton = false;
	private boolean xButton = false;
	private boolean yButton = false;
	
	private double rightXAxis = 0.0;
	private double rightYAxis = 0.0;
	private double leftXAxis = 0.0;
	private double leftYAxis = 0.0;
	
	private static Teleop instance = new Teleop();
	public static Teleop getInstance()
	{
		return instance;
	}
	
	/**
	 * Teleop init method
	 */
	public void init()
	{
		
	}
	
	/**
	 * Teleop loop
	 */
	public void periodic()
	{
		leftXAxis = xbox.getRawAxis(Xbox.Constants.LEFT_STICK_X_AXIS);
		leftYAxis = xbox.getRawAxis(Xbox.Constants.LEFT_STICK_Y_AXIS);
		rightXAxis = xbox.getRawAxis(Xbox.Constants.RIGHT_STICK_X_AXIS);
		
		drivetrain.driveCartesian(leftXAxis, leftYAxis, rightXAxis);
		
	}
}
