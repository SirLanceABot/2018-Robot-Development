package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc.team4237.robot.components.Elevator.Constants;
import org.usfirst.frc.team4237.robot.control.OperatorXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/**
 * Gripper class
 * @author Julien Thrum, Erin Lafrenz, Ben Puzycki, Darryl Wong, and Mark Washington
 */
public class Gripper extends Thread
{
	private OperatorXbox xbox = OperatorXbox.getInstance();

	private WPI_TalonSRX leftIntakeTalon = new WPI_TalonSRX(Constants.LEFT_INTAKE_MOTOR_PORT);
	private WPI_TalonSRX rightIntakeTalon = new WPI_TalonSRX(Constants.RIGHT_INTAKE_MOTOR_PORT);
	private WPI_TalonSRX pivotTalon = new WPI_TalonSRX(Constants.PIVOTER_MOTOR_PORT);
	
	private boolean isAutoEjecting = false;
	private boolean isAutoIntaking = false;
	private boolean isPivoting = false;
	
	private int currentValue = 0;
	
	private Constants.Range currentRange = Constants.Range.error;
	private Constants.Range autoTargetRange = Constants.Range.error;
	private int[] targetRange = Constants.Range.error.range;
	private Constants.Direction currentDirection = Constants.Direction.None;
	

	
	//Quarantine
	boolean isIntakeDone = true;
	boolean isPivoterRaisedDone = true;
	boolean isPivoterMiddleDone = true;
	boolean isPivoterFloorDone = true;

	boolean isAutoPivotRaisedDone = false;
	boolean isAutoPivotMiddleDone = false;
	boolean isAutoPivotFloorDone = false;
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
		rightIntakeTalon.setInverted(false);

		//Pivoter settings
		pivotTalon.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		pivotTalon.setSensorPhase(false);
		pivotTalon.configForwardSoftLimitEnable(false, 0);
		pivotTalon.configReverseSoftLimitEnable(false, 0);

		//PID settings
//		leftIntakeTalon.selectProfileSlot(Constants.PID_SLOT_ID, 0);
//		leftIntakeTalon.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
//		leftIntakeTalon.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
//		leftIntakeTalon.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
//		leftIntakeTalon.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
//
//		rightIntakeTalon.selectProfileSlot(Constants.PID_SLOT_ID, 0);
//		rightIntakeTalon.config_kP(Constants.PID_SLOT_ID, 2.2, 1);
//		rightIntakeTalon.config_kI(Constants.PID_SLOT_ID, 0.01, 1);
//		rightIntakeTalon.config_kD(Constants.PID_SLOT_ID, 0.0001, 1);
//		rightIntakeTalon.config_kF(Constants.PID_SLOT_ID, 4.3, 1);
	}
	public void resetEncoders()
	{
		pivotTalon.setSelectedSensorPosition(0,0,0);
	}

	
	/**
	 * Intake function for autonomous
	 * @return State of intake, whether it's done or not
	 */

	public boolean autoIntake()
	{
		if(!isAutoIntaking())
		{
			rightIntakeTalon.setSelectedSensorPosition(0,0,0);		// set encoder position
			setAutoIntaking(true);
			intake();
			//leftIntakeTalon.setSelectedSensorPosition(0,0,0);

//			leftIntakeTalon.set(ControlMode.Velocity, 1);
//			rightIntakeTalon.set(ControlMode.Velocity, 1);		// set motor
		}
		if(rightIntakeTalon.getSelectedSensorPosition(0) >= Constants.AUTO_INTAKE_ENCODER_STOP_VALUE)
		{
			intakeOff();
			setAutoIntaking(false);
		}
		return isAutoIntaking();

	}
	public double autoEncoder()
	{
		return rightIntakeTalon.getSelectedSensorPosition(0);
	}


	/** 
	 * Eject function for autonomous
	 * @return State of eject, whether it's done or not
	 */
	public boolean autoEject()
	{
		if(!isAutoEjecting())
		{
			rightIntakeTalon.setSelectedSensorPosition(0, 0, 0);		// set encoder position
			setAutoEjecting(true);
			eject();

//			leftIntakeTalon.set(ControlMode.Velocity, -1);
//			rightIntakeTalon.set(ControlMode.Velocity, -1);
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

//		leftIntakeTalon.set(ControlMode.PercentOutput, 0);
//		rightIntakeTalon.set(ControlMode.PercentOutput, 0);
	
		leftIntakeTalon.set(0);
		rightIntakeTalon.set(0);
	}

	/**
	 * Intake for use during tele-op
	 */
	public void intake()
	{
		//TODO: Make velocity-based control work
		//leftIntakeTalon.set(ControlMode.Velocity, -0.5);
		//rightIntakeTalon.set(ControlMode.Velocity, -0.5);

//		leftIntakeTalon.set(ControlMode.PercentOutput, -0.5);
//		rightIntakeTalon.set(ControlMode.PercentOutput, -0.5);
		
		leftIntakeTalon.set(-0.30);
		rightIntakeTalon.set(-0.30);
		
	}

	/**
	 * Eject for use during tele-op
	 */
	public void eject()
	{
		//leftIntakeTalon.set(ControlMode.Velocity, 500);
		//rightIntakeTalon.set(ControlMode.Velocity, 500);

//		leftIntakeTalon.set(ControlMode.PercentOutput, 0.5);
//		rightIntakeTalon.set(ControlMode.PercentOutput, 0.5);
		
		leftIntakeTalon.set(1.0);
		rightIntakeTalon.set(1.0);
	}
	

	/**
	 * Spins cube clockwise into left arm
	 */
	public void intakeRotateCubeLeft()
	{
		leftIntakeTalon.set(0.0);
		rightIntakeTalon.set(-0.70);
	}
	
	/**
	 * Spins cube counterclockwise into right arm
	 */
	public void intakeRotateCubeRight()
	{
		leftIntakeTalon.set(-0.70);
		rightIntakeTalon.set(0.0);
	}
	
	/**
	 * Move pivot arm at selected speed
	 * @param value Value from joystick
	 */
	public void pivot(double value)
	{
		setPivoting(true);
		pivotTalon.set(value);
	}
	
	/**
	 * Lower pivoter
	 */
	public void raise()
	{	
		pivot(0.4);
	}
	
	/**
	 * Lower pivoter
	 */
	public void lower()
	{
		pivot(-0.4);
	}
		
	/**
	 * Stop pivoting
	 */
	public void pivotOff()
	{
		setPivoting(false);
		pivotTalon.set(0.0);
	}

	@Override
	public void run()
	{
		while (!this.interrupted())
		{
			//Pivot
			boolean aButton = xbox.getRawButton(Xbox.Constants.A_BUTTON);		//Pivot down one level
			boolean bButton = xbox.getRawButton(Xbox.Constants.B_BUTTON);		//Spin Cube right
			boolean yButton = xbox.getRawButton(Xbox.Constants.Y_BUTTON);		//Pivot up one level
			boolean xButton = xbox.getRawButton(Xbox.Constants.X_BUTTON);		//Spin Cube left
			
			double rightYAxis = xbox.getRawAxis(Xbox.Constants.RIGHT_STICK_Y_AXIS);					//Free moving pivot
			
			
			//Intake
			double rightTrigger = xbox.getRawAxis(Xbox.Constants.RIGHT_TRIGGER_AXIS);		//Eject
			double leftTrigger = xbox.getRawAxis(Xbox.Constants.LEFT_TRIGGER_AXIS);			//Intake
				
			
			//Updates Current Range
			updateCurrentRange();
			System.out.println(currentRange);
			
			System.out.println("Encoder Postion" + getPivotEncoder());
			
			//Move pivot arm
			if (!isPivoting())
			{

				//Some alternative code to make it work like the elevator

				if (yButton)
				{
					targetRange = currentRange.higherNeighbor().range();
					//setPivoting(true);
					currentDirection = Constants.Direction.Up;
				}
				else if (aButton)
				{
					targetRange = currentRange.lowerNeighbor().range();
					//setPivoting(true);
					currentDirection = Constants.Direction.Down;
				}
				else if (rightYAxis < -0.5)
				{
					raise();
				}
				else if (rightYAxis > 0.5)
				{
					lower();
				}
				else if (Math.abs(rightYAxis) < 0.5)
				{
					pivotOff();
				}
				
			}
			else if(isPivoting() && !DriverStation.getInstance().isAutonomous())
			{

				System.out.println("Is Pivoting");
				if (currentDirection == Constants.Direction.Up)
				{
					if (yButton)
					{
						targetRange = currentRange.higherNeighbor.range();
					}
					else if (aButton)
					{
						currentDirection = Constants.Direction.None;
						pivotOff();
					}
				}
				else if (currentDirection == Constants.Direction.Down) 
				{
					if (aButton)
					{
						targetRange = currentRange.lowerNeighbor.range();
					}
					else if (yButton)
					{
						currentDirection = Constants.Direction.None;
						pivotOff();
					}
				}


				if ( (currentValue >= targetRange[0]) && (currentValue < targetRange[1]) )
				{
					System.out.println("In target range");
					isPivoting = false;
					currentDirection = Constants.Direction.None;
					pivotOff();
				}
				else if (currentDirection == Constants.Direction.Up)
				{
					System.out.println("Raising");
					raise();
				}
				else if (currentDirection == Constants.Direction.Down)
				{
					lower();
					System.out.println("Lowering");
				}
			}
			else if (DriverStation.getInstance().isAutonomous())
			{

				if ( (currentValue >= targetRange[0]) && (currentValue <= targetRange[1]) )
				{
					System.out.println("In target range");
					currentDirection = Constants.Direction.None;
				}
				else if (currentValue < targetRange[0])
				{
					System.out.println("Raising");
					raise();
				}
				else if (currentValue > targetRange[1])
				{
					lower();
					System.out.println("Lowering");
				}
			}
			
			//Intake
			if (Math.abs(rightTrigger) > 0.3)
			{
				eject();
			}
			else if (Math.abs(leftTrigger) > 0.3)
			{
				intake();
			}
			else if (Math.abs(leftTrigger) > 0.3 && bButton)
			{
				intakeRotateCubeLeft();
			}
			else if (Math.abs(leftTrigger) > 0.3 && xButton)
			{
				intakeRotateCubeRight();
			}
			else
			{
				intakeOff();
			}
		
				
				//
//				if (aButton || autoTargetRange == Constants.Range.floorRange)
//				{
//		 			targetRange = Constants.Range.floorRange.range();
//					setPivoting(true);
//					currentDirection = Constants.Direction.Down;
//					if ((currentValue >= targetRange[0]) && (currentValue < targetRange[1]))
//					{
//						isAutoPivotFloorDone = true;
//					}
//				}
//				else if (bButton || autoTargetRange == Constants.Range.middleRange)
//				{
//					targetRange = Constants.Range.middleRange.range();
//					setPivoting(true);
//					if (currentValue < Constants.Range.middleRange.bottomValue())
//					{
//						currentDirection = Constants.Direction.Up;
//					}
//					else if (currentValue > Constants.Range.middleRange.topValue())
//					{
//						currentDirection = Constants.Direction.Down;
//					}
//					else
//					{
//						currentDirection = Constants.Direction.None;
//					}
//					
//					if ((currentValue >= targetRange[0]) && (currentValue < targetRange[1]))
//					{
//						isAutoPivotMiddleDone = true;
//					}
//				}
//				else if (yButton || autoTargetRange == Constants.Range.raisedRange)
//				{
//					targetRange = Constants.Range.raisedRange.range();
//					setPivoting(true);
//					currentDirection = Constants.Direction.Up;
//					if ((currentValue >= targetRange[0]) && (currentValue < targetRange[1]))
//					{
//						isAutoPivotRaisedDone = true;
//					}
//				}
//				
//				
//				if (Math.abs(rightTrigger) > 0.3)
//				{
//					eject();
//				}
//				else if (Math.abs(leftTrigger) > 0.3)
//				{
//					intake();
//				}
//				else if (Math.abs(leftTrigger) > 0.3 && bButton)
//				{
//					intakeRotateCubeLeft();
//				}
//				else if (Math.abs(leftTrigger) > 0.3 && xButton)
//				{
//					intakeRotateCubeRight();
//				}
//				else
//				{
//					intakeOff();
//				}
//				
//			}
//			
//			//Keeps pivot arm pivoting until target position is reached and switches direction of pivot arm if target position is skipped forwhatever reason
//			if(isPivoting())
//			{
//				if (((currentDirection == Constants.Direction.Down) && (currentValue <= targetRange[1])) || 
//					((currentDirection == Constants.Direction.Up) && (currentValue >= targetRange[0])) ||
//					(xButton))
//				{
//					System.out.println("In target range");
//					setPivoting(false);
//					currentDirection = Constants.Direction.None;
//					pivotOff();
//				}
//				else if (currentDirection == Constants.Direction.Up)
//				{
//					System.out.println("Raising");
//					raise();
//				}
//				else if (currentDirection == Constants.Direction.Down)
//				{
//					System.out.println("Lowering");
//					lower();
//				}
			//}
			//End of code copied from elevator
			
			
			Timer.delay(0.005);
		}
	}
	
	//Updates the current range depending on the encoder value
	public void updateCurrentRange()
	{
		currentValue = getPivotEncoder();
		if (currentValue <= Constants.Range.floorRange.topValue())
		{
			currentRange = Constants.Range.floorRange;
		}
		else if (currentValue <= Constants.Range.floorHorizontalRange.topValue())
		{
			currentRange = Constants.Range.floorHorizontalRange;
		}
		else if (currentValue <= Constants.Range.horizontalRange.topValue())
		{
			currentRange = Constants.Range.horizontalRange;
		}
		else if (currentValue <= Constants.Range.horizontalMiddleRange.topValue())
		{
			currentRange = Constants.Range.horizontalMiddleRange;
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

	public boolean isAutoMiddlePivotDone()
	{
		return isAutoPivotMiddleDone;
	}
	
	public boolean isAutoFloorPivotDone()
	{
		return isAutoPivotFloorDone;
	}
	
	public boolean isAutoRaisedPivotDone()
	{
		return isAutoPivotRaisedDone;
	}
	
	public int[] getVelocityArray()
	{
		return new int[] {leftIntakeTalon.getSelectedSensorVelocity(0), rightIntakeTalon.getSelectedSensorVelocity(0)};
	}
	
	public int getPivotEncoder()
	{
		return pivotTalon.getSelectedSensorPosition(0);
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
	
	public void autoSetRaisedTargetRange()
	{
		targetRange = Constants.Range.raisedRange.range;
	}
	
	public void autoSetMiddleTargetRange()
	{
		targetRange = Constants.Range.middleRange.range;
	}
	
	public void autoSetHorizontalTargetRange()
	{
		targetRange = Constants.Range.horizontalRange.range;
	}
	
	public void autoSetFloorTargetRange()
	{
		targetRange = Constants.Range.floorRange.range;
	}
	
	public Constants.Range getCurrentRange()
	{
		return this.currentRange;
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
			floorHorizontalRange(20, 2090),
			horizontalRange(2990, 3010),
			horizontalMiddleRange(3010,3980),
			middleRange(5980, 6000),
			middleRaisedRange(6000, 6500),
			raisedRange(6500, 6510),
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
		
		//Sets lower and upper limits for each range
		public enum Range
		{
			floorRange(       		InitRange.floorRange.range(),        		InitRange.floorRange, 		InitRange.horizontalRange),
			floorHorizontalRange(  	InitRange.floorHorizontalRange.range(),		InitRange.floorRange,  		InitRange.horizontalRange),
			horizontalRange(		InitRange.horizontalRange.range(), 			InitRange.floorRange,		InitRange.middleRange),
			horizontalMiddleRange(	InitRange.horizontalMiddleRange.range(),	InitRange.horizontalRange,	InitRange.middleRange),
			middleRange(       		InitRange.middleRange.range(),       		InitRange.horizontalRange,  InitRange.raisedRange),
			middleRaisedRange( 		InitRange.middleRaisedRange.range(), 		InitRange.middleRange,		InitRange.raisedRange),
			raisedRange(      		InitRange.raisedRange.range(),       		InitRange.middleRange,		InitRange.raisedRange),
			error(             		InitRange.error.range(),             		InitRange.error,      		InitRange.error);

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

		public static final int AUTO_EJECT_ENCODER_STOP_VALUE = -5000;
		public static final int AUTO_INTAKE_ENCODER_STOP_VALUE = 5000;

		public static final int ENCODER_FLOOR_POSITION = 0;
		public static final int ENCODER_MIDDLE_POSITION = 3000;
		public static final int ENCODER_RAISED_POSITION = 6000;

		public static final int LEFT_INTAKE_MOTOR_PORT = 8;
		public static final int RIGHT_INTAKE_MOTOR_PORT = 9;
		public static final int PIVOTER_MOTOR_PORT = 6;

		public static final int PID_SLOT_ID = 0;
	}

}