package org.usfirst.frc.team4237.robot.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class Drivetrain extends MecanumDrive
{
	
	private static WPI_TalonSRX frontLeftMasterMotor = new WPI_TalonSRX(Constants.FRONT_LEFT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX frontLeftFollowerMotor = new WPI_TalonSRX(Constants.FRONT_LEFT_FOLLOWER_MOTOR_PORT);
	private static WPI_TalonSRX frontRightMasterMotor = new WPI_TalonSRX(Constants.FRONT_RIGHT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX frontRightFollowerMotor = new WPI_TalonSRX(Constants.FRONT_RIGHT_FOLLOWER_MOTOR_PORT);
	private static WPI_TalonSRX rearLeftMasterMotor = new WPI_TalonSRX(Constants.REAR_LEFT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX rearLeftFollowerMotor = new WPI_TalonSRX(Constants.REAR_LEFT_FOLOWER_MOTOR_PORT);
	private static WPI_TalonSRX rearRightMasterMotor = new WPI_TalonSRX(Constants.REAR_RIGHT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX rearRightFollowerMotor = new WPI_TalonSRX(Constants.REAR_RIGHT_FOLLOWER_MOTOR_PORT);
	
	
	
	private static Drivetrain instance = new Drivetrain();
	
	/**
	 * Returns the instance of Drivetrain
	 * @return
	 */
	public static Drivetrain getInstance()
	{
		return instance;
	}
	

	private Drivetrain()
	{
		super(frontLeftMasterMotor, rearLeftMasterMotor, frontRightMasterMotor, rearRightMasterMotor);
		this.setSafetyEnabled(false);
		
		frontLeftFollowerMotor.follow(frontLeftMasterMotor);
		frontRightFollowerMotor.follow(frontRightMasterMotor);
		rearLeftFollowerMotor.follow(rearLeftMasterMotor);
		rearRightFollowerMotor.follow(rearRightMasterMotor);
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
		public static final int FRONT_LEFT_MASTER_MOTOR_PORT = 13;
		public static final int FRONT_LEFT_FOLLOWER_MOTOR_PORT = 12;
		public static final int FRONT_RIGHT_MASTER_MOTOR_PORT = 15;
		public static final int FRONT_RIGHT_FOLLOWER_MOTOR_PORT = 14;
		public static final int REAR_LEFT_MASTER_MOTOR_PORT = 2;
		public static final int REAR_LEFT_FOLOWER_MOTOR_PORT = 3;
		public static final int REAR_RIGHT_MASTER_MOTOR_PORT = 0;
		public static final int REAR_RIGHT_FOLLOWER_MOTOR_PORT = 1;
	}
}
