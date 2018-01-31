package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class Drivetrain extends MecanumDrive
{
	
	private static WPI_TalonSRX frontLeftMotor = new WPI_TalonSRX(Constants.FRONT_LEFT_MOTOR_PORT);
	private static WPI_TalonSRX frontRightMotor = new WPI_TalonSRX(Constants.FRONT_RIGHT_MOTOR_PORT);
	private static WPI_TalonSRX rearLeftMotor = new WPI_TalonSRX(Constants.REAR_LEFT_MOTOR_PORT);
	private static WPI_TalonSRX rearRightMotor = new WPI_TalonSRX(Constants.FRONT_RIGHT_MOTOR_PORT);
	
	private static Drivetrain instance = new Drivetrain();
	
	/**
	 * Returns the instance of DriveTrain
	 * @return
	 */
	public static Drivetrain getInstance()
	{
		return instance;
	}
	

	private Drivetrain()
	{
		super(frontLeftMotor, frontRightMotor, rearLeftMotor, rearRightMotor);
	}
	
	/**
	 * Drive the distance passed into the method
	 * @return
	 */
	public boolean driveDistance()
	{
		//FIXME: Add method body
		return true;
	}
	
	/**
	 * Rotate by the amount passed into the method
	 * @return
	 */
	public boolean setRelativeAngle()
	{
		//FIXME: Add method body
		return true;
	}
	
	/**
	 * Rotate to the absolute angle passed into the method
	 * @return
	 */
	public boolean setAbsoluteAngle()
	{
		//FIXME: Add method body
		return true;
	}
	
	/**
	 * Class for constant variables related to the drivetrain
	 * @author Mark
	 *
	 */
	public static class Constants
	{
		public static final int FRONT_LEFT_MOTOR_PORT = 0;
		public static final int FRONT_RIGHT_MOTOR_PORT = 1;
		public static final int REAR_LEFT_MOTOR_PORT = 2;
		public static final int REAR_RIGHT_MOTOR_PORT = 3;
	}
}
