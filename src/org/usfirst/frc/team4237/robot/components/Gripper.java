package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


/**
 * Gripper class
 * @author Julien Dumb & Erin Lafrenz
 */
public class Gripper
{
	private static Gripper instance = new Gripper();

	public static Gripper getInstance()
	{
		return instance;
	}
	
	WPI_TalonSRX mLeftIntake = new WPI_TalonSRX(Constants.LEFT_INTAKE_MOTOR_PORT);
	WPI_TalonSRX mRightIntake = new WPI_TalonSRX(Constants.RIGHT_INTAKE_MOTOR_PORT);
	WPI_TalonSRX mPivot = new WPI_TalonSRX(Constants.PIVOT_MOTOR_PORT);
	
	boolean isIntakeDone = true;
	boolean isPivotDone = true;
	
	public Gripper()
	{
		mLeftIntake.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		mLeftIntake.setSensorPhase(false);
		mRightIntake.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		mRightIntake.setSensorPhase(false);
		
		
		
		mPivot.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		mPivot.setSensorPhase(false);
		
		mLeftIntake.selectProfileSlot(Constants.PID_SLOT_ID, 0);
		mLeftIntake.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
		mLeftIntake.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
		mLeftIntake.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
		mLeftIntake.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
		
		mRightIntake.selectProfileSlot(Constants.PID_SLOT_ID, 0);
		mRightIntake.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
		mRightIntake.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
		mRightIntake.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
		mRightIntake.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
	}

	/**
	 * Intake function for autonomous
	 * @return State of intake, whether it's done or not
	 */
	boolean autoIntake()
	{
		if(isIntakeDone == true)
		{
			isIntakeDone = false;
			mLeftIntake.setSelectedSensorPosition(0,0,0);
			mRightIntake.setSelectedSensorPosition(0,0,0);		// set encoder position
			mLeftIntake.set(ControlMode.Velocity, 1);
			mRightIntake.set(ControlMode.Velocity, -1);		// set motor
		}
		else if(mRightIntake.getSelectedSensorPosition(0) >= Constants.SUCK_CUBE_IN)
		{
			intakeOff();
			isIntakeDone = true;
		}
		
		return isIntakeDone;
	}

	/** 
	 * Outtake function for autonomous
	 * @return State of outtake, whether it's done or not
	 */
	boolean autoOuttake()
	{
		if(isIntakeDone == true)
		{
			isIntakeDone = false;
			mRightIntake.setSelectedSensorPosition(0,0,0);		// set encoder position
			mLeftIntake.set(ControlMode.Velocity, -1);
			mRightIntake.set(ControlMode.Velocity, 1);
		}
		else if(mRightIntake.getSelectedSensorPosition(0) >= Constants.SPIT_CUBE_OUT)
		{
			intakeOff();
			isIntakeDone = true;
		}
		
		return isIntakeDone;
	}

	/**
	 * Turn off intake
	 */
	void intakeOff()
	{
		mLeftIntake.set(ControlMode.Velocity, 0);
		mRightIntake.set(ControlMode.Velocity, 0);
	}

	/**
	 * Intake for use during tele-op
	 */
	void intake()
	{
		mLeftIntake.set(ControlMode.Velocity, -0.5);
		mRightIntake.set(ControlMode.Velocity, 0.5);
	}
	
	/**
	 * Outtake for use during tele-op
	 */
	void outtake()
	{
		mLeftIntake.set(ControlMode.Velocity, 0.5);
		mRightIntake.set(ControlMode.Velocity, -0.5);
	}
	
	void spinLeft()
	{
		mLeftIntake.set(ControlMode.Velocity, 0.1);
		mRightIntake.set(ControlMode.Velocity, 0.1);
	}
	
	void spinRight()
	{
		mLeftIntake.set(ControlMode.Velocity, -0.1);
		mLeftIntake.set(ControlMode.Velocity, -0.1);
	}
	
	/**
	 *  Angle gripper
	 * @param value from joystick
	 */
	void pivot(double value)
	{
		mPivot.set(value);
	}

	/**
	 * Stop pivoting
	 */
	void pivotOff()
	{
		mPivot.set(0);
	}
	
	/**
	 * Pivot to angle of switch
	 * @return State of pivot, whether it's done or not
	 */
	boolean pivotSwitch()
	{
		if(isPivotDone)
		{
			isPivotDone = false;
			if(mPivot.getSelectedSensorPosition(0) > Constants.ENCODER_SWITCH_POSITION)
			{
				pivot(-.25);
			}
			else if(mPivot.getSelectedSensorPosition(0) < Constants.ENCODER_SWITCH_POSITION)
			{
				pivot(.25);
			}
		}
		else if(Math.abs(mPivot.getSelectedSensorPosition(0)) >= Constants.ENCODER_SWITCH_POSITION)
		{
			pivotOff();
			isPivotDone = true;
		}
		return isPivotDone;
	}

	/**
	 * Pivot to angle of scale
	 * @return State of pivot, whether it's done or not
	 */
	boolean pivotScale()
	{
		if(isPivotDone)
		{
			isPivotDone = false;
			if(mPivot.getSelectedSensorPosition(0) > Constants.ENCODER_SCALE_POSITION)
			{
				pivot(-.25);
			}
			else if(mPivot.getSelectedSensorPosition(0) < Constants.ENCODER_SCALE_POSITION)
			{
				pivot(.25);
			}
		}
		else if(Math.abs(mPivot.getSelectedSensorPosition(0)) >= Constants.ENCODER_SCALE_POSITION)
		{
			pivotOff();
			isPivotDone = true;
		}
		return isPivotDone;
	}
	
	

	/**
	 * Constants class for Gripper
	 */
	public static class Constants
	{
		//TODO:change numbers to appropriate values
		public static final int SPIT_CUBE_OUT = 1;
		public static final int SUCK_CUBE_IN = 500;
		
		public static final int ENCODER_SWITCH_POSITION = 0;
		public static final int ENCODER_SCALE_POSITION = 1;
		
		public static final int LEFT_INTAKE_MOTOR_PORT = 0;
		public static final int RIGHT_INTAKE_MOTOR_PORT = 1;
		public static final int PIVOT_MOTOR_PORT = 2;
		
		public static final int PID_SLOT_ID = 0;
	}

}