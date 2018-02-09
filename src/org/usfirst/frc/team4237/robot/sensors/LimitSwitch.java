package org.usfirst.frc.team4237.robot.sensors;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Class for limit switches
 * @author Mark Washington
 *
 */
public class LimitSwitch extends DigitalInput
{
	/**
	 * Limit switch class constructor
	 * @param port Port the limit switch is plugged into
	 */
	public LimitSwitch(int port)
	{
		super(port);
	}
}
