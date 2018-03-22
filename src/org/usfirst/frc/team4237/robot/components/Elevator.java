package org.usfirst.frc.team4237.robot.components;

import java.util.HashMap;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team4237.robot.components.Gripper.Constants;
import org.usfirst.frc.team4237.robot.control.OperatorXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * 
 * @author Ben Puzycki, Darryl Wong, Mark Washington
 *
 */
public class Elevator implements Component
{
	private OperatorXbox xbox = OperatorXbox.getInstance();

	private WPI_TalonSRX masterTalonSRX = new WPI_TalonSRX(Constants.MASTER_MOTOR_PORT); 
	private WPI_TalonSRX slaveTalonSRX = new WPI_TalonSRX(Constants.SLAVE_MOTOR_PORT);

	private HashMap<Integer, WPI_TalonSRX> talonSRXHashMap = new HashMap<Integer, WPI_TalonSRX>();

	private double currentValue;
	private double[] targetRange = Constants.Range.floorRange.range;

	private Constants.Range currentRange;
	private Constants.Direction currentDirection = Constants.Direction.None;

	private boolean isMoving = false;

	private Constants.InitRange autoTargetRange = Constants.InitRange.error;

	private int currentTestKeyPosition = 0;

	//Joystick buttons
	private boolean leftBumper;
	private boolean rightBumper;
	private boolean aButton;

	private static Elevator instance = new Elevator();

	public static Elevator getInstance()
	{
		return instance;
	}


	/**
	 * Constructor for Elevator, called only
	 * once by getInstance(). It initializes
	 * the keys and values in levelTicks and
	 * tickLevels.
	 */
	private Elevator()
	{
		masterTalonSRX.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.Analog, 0, 0);
		//		masterTalonSRX.configSetParameter(ParamEnum.eFeedbackNotContinuous, 1, 0x00, 0x00, 0);
		masterTalonSRX.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 20);
		masterTalonSRX.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 20);

		masterTalonSRX.configForwardSoftLimitThreshold(Constants.ABSOLUTE_TOP, 0);
		masterTalonSRX.configReverseSoftLimitThreshold(Constants.FLOOR, 0);
		masterTalonSRX.configForwardSoftLimitEnable(false, 0);
		masterTalonSRX.configReverseSoftLimitEnable(false, 0);

		slaveTalonSRX.follow(masterTalonSRX); // Sets slaveTalonSRX to follow masterTalonSrx

		talonSRXHashMap.put(Constants.MASTER_MOTOR_PORT, masterTalonSRX);
		talonSRXHashMap.put(Constants.SLAVE_MOTOR_PORT, slaveTalonSRX);
	}

	/**
	 * Raise the elevator
	 */
	public void raise()
	{
		masterTalonSRX.set(1.0);
	}

	/**
	 * Lower the elevator
	 */
	public void lower()
	{
		masterTalonSRX.set(-1.0);
	}


	public void run()
	{
		updateCurrentRange();
		if (DriverStation.getInstance().isOperatorControl() && DriverStation.getInstance().isEnabled())
		{
			teleop();
		}
		else if (DriverStation.getInstance().isAutonomous() && DriverStation.getInstance().isEnabled())
		{
			autonomous();
		}
		else if (DriverStation.getInstance().isTest() && DriverStation.getInstance().isEnabled())
		{
			test();
		}
	}


	public void teleop()
	{
		rightBumper = xbox.getRawButton(Xbox.Constants.RIGHT_BUMPER);
		leftBumper = xbox.getRawButton(Xbox.Constants.LEFT_BUMPER);

		double leftYAxis = xbox.getRawAxis(Xbox.Constants.LEFT_STICK_Y_AXIS);

		if (!isMoving())
		{
			if (rightBumper)
			{
				targetRange = currentRange.higherNeighbor().range();
				isMoving = true;
				currentDirection = Constants.Direction.Up;
			}
			else if (leftBumper)
			{
				targetRange = currentRange.lowerNeighbor().range();
				isMoving = true;
				currentDirection = Constants.Direction.Down;
			}
			else if(Math.abs(leftYAxis) > 0.2)	
			{
				masterTalonSRX.set(-leftYAxis);
			}
			else
			{
				stopMoving();
				xbox.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
			}
		}

		else if(isMoving())
		{
			System.out.println("Is Moving");
			if (currentDirection == Constants.Direction.Up)
			{
				if (rightBumper)
				{
					targetRange = currentRange.higherNeighbor.range();
				}
				else if (leftBumper)
				{
					currentDirection = Constants.Direction.None;
					stopMoving();
				}
			}
			else if (currentDirection == Constants.Direction.Down) 
			{
				if (leftBumper)
				{
					targetRange = currentRange.lowerNeighbor.range();
				}
				else if (rightBumper)
				{
					currentDirection = Constants.Direction.None;
					stopMoving();
				}
			}

			if  ( (currentValue >= targetRange[0] && currentDirection == Constants.Direction.Up) || 
					(currentValue < targetRange[1] && currentDirection == Constants.Direction.Down))
			{
				//System.out.println("Elevator in target range");
				isMoving = false;
				currentDirection = Constants.Direction.None;
				stopMoving();
			}
			else if (currentDirection == Constants.Direction.Up)
			{
				//System.out.println("Elevator raising");
				raise();
			}
			else if (currentDirection == Constants.Direction.Down)
			{
				lower();
				//System.out.println("Elevator lowering");
			}
		}
	}

	public void autonomous()
	{
		updateCurrentRange();
		//		if (currentValue < targetRange[0] && currentDirection == Constants.Direction.Up)
		//		{
		//			//System.out.println("Elevator raising");
		//			raise();
		//		}
		//		else if (currentValue > targetRange[1] && currentDirection == Constants.Direction.Down)
		//		{
		//			lower();
		//			//System.out.println("Elevator lowering");
		//		}
		//		else
		//		{
		//			currentDirection = Constants.Direction.None;
		//			stopMoving();
		//		}
	}

	public void test()
	{
		leftBumper = xbox.getRawButtonPressed(Xbox.Constants.LEFT_BUMPER);
		rightBumper = xbox.getRawButtonPressed(Xbox.Constants.RIGHT_BUMPER);
		aButton = xbox.getRawButton(Xbox.Constants.A_BUTTON);

		if (leftBumper) 
		{
			currentTestKeyPosition--;

		}
		else if (rightBumper) 
		{
			currentTestKeyPosition++;
		}

		if (currentTestKeyPosition >= talonSRXHashMap.keySet().size()) 
		{
			currentTestKeyPosition = talonSRXHashMap.size() - 1;
		}
		else if (currentTestKeyPosition < 0) 
		{
			currentTestKeyPosition = 0;
		}

		if (aButton)
		{
			talonSRXHashMap.get(currentTestKeyPosition).set(ControlMode.PercentOutput, 0.3);
		}
		else
		{
			for (int i : talonSRXHashMap.keySet())
			{
				talonSRXHashMap.get(i).set(ControlMode.PercentOutput, 0.0);
			}
		}
		printTestInfo();
	}
	/**
	 * Test Code
	 */

	public void updateCurrentRange()
	{
		currentValue = getStringPot();
		//System.out.println("Elevator pot: " + currentValue);
		if (currentValue <= Constants.Range.floorRange.topValue())
		{
			currentRange = Constants.Range.floorRange;
			//			System.out.println("Current Range: " + Constants.Range.floorRange);
		}
		else if (currentValue <= Constants.Range.floorExchangeAndSwitchAndPortalRange.topValue())
		{
			currentRange = Constants.Range.floorExchangeAndSwitchAndPortalRange;
			//			System.out.println("Current Range: " + Constants.Range.floorAndSwitchExchangeAndPortalRange);
		}
		else if (currentValue <= Constants.Range.exchangeAndSwitchAndPortalRange.topValue())
		{
			currentRange = Constants.Range.exchangeAndSwitchAndPortalRange;
			//			System.out.println("Current Range: " + Constants.Range.exchangeAndPortalRange);
		}
		else if (currentValue <= Constants.Range.exchangeAndSwitchAndPortalBottomScaleRange.topValue())
		{
			currentRange = Constants.Range.exchangeAndSwitchAndPortalBottomScaleRange;
			//			System.out.println("Current Range: " + Constants.Range.exchangeAndPortalSwitchRange);
		}
		else if (currentValue <= Constants.Range.bottomScaleRange.topValue())
		{
			currentRange = Constants.Range.bottomScaleRange;
			//			System.out.println("Current Range: " + Constants.Range.bottomScaleRange);
		}
		else if (currentValue <= Constants.Range.bottomScaleTopScaleRange.topValue())
		{
			currentRange = Constants.Range.bottomScaleTopScaleRange;
			//			System.out.println("Current Range: " + Constants.Range.bottomScaleTopScaleRange);
		}
		else //if (currentValue <= Constants.Range.topScaleRange.topValue())
		{
			currentRange = Constants.Range.topScaleRange;
			//			System.out.println("Current Range: " + Constants.Range.topScaleRange);
		}

	}

	public Constants.Range getCurrentRange()
	{
		return currentRange;
	}

	public void stopMoving()
	{
		currentDirection = Constants.Direction.None;
		masterTalonSRX.set(0.0);
		setMoving(false);
	}

	public double getStringPot()
	{
		return masterTalonSRX.getSelectedSensorPosition(0);
	}

	public boolean isMoving()
	{
		return isMoving;
	}

	public void setMoving(boolean isMoving)
	{
		this.isMoving = isMoving;
	}

	public boolean inTargetRange()
	{
		return  (currentValue >= targetRange[0]) && (currentValue <= targetRange[1]);
	}

	public void autoSetScaleTargetRange()
	{
		currentDirection = Constants.Direction.Up;
		targetRange = Constants.Range.topScaleRange.range();
	}

	public void autoSetSwitchTargetRange()
	{
		targetRange = Constants.Range.exchangeAndSwitchAndPortalRange.range;
		currentDirection = Constants.Direction.Up;
	}

	public void autoSetFloorTargetRange()
	{
		currentDirection = Constants.Direction.Up;
		targetRange = Constants.Range.floorRange.range;
	}

	public void printTestInfo()
	{
		//System.out.printf("ID: %2d Potentiometer Position: %.2f", talonSRXHashMap.keySet().toArray()[currentTestKeyPosition], getStringPot());
		System.out.println("[Elevator] String-potentiometer position: " + getStringPot());

	}

	public boolean autoFloor()
	{
		boolean inFloorRange = false;
		updateCurrentRange();
		if(currentValue < Constants.FLOOR + Constants.THRESHOLD)
		{
			lower();
		}
		else
		{
			inFloorRange = true;
			stopMoving();
		}

		return inFloorRange;
	}

	public boolean autoSwitch()
	{
		boolean inSwitchRange = false;
		updateCurrentRange();
		if(currentValue < Constants.SWITCH - Constants.THRESHOLD)
		{
			raise();
		}
		else
		{
			inSwitchRange = true;
			stopMoving();
		}

		return inSwitchRange;
	}

	public boolean autoTopScale()
	{
		boolean inScaleRange = false;
		updateCurrentRange();
		if(currentValue < Constants.TOP_SCALE - Constants.THRESHOLD)
		{
			raise();
		}
		else
		{
			inScaleRange = true;
			stopMoving();
		}

		return inScaleRange;
	}

	public static class Constants
	{		
		private enum InitRange
		{
			//			floorRange(118, 138),
			//			floorExchangeAndSwitchAndPortalRange(138, 218),
			//			exchangeAndSwitchAndPortalRange(218, 258),
			//			exchangeAndSwitchAndPortalBottomScaleRange(258, 406),
			//			bottomScaleRange(406, 446),
			//			bottomScaleTopScaleRange(446, 568),
			//			topScaleRange(568, 588),
			//			none(-1, -1),
			//			error(-1, -1);

			floorRange(Constants.FLOOR, Constants.FLOOR + Constants.THRESHOLD),
			floorExchangeAndSwitchAndPortalRange(Constants.FLOOR + Constants.THRESHOLD, Constants.SWITCH - Constants.THRESHOLD),
			exchangeAndSwitchAndPortalRange(Constants.SWITCH - Constants.THRESHOLD, Constants.SWITCH + Constants.THRESHOLD),
			exchangeAndSwitchAndPortalBottomScaleRange(Constants.SWITCH + Constants.THRESHOLD, Constants.BOTTOM_SCALE - Constants.THRESHOLD),
			bottomScaleRange(Constants.BOTTOM_SCALE - Constants.THRESHOLD, Constants.BOTTOM_SCALE + Constants.THRESHOLD),
			bottomScaleTopScaleRange(Constants.BOTTOM_SCALE + Constants.THRESHOLD, Constants.TOP_SCALE - Constants.THRESHOLD),
			topScaleRange(Constants.TOP_SCALE - Constants.THRESHOLD, Constants.TOP_SCALE),
			none(-1, -1),
			error(-1, -1);



			private final double[] range;

			InitRange(double bottomValue, double topValue)
			{
				this.range = new double[] {bottomValue, topValue};
			}

			public double[] range()
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
			floorRange(     								InitRange.floorRange.range(),                 					InitRange.floorRange,        				InitRange.exchangeAndSwitchAndPortalRange),
			floorExchangeAndSwitchAndPortalRange(   		InitRange.floorExchangeAndSwitchAndPortalRange.range(),			InitRange.floorRange,        				InitRange.exchangeAndSwitchAndPortalRange),
			exchangeAndSwitchAndPortalRange(         		InitRange.exchangeAndSwitchAndPortalRange.range(),				InitRange.floorRange,        				InitRange.bottomScaleRange),
			exchangeAndSwitchAndPortalBottomScaleRange(   	InitRange.exchangeAndSwitchAndPortalBottomScaleRange.range(),	InitRange.exchangeAndSwitchAndPortalRange,  InitRange.bottomScaleRange),
			bottomScaleRange(           					InitRange.bottomScaleRange.range(),          					InitRange.exchangeAndSwitchAndPortalRange, 	InitRange.topScaleRange), 
			bottomScaleTopScaleRange(   					InitRange.bottomScaleTopScaleRange.range(),   					InitRange.bottomScaleRange,  				InitRange.topScaleRange),
			topScaleRange(              					InitRange.topScaleRange.range(),              					InitRange.bottomScaleRange,  				InitRange.topScaleRange),
			error(                      					InitRange.error.range(),                      					InitRange.error,             				InitRange.error);

			private final double[] range;
			private final InitRange higherNeighbor;
			private final InitRange lowerNeighbor;

			Range(double[] range, Constants.InitRange lowerNeighbor, Constants.InitRange higherNeighbor)
			{
				this.range = range;
				this.higherNeighbor = higherNeighbor;
				this.lowerNeighbor = lowerNeighbor;
			}

			public double[] range()
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

		public enum Direction
		{
			Up,
			Down,
			None
		}

		public static final int ACCEPTABLE_TICK_RANGE = 5;

		public static final int MASTER_MOTOR_PORT = 10;
		public static final int SLAVE_MOTOR_PORT = 11;
		public static final int STRING_POT_PORT = 3;
		public static final int OTHER_STRING_POT_PORT = 7;

		public static final double STRING_POT_SCALE = 1.0;

		public static final double SPEED = 0.5;

		public static final int THRESHOLD = 15;
		public static final int FLOOR = 135;
		public static final int SWITCH = 330;
		public static final int BOTTOM_SCALE = 426;
		public static final int TOP_SCALE = 605;
		public static final int ABSOLUTE_TOP = 620;

	}
}

