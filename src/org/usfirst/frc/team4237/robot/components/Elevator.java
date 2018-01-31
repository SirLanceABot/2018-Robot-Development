package org.usfirst.frc.team4237.robot.components;

import java.util.HashMap;
import org.usfirst.frc.team4237.robot.sensors.LimitSwitch;
import org.usfirst.frc.team4237.robot.control.Xbox;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * Class for the robot's lift system.
 * @author Ben Puzycki, Darryl Wong, Mark Washington
 * @version 1.0
 */
public class Elevator extends Thread
{
	private static Elevator instance = new Elevator();

	public static Elevator getInstance()
	{
		return instance;
	}

	private Xbox xbox = Xbox.getInstance();

	private WPI_TalonSRX masterTalonSRX = new WPI_TalonSRX(Constants.MASTER_MOTOR_PORT); 
	private WPI_TalonSRX slaveTalonSRX = new WPI_TalonSRX(Constants.SLAVE_MOTOR_PORT);

	private AnalogPotentiometer stringPot = new AnalogPotentiometer(0);

	private LimitSwitch topLimitSwitch = new LimitSwitch(0);
	private LimitSwitch bottomLimitSwitch = new LimitSwitch(1);

	private double currentValue;
	private Constants.Range currentRange;
	private int[] targetRange;
	private Constants.Direction currentDirection = Constants.Direction.None;
	private boolean isMoving = false;

	/**
	 * Constructor for Elevator, called only
	 * once by getInstance(). It initializes
	 * the keys and values in levelTicks and
	 * tickLevels.
	 */
	private Elevator()
	{
		masterTalonSRX.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		//	masterTalonSRX.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.Analog, 0, 0);
		masterTalonSRX.setSensorPhase(false);

		masterTalonSRX.configForwardSoftLimitThreshold(5000, 0);
		masterTalonSRX.configReverseSoftLimitThreshold(-5000, 0);
		masterTalonSRX.configForwardSoftLimitEnable(true, 0);
		masterTalonSRX.configReverseSoftLimitEnable(true, 0);

		slaveTalonSRX.configForwardSoftLimitEnable(false, 0);
		slaveTalonSRX.configReverseSoftLimitEnable(false, 0);

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
			if(isMoving())
			{
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
					isMoving = false;
					currentDirection = Constants.Direction.None;
					stopMoving();
				}
				else if (currentDirection == Constants.Direction.Up)
				{
					raise();
				}
				else if (currentDirection == Constants.Direction.Down)
				{
					lower();
				}
			}
		} //End of while loop
	}

	public void updateCurrentRange()
	{
		currentValue = getStringPot();
		if (currentValue <= Constants.Range.floorRange.topValue())
		{
			currentRange = Constants.Range.floorRange;
		}
		else if (currentValue <= Constants.Range.floorExchangeRange.topValue())
		{
			currentRange = Constants.Range.floorExchangeRange;
		}
		else if (currentValue <= Constants.Range.exchangeRange.topValue())
		{
			currentRange = Constants.Range.exchangeRange;
		}
		else if (currentValue <= Constants.Range.exchangePortalRange.topValue())
		{
			currentRange = Constants.Range.exchangePortalRange;
		}
		else if (currentValue <= Constants.Range.portalRange.topValue())
		{
			currentRange = Constants.Range.portalRange;
		}
		else if (currentValue <= Constants.Range.portalBottomSwitchRange.topValue())
		{
			currentRange = Constants.Range.portalBottomSwitchRange;
		}
		else if (currentValue <= Constants.Range.bottomSwitchRange.topValue())
		{
			currentRange = Constants.Range.bottomSwitchRange;
		}
		else if (currentValue <= Constants.Range.bottomSwitchTopSwitchRange.topValue())
		{
			currentRange = Constants.Range.bottomSwitchTopSwitchRange;
		}
		else if (currentValue <= Constants.Range.topSwitchRange.topValue())
		{
			currentRange = Constants.Range.topSwitchRange;
		}
		else if (currentValue <= Constants.Range.topSwitchBottomScaleRange.topValue())
		{
			currentRange = Constants.Range.topSwitchBottomScaleRange;
		}
		else if (currentValue <= Constants.Range.bottomScaleRange.topValue())
		{
			currentRange = Constants.Range.bottomScaleRange;
		}
		else if (currentValue <= Constants.Range.bottomScaleTopScaleRange.topValue())
		{
			currentRange = Constants.Range.bottomScaleTopScaleRange;
		}
		else if (currentValue <= Constants.Range.topScaleRange.topValue())
		{
			currentRange = Constants.Range.topScaleRange;
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

	public double getStringPot()
	{
		double distance;

		distance = stringPot.get() * Constants.STRING_POT_SCALE; // Multiply voltage by constant to get the distance

		return distance;
	}

	public int getEncoder()
	{
		return masterTalonSRX.getSelectedSensorPosition(0);
	}

	public void resetEncoder()
	{
		masterTalonSRX.setSelectedSensorPosition(0, 0, 20);
	}

	public boolean isMoving()
	{
		return isMoving;
	}

	public static class Constants
	{
		private enum InitRange
		{
			floorRange(0, 1), //>= 0 and < 1
			floorExchangeRange(1, 2), //>= 1 and < 2
			exchangeRange(2, 3),
			exchangePortalRange(3, 4),
			portalRange(4, 5),
			portalBottomSwitchRange(5, 6),
			bottomSwitchRange(6, 7),
			bottomSwitchTopSwitchRange(7, 8),
			topSwitchRange(8, 9),
			topSwitchBottomScaleRange(9, 10),
			bottomScaleRange(10, 11),
			bottomScaleTopScaleRange(11, 12),
			topScaleRange(12, 13),
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

			public int bottomValue()
			{
				return this.range[0];
			}

			public int topValue()
			{
				return this.range[1];
			}
		}

		public enum Range
		{
			floorRange(                 InitRange.floorRange.range(),                 InitRange.floorRange,              InitRange.exchangeRange),
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

			public int bottomValue()
			{
				return this.range[0];
			}

			public int topValue()
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

		public static final int MASTER_MOTOR_PORT = 0;
		public static final int SLAVE_MOTOR_PORT = 1;

		public static final double STRING_POT_SCALE = 1;

		public static final double SPEED = 0.5;
	}
}