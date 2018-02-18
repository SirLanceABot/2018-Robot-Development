package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
//import org.usfirst.frc.team4237.robot.components.Elevator;
//import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.control.DriverXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;
import org.usfirst.frc.team4237.robot.control.Xbox.Constants;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Teleop
{
	private Drivetrain drivetrain = Drivetrain.getInstance();
	//private Elevator elevator = Elevator.getInstance();
	//private Gripper gripper = Gripper.getInstance();
	private DriverXbox driverXbox = DriverXbox.getInstance();
	//private SmartDashboard dash = new SmartDashboard();

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
		try
		{
			if (Math.abs(driverXbox.getRawAxis(1)) > 0.2)
			{
				leftYAxis = -driverXbox.getRawAxis(Xbox.Constants.LEFT_STICK_Y_AXIS);
			}
			else leftYAxis = 0;

			if (Math.abs(driverXbox.getRawAxis(0)) > 0.2)
			{
				leftXAxis = driverXbox.getRawAxis(Xbox.Constants.LEFT_STICK_X_AXIS);
			}
			else leftXAxis = 0;

			if (Math.abs(driverXbox.getRawAxis(4)) > 0.2)
			{
				rightXAxis = driverXbox.getRawAxis(Xbox.Constants.RIGHT_STICK_X_AXIS);
			}
			else rightXAxis = 0;


			if(driverXbox.getRawButton(Constants.RIGHT_BUMPER))
			{
				drivetrain.driveCartesian(leftXAxis, leftYAxis, (drivetrain.getNavXYaw()) / 50);
			}
			else
			{
				drivetrain.driveCartesian(leftXAxis, leftYAxis, rightXAxis);
			}
			
			drivetrain.debugPrintCurrent();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
