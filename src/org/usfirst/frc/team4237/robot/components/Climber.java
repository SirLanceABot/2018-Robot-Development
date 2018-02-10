package org.usfirst.frc.team4237.robot.components;

import org.usfirst.frc.team4237.robot.components.Drivetrain.Constants;
import org.usfirst.frc.team4237.robot.control.OperatorXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * 
 * @author Erin Lafrenz
 *
 */

public class Climber 
{
	private Xbox xbox = OperatorXbox.getInstance();
	private WPI_TalonSRX climberTalonSRX = new WPI_TalonSRX(Constants.PORT); 
	
	private static Climber instance = new Climber();
	
	
	
	public static Climber getInstance()
	{
		return instance;
	}
	
	/**
	 * Constructor for Climber
	 */
	private Climber()
	{
		climberTalonSRX.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 20);
		climberTalonSRX.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 20);
		
		climberTalonSRX.configForwardSoftLimitEnable(false, 20);
		climberTalonSRX.configReverseSoftLimitEnable(false, 20);
	}
	
	/**
	 * Raise the climber
	 */
	public void raise()
	{
		climberTalonSRX.set(0.5);
	}
	
	/**
	 * Lower the climber
	 */
	public void lower()
	{
		climberTalonSRX.set(-0.5);
	}
	
	public static class Constants
	{
		public static final int PORT = 0;
		
	}
}
