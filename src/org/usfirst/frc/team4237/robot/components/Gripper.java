package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc.team4237.robot.control.OperatorXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;
import edu.wpi.first.wpilibj.Timer;

/**
 * Gripper class
 * @author Julien Dumb & Erin Lafrenz & Ben Puzycki & Shi Han Dont Have One Wang
 */
public class Gripper extends Thread
{
	private Xbox xbox = OperatorXbox.getInstance();

	private WPI_TalonSRX leftIntakeTalon = new WPI_TalonSRX(Constants.LEFT_INTAKE_MOTOR_PORT);
	private WPI_TalonSRX rightIntakeTalon = new WPI_TalonSRX(Constants.RIGHT_INTAKE_MOTOR_PORT);
	private WPI_TalonSRX pivoter = new WPI_TalonSRX(Constants.pivoter_MOTOR_PORT);
	
	private boolean isAutoEjecting = false;
	private boolean isAutoIntaking = false;
	private boolean isPivoting = false;
	
	private int currentValue = 0;
	
	private Constants.Range currentRange;
	private int[] targetRange = new int[2];
	private Constants.Direction currentDirection = Constants.Direction.None;

	
	//Quarantine
	boolean isIntakeDone = true;
	boolean ispivoterRaisedDone = true;
	boolean ispivoterMiddleDone = true;
	boolean ispivoterFloorDone = true;

	boolean isAutopivoterRaisedDone = false;
	boolean isAutopivoterMiddleDone = false;
	boolean isAutopivoterFloorDone = false;
	//End quarantine

	private static Gripper instance = new Gripper();
	public static Gripper getInstance()
	{
		return instance;
	}

	private Gripper()
	{
		//Left & right intake talon settings
		leftIntakeTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		leftIntakeTalon.setSensorPhase(false);
		leftIntakeTalon.configForwardSoftLimitEnable(false, 0);
		leftIntakeTalon.configReverseSoftLimitEnable(false, 0);
		leftIntakeTalon.setInverted(false);

		rightIntakeTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		rightIntakeTalon.setSensorPhase(true);
		rightIntakeTalon.configForwardSoftLimitEnable(false, 0);
		rightIntakeTalon.configReverseSoftLimitEnable(false, 0);
		rightIntakeTalon.setInverted(true);

		//Pivoter settings
		pivoter.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		pivoter.setSensorPhase(false);
		pivoter.configForwardSoftLimitEnable(false, 0);
		pivoter.configReverseSoftLimitEnable(false, 0);

		//PID settings
		leftIntakeTalon.selectProfileSlot(Constants.PID_SLOT_ID, 0);
		leftIntakeTalon.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
		leftIntakeTalon.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
		leftIntakeTalon.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
		leftIntakeTalon.config_kF(Constants.PID_SLOT_ID, 4.3, 1);

		rightIntakeTalon.selectProfileSlot(Constants.PID_SLOT_ID, 0);
		rightIntakeTalon.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
		rightIntakeTalon.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
		rightIntakeTalon.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
		rightIntakeTalon.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
	}
	public void resetEncoders()
	{
		pivoter.setSelectedSensorPosition(0,0,0);
	}

	
	//TODO: Fix this method
	/**
	 * Intake function for autonomous
	 * @return State of intake, whether it's done or not
	 */
	public boolean autoIntake()
	{
		if(!isAutoIntaking())
		{
			setAutoIntaking(true);
			leftIntakeTalon.setSelectedSensorPosition(0,0,0);
			rightIntakeTalon.setSelectedSensorPosition(0,0,0);		// set encoder position

			leftIntakeTalon.set(ControlMode.Velocity, 1);
			rightIntakeTalon.set(ControlMode.Velocity, 1);		// set motor
		}
		if(rightIntakeTalon.getSelectedSensorPosition(0) >= Constants.AUTO_INTAKE_ENCODER_STOP_VALUE)
		{
			intakeOff();
			setAutoIntaking(false);
		}

		return isAutoIntaking();
	}

	//TODO: Fix this method
	/** 
	 * Eject function for autonomous
	 * @return State of eject, whether it's done or not
	 */
	public boolean autoEject()
	{
		if(isAutoEjecting())
		{
			setAutoEjecting(true);

			rightIntakeTalon.setSelectedSensorPosition(0, 0, 0);		// set encoder position
			leftIntakeTalon.set(ControlMode.Velocity, -1);
			rightIntakeTalon.set(ControlMode.Velocity, -1);
		}
		//TODO: Check logic, should it be <= or should it be >= like it was originally?
		if(rightIntakeTalon.getSelectedSensorPosition(0) <= Constants.AUTO_EJECT_ENCODER_STOP_VALUE)
		{
			intakeOff();
			setAutoEjecting(false);
		}

		return isAutoEjecting();
	}

	/**
	 * Turn off intake
	 */
	public void intakeOff()
	{
		//TODO: Make velocity-based control work
		//leftIntakeTalon.set(ControlMode.Velocity, 0);
		//rightIntakeTalon.set(ControlMode.Velocity, 0);

		leftIntakeTalon.set(ControlMode.PercentOutput, 0);
		rightIntakeTalon.set(ControlMode.PercentOutput, 0);
	}

	/**
	 * Intake for use during tele-op
	 */
	public void intake()
	{
		//TODO: Make velocity-based control work
		//leftIntakeTalon.set(ControlMode.Velocity, -0.5);
		//rightIntakeTalon.set(ControlMode.Velocity, -0.5);

		leftIntakeTalon.set(ControlMode.PercentOutput, -0.5);
		rightIntakeTalon.set(ControlMode.PercentOutput, -0.5);
	}

	/**
	 * Eject for use during tele-op
	 */
	public void eject()
	{
		//leftIntakeTalon.set(ControlMode.Velocity, 500);
		//rightIntakeTalon.set(ControlMode.Velocity, 500);

		leftIntakeTalon.set(ControlMode.PercentOutput, 0.5);
		rightIntakeTalon.set(ControlMode.PercentOutput, 0.5);
	}

	/**
	 * Spins gripper to the left to move power cubes
	 */
	public void spinLeft()
	{
		leftIntakeTalon.set(ControlMode.PercentOutput, -0.3);
		rightIntakeTalon.set(ControlMode.PercentOutput, 0.3);
	}

	/**
	 * Spins gripper to the right to move power cubes
	 */
	public void spinRight()
	{
		leftIntakeTalon.set(ControlMode.PercentOutput, 0.3);
		rightIntakeTalon.set(ControlMode.PercentOutput, -0.3);
	}

	/**
	 * Move gripper at selected speed
	 * @param value Value from joystick
	 */
	public void pivot(double value)
	{
		setPivoting(true);
		pivoter.set(value);
	}
	
	/**
	 * Lower pivoter
	 */
	public void raise()
	{	
		setPivoting(true);
		pivot(0.5);
	}
	
	/**
	 * Lower pivoter
	 */
	public void lower()
	{
		setPivoting(true);
		pivot(-0.5);
	}
		
	/**
	 * Stop pivoting
	 */
	public void pivoterOff()
	{
		setPivoting(false);
		pivoter.set(0.0);
	}

	@Override
	public synchronized void run()
	{
		while (!this.interrupted())
		{
			boolean aButton = xbox.getRawButton(Xbox.Constants.A_BUTTON);
			boolean bButton = xbox.getRawButton(Xbox.Constants.B_BUTTON);
			boolean yButton = xbox.getRawButton(Xbox.Constants.Y_BUTTON);
			boolean xButton = xbox.getRawButton(Xbox.Constants.X_BUTTON);
			
			updateCurrentRange();
			
			//Start of code copied from Elevator
			if (!isPivoting())
			{

				//Some alternative code to make it work like the elevator

//				if (yButton)
//				{
//					targetRange = currentRange.higherNeighbor().range();
//					isMoving = true;
//					currentDirection = Constants.Direction.Up;
//				}
//				else if (aButton)
//				{
//					targetRange = currentRange.lowerNeighbor().range();
//					isMoving = true;
//					currentDirection = Constants.Direction.Down;
//				}
				
				if (aButton)
				{
					targetRange = Constants.Range.floorRange.range();
					setPivoting(true);
					currentDirection = Constants.Direction.Down;
				}
				else if (bButton)
				{
					targetRange = Constants.Range.middleRange.range();;
					setPivoting(true);
					if (currentValue < Constants.Range.middleRange.bottomValue())
					{
						currentDirection = Constants.Direction.Up;
					}
					else if (currentValue > Constants.Range.middleRange.topValue())
					{
						currentDirection = Constants.Direction.Down;
					}
					else
					{
						if (currentValue > Constants.Range.middleRange.topValue())
						{
							currentDirection = Constants.Direction.Down;
						}
						else if (currentValue < Constants.Range.middleRange.bottomValue())
						{
							currentDirection = Constants.Direction.Up;
						}
						else
						{
							currentDirection = Constants.Direction.None;
						}
					}
				}
				else if (yButton)
				{
					targetRange = Constants.Range.raisedRange.range();
					setPivoting(true);
					currentDirection = Constants.Direction.Up;
				}
			}
						
			if(isPivoting())
			{
				if (yButton)
				{
					pivoterOff();
				}
				
				if ((currentValue >= targetRange[0]) && (currentValue < targetRange[1]))
				{
					System.out.println("In target range");
					setPivoting(false);
					currentDirection = Constants.Direction.None;
					pivoterOff();
				}
				else if (currentDirection == Constants.Direction.Up)
				{
					System.out.println("Raising");
					raise();
				}
				else if (currentDirection == Constants.Direction.Down)
				{
					System.out.println("Lowering");
					lower();
				}
			}
			//End of code copied from elevator
			
			/*
			if(yButton || !ispivoterRaisedDone)
			{	
				ispivoterRaisedDone = false;
				ispivoterMiddleDone = true;
				ispivoterFloorDone = true;
				if(pivoter.getSelectedSensorPosition(0) < Constants.ENCODER_RAISED_POSITION)
				{
					pivot(0.5);
				}
				else
				{
					ispivoterRaisedDone = true;
					pivoterOff();
				}
			}
			if(bButton || !ispivoterMiddleDone)
			{
				ispivoterFloorDone = true;
				ispivoterRaisedDone = true;

				if(ispivoterMiddleDone)
				{
					ispivoterMiddleDone = false;
					if(pivoter.getSelectedSensorPosition(0) > Constants.ENCODER_MIDDLE_POSITION)	// If it is above the switch
					{
						pivot(-0.5);
						currentDirection = Constants.Direction.Down;
					}
					else if(pivoter.getSelectedSensorPosition(0) < Constants.ENCODER_MIDDLE_POSITION)	// If it is below the switch
					{
						pivot(0.5);  
						currentDirection = Constants.Direction.Up;
					}
				}
				else if(((pivoter.getSelectedSensorPosition(0) >= Constants.ENCODER_MIDDLE_POSITION) && (currentDirection == Constants.Direction.Up)) || ((pivoter.getSelectedSensorPosition(0) <= Constants.ENCODER_MIDDLE_POSITION) && (currentDirection == Constants.Direction.Down)))
				{
					ispivoterMiddleDone = true;
					pivoterOff();
				}

			}
			if(aButton || !ispivoterFloorDone)
			{
				ispivoterFloorDone = false;
				ispivoterMiddleDone = true;
				ispivoterRaisedDone = true;

				if(pivoter.getSelectedSensorPosition(0) > Constants.ENCODER_FLOOR_POSITION)
				{
					pivot(-0.5);
				}
				else
				{
					pivoterOff();
					ispivoterFloorDone = true;
				}
			}
			 */
			Timer.delay(0.005);
		}
	}
	
	public void updateCurrentRange()
	{
		currentValue = (int)getPivoterEncoder();
		if (currentValue <= Constants.Range.floorRange.topValue())
		{
			currentRange = Constants.Range.floorRange;
		}
		else if (currentValue <= Constants.Range.floorMiddleRange.topValue())
		{
			currentRange = Constants.Range.floorMiddleRange;
		}
		else if (currentValue <= Constants.Range.middleRange.topValue())
		{
			currentRange = Constants.Range.middleRange;
		}
		else if (currentValue <= Constants.Range.middleRaisedRange.topValue())
		{
			currentRange = Constants.Range.middleRaisedRange;
		}
		else if (currentValue <= Constants.Range.raisedRange.topValue())
		{
			currentRange = Constants.Range.raisedRange;
		}
	}

	public int[] getVelocityArray()
	{
		return new int[] {leftIntakeTalon.getSelectedSensorVelocity(0), rightIntakeTalon.getSelectedSensorVelocity(0)};
	}
	
	public double getPivoterEncoder()
	{
		return pivoter.getSelectedSensorPosition(0);
	}

	public boolean isAutoEjecting()
	{
		return isAutoEjecting;
	}

	public void setAutoEjecting(boolean isAutoEjecting)
	{
		this.isAutoEjecting = isAutoEjecting;
	}

	public boolean isAutoIntaking()
	{
		return isAutoIntaking;
	}

	public void setAutoIntaking(boolean isAutoIntaking)
	{
		this.isAutoIntaking = isAutoIntaking;
	}
	
	public boolean isPivoting()
	{
		return isPivoting;
	}
	
	public void setPivoting(boolean isPivoting)
	{
		this.isPivoting = isPivoting;
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
		
		private enum InitRange
		{
			floorRange(0, 20),
			floorMiddleRange(20, 2090),
			middleRange(2090, 3010),
			middleRaisedRange(3010, 3980),
			raisedRange(5980, 6000),
			none(-1, -1),
			error(-1, -1);

			private final int[] range;

			InitRange(int bottomValue, int topValue)
			{
				this.range = new int[] {bottomValue, topValue};
			}

			public int[] range()
			{
				return this.range;
			}

			public double bottomValue()
			{
				return this.range[0];
			}

			public double topValue()
			{
				return this.range[1];
			}
		}

		public enum Range
		{
			floorRange(        InitRange.floorRange.range(),        InitRange.floorRange,  InitRange.middleRange),
			floorMiddleRange(  InitRange.floorMiddleRange.range(),  InitRange.floorRange,  InitRange.middleRange),
			middleRange(       InitRange.middleRange.range(),       InitRange.floorRange,  InitRange.raisedRange),
			middleRaisedRange( InitRange.middleRaisedRange.range(), InitRange.middleRange, InitRange.raisedRange),
			raisedRange(       InitRange.raisedRange.range(),       InitRange.middleRange, InitRange.raisedRange),
			error(             InitRange.error.range(),             InitRange.error,       InitRange.error);

			private final int[] range;
			private final InitRange higherNeighbor;
			private final InitRange lowerNeighbor;

			Range(int[] range, Constants.InitRange lowerNeighbor, Constants.InitRange higherNeighbor)
			{
				this.range = range;
				this.higherNeighbor = higherNeighbor;
				this.lowerNeighbor = lowerNeighbor;
			}

			public int[] range()
			{
				return this.range;
			}

			public double bottomValue()
			{
				return this.range[0];
			}

			public double topValue()
			{
				return this.range[1];
			}

			public InitRange lowerNeighbor()
			{
				return this.lowerNeighbor;
			}

			public InitRange higherNeighbor()
			{
				return this.higherNeighbor;
			}
		}

		//TODO:change numbers to appropriate values
		public static final int AUTO_EJECT_ENCODER_STOP_VALUE = 1;
		public static final int AUTO_INTAKE_ENCODER_STOP_VALUE = 500;

		public static final int ENCODER_FLOOR_POSITION = 0;
		public static final int ENCODER_MIDDLE_POSITION = 3000;
		public static final int ENCODER_RAISED_POSITION = 6000;

		public static final int LEFT_INTAKE_MOTOR_PORT = 0;
		public static final int RIGHT_INTAKE_MOTOR_PORT = 1;
		public static final int pivoter_MOTOR_PORT = 1;

		public static final int PID_SLOT_ID = 0;
	}

}