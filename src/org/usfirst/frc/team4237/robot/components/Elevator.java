package org.usfirst.frc.team4237.robot.components;

import java.util.HashMap;

import org.usfirst.frc.team4237.robot.components.Gripper.Constants;
import org.usfirst.frc.team4237.robot.sensors.LimitSwitch;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4237.robot.control.OperatorXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;

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

	private AnalogPotentiometer stringPot = new AnalogPotentiometer(Constants.STRING_POT_PORT);

	private double currentValue;
	private Constants.Range currentRange;
	private double[] targetRange = Constants.Range.error.range;
	private Constants.Direction currentDirection = Constants.Direction.None;
	private boolean isMoving = false;
	private boolean inTargetRange = false;

	
	private Constants.InitRange autoTargetRange = Constants.InitRange.error;
	
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
		masterTalonSRX.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 20);
		masterTalonSRX.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 20);
		
		masterTalonSRX.configForwardSoftLimitEnable(false, 20);
		masterTalonSRX.configReverseSoftLimitEnable(false, 20);

		slaveTalonSRX.follow(masterTalonSRX); // Sets slaveTalonSRX to follow masterTalonSrx
	}

	/**
	 * Raise the elevator
	 */
	public void raise()
	{
		masterTalonSRX.set(0.5);
	}

	/**
	 * Lower the elevator
	 */
	public void lower()
	{
		masterTalonSRX.set(-0.5);
	}
	

	@Override
	public synchronized void run()
	{
		while (!this.interrupted())
		{
			boolean rightBumper = xbox.getRawButton(Xbox.Constants.RIGHT_BUMPER);
			boolean leftBumper = xbox.getRawButton(Xbox.Constants.LEFT_BUMPER);
			
			updateCurrentRange();

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
			}
			
			if(isMoving() && !DriverStation.getInstance().isAutonomous())
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
			else if (DriverStation.getInstance().isAutonomous())
			{
				inTargetRange = false;
				
				if ( (currentValue >= targetRange[0]) && (currentValue <= targetRange[1]) )
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
			Timer.delay(0.005);
		} // End of while loop
	}

	public void updateCurrentRange()
	{
		currentValue = getStringPot();
		if (currentValue <= Constants.Range.floorRange.topValue())
		{
			currentRange = Constants.Range.floorRange;
			System.out.println("Current Range: " + Constants.Range.floorRange);
		}
		else if (currentValue <= Constants.Range.floorExchangeRange.topValue())
		{
			currentRange = Constants.Range.floorExchangeRange;
			System.out.println("Current Range: " + Constants.Range.floorExchangeRange);
		}
		else if (currentValue <= Constants.Range.exchangeRange.topValue())
		{
			currentRange = Constants.Range.exchangeRange;
			System.out.println("Current Range: " + Constants.Range.exchangeRange);
		}
		else if (currentValue <= Constants.Range.exchangePortalRange.topValue())
		{
			currentRange = Constants.Range.exchangePortalRange;
			System.out.println("Current Range: " + Constants.Range.exchangePortalRange);
		}
		else if (currentValue <= Constants.Range.portalRange.topValue())
		{
			currentRange = Constants.Range.portalRange;
			System.out.println("Current Range: " + Constants.Range.portalRange);
		}
		else if (currentValue <= Constants.Range.portalBottomSwitchRange.topValue())
		{
			currentRange = Constants.Range.portalBottomSwitchRange;
			System.out.println("Current Range: " + Constants.Range.portalBottomSwitchRange);
		}
		else if (currentValue <= Constants.Range.bottomSwitchRange.topValue())
		{
			currentRange = Constants.Range.bottomSwitchRange;
			System.out.println("Current Range: " + Constants.Range.bottomSwitchRange);
		}
		else if (currentValue <= Constants.Range.bottomSwitchTopSwitchRange.topValue())
		{
			currentRange = Constants.Range.bottomSwitchTopSwitchRange;
			System.out.println("Current Range: " + Constants.Range.bottomSwitchTopSwitchRange);
		}
		else if (currentValue <= Constants.Range.topSwitchRange.topValue())
		{
			currentRange = Constants.Range.topSwitchRange;
			System.out.println("Current Range: " + Constants.Range.topSwitchRange);
		}
		else if (currentValue <= Constants.Range.topSwitchBottomScaleRange.topValue())
		{
			currentRange = Constants.Range.topSwitchBottomScaleRange;
			System.out.println("Current Range: " + Constants.Range.topSwitchBottomScaleRange);
		}
		else if (currentValue <= Constants.Range.bottomScaleRange.topValue())
		{
			currentRange = Constants.Range.bottomScaleRange;
			System.out.println("Current Range: " + Constants.Range.bottomScaleRange);
		}
		else if (currentValue <= Constants.Range.bottomScaleTopScaleRange.topValue())
		{
			currentRange = Constants.Range.bottomScaleTopScaleRange;
			System.out.println("Current Range: " + Constants.Range.bottomScaleTopScaleRange);
		}
		else if (currentValue <= Constants.Range.topScaleRange.topValue())
		{
			currentRange = Constants.Range.topScaleRange;
			System.out.println("Current Range: " + Constants.Range.topScaleRange);
		}
		else
		{
			System.out.println("Something went terribly wrong");
			currentRange = Constants.Range.error;
		}
	}

	public Constants.Range getCurrentRange()
	{
		return currentRange;
	}

	public void stopMoving()
	{
		masterTalonSRX.set(0.0);
		isMoving = false;
	}

	public synchronized double getStringPot()
	{
		double distance;

		distance = stringPot.get() * Constants.STRING_POT_SCALE; // Multiply voltage by constant to get the distance
		
		if (distance > 1.0)
		{
			return 1.0;
		}
		else if (distance < 0.0)
		{
			return 0.0;
		}
		else return distance;
	}

	public boolean isMoving()
	{
		return isMoving;
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
		targetRange = Constants.Range.topSwitchRange.range;
	}
	
	public void autoSetFloorTargetRange()
	{
		targetRange = Constants.Range.floorRange.range;
	}


	
	public static class Constants
	{
		private enum InitRange
		{
			floorRange(0.101, .1549230769), //>= 0 and < 1
			floorExchangeRange(0.1549230769, 0.2078461538), //>= 1 and < 2
			exchangeRange(0.2078461538, 0.26076923076923),
			exchangePortalRange(0.26076923076923, 0.31369230769231),
			portalRange(0.31369230769231, 0.36661538461539),
			portalBottomSwitchRange(0.36661538461539, 0.41953846153846),
			bottomSwitchRange(0.41953846153846, 0.47246153846154),
			bottomSwitchTopSwitchRange(0.47246153846154, 0.52538461538462),
			topSwitchRange(0.52538461538462, 0.57830769230769),
			topSwitchBottomScaleRange(0.57830769230769, 0.63123076923077),
			bottomScaleRange(0.63123076923077, 0.68415384615385),
			bottomScaleTopScaleRange(0.68415384615385, 0.73707692307692),
			topScaleRange(0.73707692307692, .8),
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
			floorRange(                 InitRange.floorRange.range(),                 InitRange.floorRange,        InitRange.exchangeRange),
			floorExchangeRange(         InitRange.floorExchangeRange.range(),         InitRange.floorRange,        InitRange.exchangeRange),
			exchangeRange(              InitRange.exchangeRange.range(),              InitRange.floorRange,        InitRange.portalRange),
			exchangePortalRange(        InitRange.exchangePortalRange.range(),        InitRange.exchangeRange,     InitRange.portalRange),
			portalRange(                InitRange.portalRange.range(),                InitRange.exchangeRange,     InitRange.bottomSwitchRange),
			portalBottomSwitchRange(    InitRange.portalBottomSwitchRange.range(),    InitRange.portalRange,       InitRange.bottomSwitchRange),
			bottomSwitchRange(          InitRange.bottomSwitchRange.range(),          InitRange.portalRange,       InitRange.topSwitchRange),
			bottomSwitchTopSwitchRange( InitRange.bottomSwitchTopSwitchRange.range(), InitRange.bottomSwitchRange, InitRange.topSwitchRange),
			topSwitchRange(             InitRange.topSwitchRange.range(),             InitRange.bottomSwitchRange, InitRange.bottomScaleRange),
			topSwitchBottomScaleRange(  InitRange.topSwitchBottomScaleRange.range(),  InitRange.topSwitchRange,    InitRange.bottomScaleRange),
			bottomScaleRange(           InitRange.bottomScaleRange.range(),           InitRange.topSwitchRange,    InitRange.topScaleRange), 
			bottomScaleTopScaleRange(   InitRange.bottomScaleTopScaleRange.range(),   InitRange.bottomScaleRange,  InitRange.topScaleRange),
			topScaleRange(              InitRange.topScaleRange.range(),              InitRange.bottomScaleRange,  InitRange.topScaleRange),
			error(                      InitRange.error.range(),                      InitRange.error,             InitRange.error);

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

		//TODO: Change ALL of these to appropriate values

		public static final int ACCEPTABLE_TICK_RANGE = 10;

		public static final int MASTER_MOTOR_PORT = 10;
		public static final int SLAVE_MOTOR_PORT = 11;
		public static final int STRING_POT_PORT = 3;

		public static final double STRING_POT_SCALE = 1.0;

		public static final double SPEED = 0.5;
	}
}

		