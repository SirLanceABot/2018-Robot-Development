package org.usfirst.frc.team4237.robot;

import edu.wpi.first.wpilibj.Joystick;
import java.util.HashMap;

/**
 * 
 * @author Mark Washington
 * @version 1.0
 *
 */
public class Xbox extends Joystick
{
	private static Xbox instance = new Xbox(Constants.PORT);

	/**
	 * Get instance of Xbox class
	 * @return
	 */
	public static Xbox getInstance()
	{
		return instance;
	}

	/**
	 * Xbox constructor
	 * @param port
	 */
	private Xbox(int port)
	{
		super(port);
	}

	/**
	 * Constants class for Xbox
	 * @author Mark
	 *
	 */
	public static class Constants
	{
		public static final int PORT = 0;
		
		public static final int A_BUTTON = 0;
		public static final int B_BUTTON = 1;
		public static final int X_BUTTON = 2;
		public static final int Y_BUTTON = 3;
		public static final int RIGHT_BUMPER = 4;
		public static final int LEFT_BUMPER = 5;
		public static final int LEFT_STICK_BUTTON = 8;
		public static final int RIGHT_STICK_BUTTON = 9;
	}
}
