package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


/**
 * Gripper class
 * @author Julien Thrum & Erin Lafrenz
 */
public class Gripper
{
	private static Gripper instance = new Gripper();

	public static Gripper getInstance()
	{
		return instance;
	}
	
	WPI_TalonSRX mLeftIntake = new WPI_TalonSRX(0);
	//WPI_TalonSRX mRightIntake = new WPI_TalonSRX(1);
	WPI_TalonSRX mPivot = new WPI_TalonSRX(2);
	
	boolean isIntakeDone = true;
	boolean isPivotDone = true;
	
	public Gripper()
	{
		mLeftIntake.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		mLeftIntake.setSensorPhase(false);
		
		mPivot.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		mPivot.setSensorPhase(false);
	}

	/**
	 * Intake function for autonomous
	 * @return State of intake, whether it's done or not
	 */
	boolean autoIntake()
	{
//		if(isIntakeDone == true)
//		{
//			isIntakeDone = false;
//			mRightIntake.setSelectedSensorPosition(0,0,0);		// set encoder position
//			mLeftIntake.set(1);
//			mRightIntake.set(-1);		// set motor
//		}
//		else if(mRightIntake.getSelectedSensorPosition(0) >= Constants.SUCK_CUBE_IN)
//		{
//			intakeOff();
//			isIntakeDone = true;
//		}
		
		return isIntakeDone;
	}

	/** 
	 * Outtake function for autonomous
	 * @return State of outtake, whether it's done or not
	 */
	boolean autoOuttake()
	{
//		if(isIntakeDone == true)
//		{
//			isIntakeDone = false;
//			mRightIntake.setSelectedSensorPosition(0,0,0);		// set encoder position
//			mLeftIntake.set(-1);
//			mRightIntake.set(1);
//		}
//		else if(mRightIntake.getSelectedSensorPosition(0) >= Constants.SPIT_CUBE_OUT)
//		{
//			intakeOff();
//			isIntakeDone = true;
//		}
//		
		return isIntakeDone;
	}

	/**
	 * Turn off intake
	 */
	void intakeOff()
	{
		mLeftIntake.set(0);
//		mRightIntake.set(0);
	}

	/**
	 * Intake for use during tele-op
	 */
	void intake()
	{
		mLeftIntake.set(-0.5);
//		mRightIntake.set(0.5);
	}
	
	/**
	 * Outtake for use during tele-op
	 */
	void outtake()
	{
		mLeftIntake.set(0.5);
//		mRightIntake.set(-0.5);
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
			if(mPivot.getSelectedSensorPosition(0) > Constants.SWITCH)
			{
				pivot(-.25);
			}
			else if(mPivot.getSelectedSensorPosition(0) < Constants.SWITCH)
			{
				pivot(.25);
			}
		}
		else if(Math.abs(mPivot.getSelectedSensorPosition(0)) >= Constants.SWITCH)
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
			if(mPivot.getSelectedSensorPosition(0) > Constants.SCALE)
			{
				pivot(-.25);
			}
			else if(mPivot.getSelectedSensorPosition(0) < Constants.SCALE)
			{
				pivot(.25);
			}
		}
		else if(Math.abs(mPivot.getSelectedSensorPosition(0)) >= Constants.SCALE)
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
		public enum Rotations
		{
			nintendoWiiUHeight	//Slowest selling Nintendo console in history
		}

		//TODO:change numbers to appropriate values
		public static final int SPIT_CUBE_OUT = 1;
		public static final int SUCK_CUBE_IN = 500;
		public static final int SWITCH = 0;
		public static final int SCALE = 1;
	}

}