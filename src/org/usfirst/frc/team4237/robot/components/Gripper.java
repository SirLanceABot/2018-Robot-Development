package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.usfirst.frc.team4237.robot.control.Xbox;

import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Gripper class
 * @author Julien Dumb & Erin Lafrenz & Ben Puzycki & Shi Han Dont Have One Wang
 */
public class Gripper extends Thread
{
	private Xbox xbox = Xbox.getInstance();
	private static Gripper instance = new Gripper();

	public static Gripper getInstance()
	{
		return instance;
	}
	
	WPI_TalonSRX leftIntake = new WPI_TalonSRX(Constants.LEFT_INTAKE_MOTOR_PORT);
	WPI_TalonSRX rightIntake = new WPI_TalonSRX(Constants.RIGHT_INTAKE_MOTOR_PORT);
	WPI_TalonSRX pivot = new WPI_TalonSRX(Constants.PIVOT_MOTOR_PORT);
	
	boolean isIntakeDone = true;
	boolean isPivotRaisedDone = true;
	boolean isPivotMiddleDone = true;
	boolean isPivotFloorDone = true;
	
	boolean isAutoPivotRaisedDone = false;
	boolean isAutoPivotMiddleDone = false;
	boolean isAutoPivotFloorDone = false;
	
	private Constants.Direction pivotDirection = Constants.Direction.None;
	
	public Gripper()
	{
		//TODO: Disable Soft lImite switches
		leftIntake.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		leftIntake.setSensorPhase(false);
		rightIntake.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		rightIntake.setSensorPhase(true);
		
		leftIntake.configForwardSoftLimitEnable(false, 0);
		leftIntake.configReverseSoftLimitEnable(false, 0);
		rightIntake.configForwardSoftLimitEnable(false, 0);
		rightIntake.configReverseSoftLimitEnable(false, 0);
		
		rightIntake.setInverted(true);
		
		pivot.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		pivot.setSensorPhase(false);
		pivot.configForwardSoftLimitEnable(false, 0);
		pivot.configReverseSoftLimitEnable(false, 0);
		
		leftIntake.selectProfileSlot(Constants.PID_SLOT_ID, 0);
		leftIntake.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
		leftIntake.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
		leftIntake.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
		leftIntake.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
		
		rightIntake.selectProfileSlot(Constants.PID_SLOT_ID, 0);
		rightIntake.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
		rightIntake.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
		rightIntake.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
		rightIntake.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
	}
	public void resetEncoders()
	{
		pivot.setSelectedSensorPosition(0,0,0);
	}

	/**
	 * Intake function for autonomous
	 * @return State of intake, whether it's done or not
	 */
	public boolean autoIntake()
	{
		if(isIntakeDone == true)
		{
			isIntakeDone = false;
			leftIntake.setSelectedSensorPosition(0,0,0);
			rightIntake.setSelectedSensorPosition(0,0,0);		// set encoder position
			leftIntake.set(ControlMode.Velocity, 1);
			rightIntake.set(ControlMode.Velocity, 1);		// set motor
		}
		else if(rightIntake.getSelectedSensorPosition(0) >= Constants.SUCK_CUBE_IN)
		{
			intakeOff();
			isIntakeDone = true;
		}
		
		return isIntakeDone;
	}

	/** 
	 * eject function for autonomous
	 * @return State of eject, whether it's done or not
	 */
	public boolean autoEject()
	{
		if(isIntakeDone == true)
		{
			isIntakeDone = false;
			rightIntake.setSelectedSensorPosition(0,0,0);		// set encoder position
			leftIntake.set(ControlMode.Velocity, -1);
			rightIntake.set(ControlMode.Velocity, -1);
		}
		else if(rightIntake.getSelectedSensorPosition(0) >= Constants.SPIT_CUBE_OUT)
		{
			intakeOff();
			isIntakeDone = true;
		}
		
		return isIntakeDone;
	}

	/**
	 * Turn off intake
	 */
	public void intakeOff()
	{
		//leftIntake.set(ControlMode.Velocity, 0);
		//rightIntake.set(ControlMode.Velocity, 0);
		
		leftIntake.set(ControlMode.PercentOutput, 0);
		rightIntake.set(ControlMode.PercentOutput, 0);
	}

	/**
	 * Intake for use during tele-op
	 */
	public void intake()
	{
		leftIntake.set(ControlMode.PercentOutput, -0.5);
		rightIntake.set(ControlMode.PercentOutput, -0.5);
		//leftIntake.set(ControlMode.Velocity, -0.5);
		//rightIntake.set(ControlMode.Velocity, -0.5);
	}
	
	/**
	 * Eject for use during tele-op
	 */
	public void eject()
	{
		//leftIntake.set(ControlMode.Velocity, 500);
		//rightIntake.set(ControlMode.Velocity, 500);
		
		leftIntake.set(ControlMode.PercentOutput, 0.5);
		rightIntake.set(ControlMode.PercentOutput, 0.5);
	}
	
	public void spinLeft()
	{
		leftIntake.set(ControlMode.PercentOutput, -0.3);
		rightIntake.set(ControlMode.PercentOutput, 0.3);
	}
	
	public void spinRight()
	{
		leftIntake.set(ControlMode.PercentOutput, 0.3);
		rightIntake.set(ControlMode.PercentOutput, -0.3);
	}
	
	/**
	 *  Angle gripper
	 * @param value from joystick
	 */
	public void pivot(double value)
	{
		pivot.set(value);
	}

	/**
	 * Stop pivoting
	 */
	public void pivotOff()
	{
		pivot.set(0.0);
	}
	
	@Override
	public synchronized void run()
	{
		while (!this.interrupted())
		{
			boolean aButton = xbox.getRawButton(1);
			boolean xButton = xbox.getRawButton(3);
			boolean yButton = xbox.getRawButton(4);
			
			
			if(yButton || !isPivotRaisedDone)
			{	
				isPivotRaisedDone = false;
				isPivotMiddleDone = true;
				isPivotFloorDone = true;
				if(pivot.getSelectedSensorPosition(0) < Constants.ENCODER_RAISED_POSITION)
				{
					pivot(0.5);
				}
				else
				{
					isPivotRaisedDone = true;
					pivotOff();
				}

				
			}
			if(xButton || !isPivotMiddleDone)
			{
				isPivotFloorDone = true;
				isPivotRaisedDone = true;
				
				if(isPivotMiddleDone)
				{
					isPivotMiddleDone = false;
					if(pivot.getSelectedSensorPosition(0) > Constants.ENCODER_MIDDLE_POSITION)	// If it is above the switch
					{
						pivot(-0.5);
						pivotDirection = Constants.Direction.Down;
					}
					else if(pivot.getSelectedSensorPosition(0) < Constants.ENCODER_MIDDLE_POSITION)	// If it is below the switch
					{
						pivot(0.5);  
						pivotDirection = Constants.Direction.Up;
					}
				}
				else if(((pivot.getSelectedSensorPosition(0) >= Constants.ENCODER_MIDDLE_POSITION) && (pivotDirection == Constants.Direction.Up)) || ((pivot.getSelectedSensorPosition(0) <= Constants.ENCODER_MIDDLE_POSITION) && (pivotDirection == Constants.Direction.Down)))
				{
					isPivotMiddleDone = true;
					pivotOff();
				}

			}
			if(aButton || !isPivotFloorDone)
			{
				isPivotFloorDone = false;
				isPivotMiddleDone = true;
				isPivotRaisedDone = true;
				
				if(pivot.getSelectedSensorPosition(0) > Constants.ENCODER_FLOOR_POSITION)
				{
					pivot(-0.5);
				}
				else
				{
					pivotOff();
					isPivotFloorDone = true;
				}
			}
			
			Timer.delay(0.005);
		}
	}
			

	public boolean autoPivotUp()
	{
		isAutoPivotRaisedDone = false;
		
		if(pivot.getSelectedSensorPosition(0) < Constants.ENCODER_RAISED_POSITION)
		{
			pivot(0.5);
		}
		else
		{
			pivotOff();
			isAutoPivotRaisedDone = true;
		}
		return isAutoPivotRaisedDone;
	}
	
	public boolean autoPivotFloor()
	{	
		if(pivot.getSelectedSensorPosition(0) > Constants.ENCODER_FLOOR_POSITION && (isAutoPivotMiddleDone && isAutoPivotRaisedDone))
		{
			isAutoPivotFloorDone = false;
			pivot(-0.5);
		}
		else
		{
			pivotOff();
			isAutoPivotFloorDone = true;
		}
		return isAutoPivotFloorDone;
	}
	
	/**
	 * Pivot to angle of switch
	 * @return State of pivot, whether it's done or not
	 */
	public boolean autoPivotMiddle()
	{
		if(isAutoPivotMiddleDone)
		{
			isAutoPivotMiddleDone = false;
			if(pivot.getSelectedSensorPosition(0) > Constants.ENCODER_MIDDLE_POSITION)	// If it is above the switch
			{
				pivot(-0.5);
				pivotDirection = Constants.Direction.Down;
			}
			else if(pivot.getSelectedSensorPosition(0) < Constants.ENCODER_MIDDLE_POSITION)	// If it is below the switch
			{
				pivot(0.5);  
				pivotDirection = Constants.Direction.Up;
			}
		}
		else if(((pivot.getSelectedSensorPosition(0) >= Constants.ENCODER_MIDDLE_POSITION) && (pivotDirection == Constants.Direction.Up)) || ((pivot.getSelectedSensorPosition(0) <= Constants.ENCODER_MIDDLE_POSITION) && (pivotDirection == Constants.Direction.Down)))
		{
			pivotOff();
			isAutoPivotMiddleDone = true;
		}
		return isAutoPivotMiddleDone;
	}
	
	
	public int[] getVelocityArray()
	{
		return new int[] {leftIntake.getSelectedSensorVelocity(0), rightIntake.getSelectedSensorVelocity(0)};
	}
	public double getPivotEncoder()
	{
		return pivot.getSelectedSensorPosition(0);
	}

	/**
	 * Constants class for Gripper
	 */
	public static class Constants
	{
		public enum Direction
		{
			Up,
			Down,
			None
		}
		
		//TODO:change numbers to appropriate values
		public static final int SPIT_CUBE_OUT = 1;
		public static final int SUCK_CUBE_IN = 500;
		
		public static final int ENCODER_FLOOR_POSITION = 0;
		public static final int ENCODER_MIDDLE_POSITION = 3000;
		public static final int ENCODER_RAISED_POSITION = 6000;
		
		public static final int LEFT_INTAKE_MOTOR_PORT = 0;
		public static final int RIGHT_INTAKE_MOTOR_PORT = 1;
		public static final int PIVOT_MOTOR_PORT = 1;
		
		public static final int PID_SLOT_ID = 0;
	}

}