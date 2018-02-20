package org.usfirst.frc.team4237.robot.components;

import java.util.HashMap;

import org.usfirst.frc.team4237.robot.components.Gripper.Constants;
import org.usfirst.frc.team4237.robot.sensors.LimitSwitch;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4237.robot.control.OperatorXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * 
 * @author Ben Puzycki, Darryl Wong, Mark Washington
 *
 */
public class Elevator extends Thread
{
	private OperatorXbox xbox = OperatorXbox.getInstance();

	private WPI_TalonSRX masterTalonSRX = new WPI_TalonSRX(Constants.MASTER_MOTOR_PORT); 
	private WPI_TalonSRX slaveTalonSRX = new WPI_TalonSRX(Constants.SLAVE_MOTOR_PORT);

	private HashMap<Integer, WPI_TalonSRX> talonSRXHashMap = new HashMap<Integer, WPI_TalonSRX>();
	
	private AnalogPotentiometer stringPot = new AnalogPotentiometer(Constants.STRING_POT_PORT);
	private AnalogPotentiometer stringPot4 = new AnalogPotentiometer(4);
	private AnalogPotentiometer stringPot5 = new AnalogPotentiometer(5);
	private AnalogPotentiometer stringPot7 = new AnalogPotentiometer(7);

	private double currentValue;
	private double[] targetRange = Constants.Range.error.range;

	private Constants.Range currentRange;
	private Constants.Direction currentDirection = Constants.Direction.None;
	
	private boolean isMoving = false;
	private boolean inTargetRange = false;

	private Constants.InitRange autoTargetRange = Constants.InitRange.error;
	
	private int currentTestKeyPosition = 0;
	
	//Joystick buttons
	private boolean leftBumper;
	private boolean rightBumper;
	private boolean aButton;
	
	private boolean wasLeftBumper;
	private boolean wasRightBumper;
	private boolean wasAButton;
	
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

		masterTalonSRX.configForwardSoftLimitThreshold(612, 0);
		masterTalonSRX.configReverseSoftLimitThreshold(118, 0);
		masterTalonSRX.configForwardSoftLimitEnable(true, 0);
		masterTalonSRX.configReverseSoftLimitEnable(true, 0);

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
	

	@Override
	public synchronized void run()
	{
		while (!this.interrupted())
		{	
			updateCurrentRange();

			if (DriverStation.getInstance().isOperatorControl())
			{
				teleop();
			}
			else if (DriverStation.getInstance().isAutonomous())
			{
				autonomous();
			}
			else if (DriverStation.getInstance().isTest())
			{
				test();
			}
			Timer.delay(0.005);
		} //End of while loop
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
			else if (leftYAxis < -0.5)
			{
				raise();
			}
			else if (leftYAxis > 0.5)
			{
				lower();
			}
			else
			{
				stopMoving();
			}
		}
		
		if(isMoving())
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
		
			if ( (currentValue >= targetRange[0]) && (currentValue < targetRange[1]) )
			{
				System.out.println("In target range");
				isMoving = false;
				currentDirection = Constants.Direction.None;
				stopMoving();
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
	}
	
	public void autonomous()
	{
		inTargetRange = false;
		
		if ((currentValue >= targetRange[0]) && (currentValue <= targetRange[1]))
		{
			System.out.println("In target range");
			currentDirection = Constants.Direction.None;
			stopMoving();
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
	
	public void test()
	{
		leftBumper = xbox.getRawButton(Xbox.Constants.LEFT_BUMPER);
		rightBumper = xbox.getRawButton(Xbox.Constants.RIGHT_BUMPER);
		aButton = xbox.getRawButton(Xbox.Constants.A_BUTTON);
		
		if (leftBumper && !wasLeftBumper) currentTestKeyPosition++;
		else if (rightBumper && !wasRightBumper) currentTestKeyPosition--;
		
		if (currentTestKeyPosition >= talonSRXHashMap.keySet().size()) currentTestKeyPosition = talonSRXHashMap.size() - 1;
		else if (currentTestKeyPosition < 0) currentTestKeyPosition = 0;
		
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
		
		wasLeftBumper = leftBumper;
		wasRightBumper = rightBumper;
	}

	public void updateCurrentRange()
	{
		currentValue = getStringPot();
//		printPotentiometers();
		System.out.println("Potentiometer Value = " + currentValue);
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
		else if (currentValue <= Constants.Range.topScaleRange.topValue())
		{
			currentRange = Constants.Range.topScaleRange;
//			System.out.println("Current Range: " + Constants.Range.topScaleRange);
		}
		else
		{
//			System.out.println("Something went terribly wrong");
//			currentRange = Constants.Range.error;
		}
	}

	public Constants.Range getCurrentRange()
	{
		return currentRange;
	}

	public void stopMoving()
	{
		masterTalonSRX.set(0.0);
		setMoving(false);
	}

	public synchronized double getStringPot()
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
		targetRange = Constants.Range.topScaleRange.range;
	}
	
	public void autoSetSwitchTargetRange()
	{
		targetRange = Constants.Range.exchangeAndSwitchAndPortalRange.range;
	}
	
	public void printPotentiometers()
	{
		System.out.printf("Port4 = %f1.4 Port5 = %f1.4 Port6 = %f1.4 Port7 = %f1.4\n",  stringPot4.get(), stringPot5.get(), stringPot.get(), stringPot7.get());
	}
	
	public void autoSetFloorTargetRange()
	{
		targetRange = Constants.Range.floorRange.range;
	}

	public static class Constants
	{		
		private enum InitRange
		{
			
			floorRange(118, 128),
			floorExchangeAndSwitchAndPortalRange(118, 228),
			exchangeAndSwitchAndPortalRange(228, 248),
			exchangeAndSwitchAndPortalBottomScaleRange(248, 416),
			bottomScaleRange(416, 436),
			bottomScaleTopScaleRange(436, 578),
			topScaleRange(578, 588),
			none(-1, -1),
			error(-1, -1);
			
			/*floorRange(),
			floorExchangeAlongWithPortalRangeAsWellToo(),
			exchangeIncludingWithTheZonePortalRangeAreaPlace(),
			exchangeAlongWithPortalRangeAsWellTooSwitchRange(0.36661538461539, 0.41953846153846),
			switchRange(0.41953846153846, 0.47246153846154),
			switchBottomScaleRange(0.57830769230769, 0.63123076923077),
			bottomScaleRange(0.63123076923077, 0.68415384615385),
			bottomScaleTopScaleRange(0.68415384615385, 0.73707692307692),
			topScaleRange(0.73707692307692, .8),
			none(-1, -1),
			error(-1, -1);
			*/

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
			floorRange(     				InitRange.floorRange.range(),                 	InitRange.floorRange,        			InitRange.exchangeAndSwitchAndPortalRange),
			floorExchangeAndSwitchAndPortalRange(   InitRange.floorExchangeAndSwitchAndPortalRange.range(),	InitRange.floorRange,        			InitRange.exchangeAndSwitchAndPortalRange),
			exchangeAndSwitchAndPortalRange(         		InitRange.exchangeAndSwitchAndPortalRange.range(),				InitRange.floorRange,        			InitRange.bottomScaleRange),
			exchangeAndSwitchAndPortalBottomScaleRange(   	InitRange.exchangeAndSwitchAndPortalBottomScaleRange.range(),	InitRange.exchangeAndSwitchAndPortalRange,       		InitRange.bottomScaleRange),
			bottomScaleRange(           			InitRange.bottomScaleRange.range(),          			InitRange.exchangeAndSwitchAndPortalRange,    			InitRange.topScaleRange), 
			bottomScaleTopScaleRange(   			InitRange.bottomScaleTopScaleRange.range(),   			InitRange.bottomScaleRange,  					InitRange.topScaleRange),
			topScaleRange(              			InitRange.topScaleRange.range(),              			InitRange.bottomScaleRange,  					InitRange.topScaleRange),
			error(                      			InitRange.error.range(),                      			InitRange.error,             					InitRange.error);

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

		public static final int ACCEPTABLE_TICK_RANGE = 10;

		public static final int MASTER_MOTOR_PORT = 10;
		public static final int SLAVE_MOTOR_PORT = 11;
		public static final int STRING_POT_PORT = 3;
		public static final int OTHER_STRING_POT_PORT = 7;

		public static final double STRING_POT_SCALE = 1.0;

		public static final double SPEED = 0.5;
	}
}

		