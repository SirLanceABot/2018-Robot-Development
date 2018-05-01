package org.usfirst.frc.team4237.robot.components;

import edu.wpi.first.wpilibj.DigitalOutput;

public class LightRing extends DigitalOutput
{
	/**
	 * Light ring class constructor
	 * @param port Port the light ring is plugged into
	 */
	public LightRing(int port)
	{
		super(port);
	}
}
