package org.usfirst.frc.team4237.robot.components;

import org.usfirst.frc.team4237.robot.sensors.AMSColorSensor;
import org.usfirst.frc.team4237.robot.util.Colors;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
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
	
	private Encoder enc = new Encoder(0, 1, false, EncodingType.k4X);
	private AHRS navX = new AHRS(I2C.Port.kMXP);
	
	//color sensor stuff
	private AMSColorSensor mAMSColorSensor = new AMSColorSensor(AMSColorSensor.Constants.PORT, AMSColorSensor.Constants.ADDRESS);
	private Colors crgb = new Colors();
	private Colors crgbPrevious = new Colors();
	private Colors crgbUpperThreshold = new Colors();
	private Colors crgbLowerThreshold = new Colors();
	private int UpperThresholdFactor;
	private int LowerThresholdFactor;
	
	private static Drivetrain instance = new Drivetrain();
	
	/**
	 * Returns the instance of Drivetrain
	 * @return
	 */
	public static Drivetrain getInstance()
	{
		return instance;
	}
	
	public void debugPrintCurrent()
	{
		System.out.println("Talon 0: " + rearRightMasterMotor.getOutputCurrent());
		System.out.println("Talon 1: " + rearRightFollowerMotor.getOutputCurrent());
		System.out.println("Talon 2: " + rearLeftMasterMotor.getOutputCurrent());
		System.out.println("Talon 3: " + rearLeftFollowerMotor.getOutputCurrent());
		
		System.out.println("Talon 12: " + frontLeftFollowerMotor.getOutputCurrent());
		System.out.println("Talon 13: " + frontLeftMasterMotor.getOutputCurrent());
		System.out.println("Talon 14: " + frontRightFollowerMotor.getOutputCurrent());
		System.out.println("Talon 15: " + frontRightMasterMotor.getOutputCurrent() + "\n\n");

		
	}
	

	private Drivetrain()
	{
		super(frontLeftMasterMotor, rearLeftMasterMotor, frontRightMasterMotor, rearRightMasterMotor);
		this.setSafetyEnabled(false);
		
		frontLeftFollowerMotor.follow(frontLeftMasterMotor);
		frontRightFollowerMotor.follow(frontRightMasterMotor);
		rearLeftFollowerMotor.follow(rearLeftMasterMotor);
		rearRightFollowerMotor.follow(rearRightMasterMotor);
		
		frontLeftMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontLeftMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontLeftMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		frontLeftFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontLeftFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontLeftFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		
		rearLeftMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearLeftMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearLeftMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		rearLeftFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearLeftFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearLeftFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		
		frontRightMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontRightMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontRightMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		frontRightFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontRightFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontRightFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		
		rearRightMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearRightMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearRightMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
		rearRightFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearRightFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearRightFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, 10);
		
	}
	
	/**
	 * Drive the distance passed into the method
	 * @return
	 */
	public boolean driveDistance(int inches, double maxSpeed, int heading)
	{
		boolean isDoneDriving = false;
		//			double ticsdPerInch = 76.39437268410976116906420641880689377654;
		//			double x = Math.abs(frontLeftMotor.getSelectedSensorPosition(0)) / ticsdPerInch; //x is position in inches
		double ticsdPerInch = 26.9627197708622686479;
		double x = Math.abs(enc.getRaw()) / ticsdPerInch; //x is position in inches		
		double startingSpeed = 0.3;
		double stoppingSpeed = 0.15;
		int startingDistance = 36;
		int stoppingDistance = 80;
		double rotate = (navX.getYaw() - heading) / 50;

		if (x <= inches)
		{
			if(x <= startingDistance)
			{
				driveCartesian(0, -(((maxSpeed - startingSpeed) / startingDistance) * x + startingSpeed), rotate);
			}
			else if(x >= startingDistance && x <= inches - stoppingDistance)
			{
				driveCartesian(0, -maxSpeed, rotate);
			}
			else
			{
				driveCartesian(0, -stoppingSpeed, rotate);
			}
		}
		else
		{
			driveCartesian(0, 0, 0);
			isDoneDriving = true;
		}
		System.out.println("Distance: " + x);
		return isDoneDriving;
	}

	/**
	 * Rotate to the bearing passed into the method. 0 degrees is North
	 * @return
	 */
	public boolean spinToBearing(int bearing, double speed)
	{
		boolean isDoneSpinning = false;
		double heading = navX.getYaw();
		int threshold = 20;
		if(bearing - heading > 0)
		{
			speed *= -1;
		}

		if(Math.abs(bearing - heading) >= threshold)
		{
			driveCartesian(0, 0, speed);
		}
		else
		{
			driveCartesian(0, 0, 0);
			isDoneSpinning = true;
		}
		System.out.println("heading: " + heading);
		return isDoneSpinning;
	}

	public boolean driveToColor(AMSColorSensor.Constants.Color color, double speed, int heading)
	{
		boolean isDoneDriving = false;
		boolean foundTape = false;
		mAMSColorSensor.get(crgb);

		double rotate = (navX.getYaw() - heading) / 50;

		if(color == AMSColorSensor.Constants.Color.kRed)
		{
			foundTape = crgb.R > crgbUpperThreshold.R;
		}
		else if(color == AMSColorSensor.Constants.Color.kBlue)
		{
			foundTape = crgb.B > crgbUpperThreshold.B;
		}
		else if(color == AMSColorSensor.Constants.Color.kWhite)
		{
			foundTape = crgb.C > crgbUpperThreshold.C;
		}
		if(!foundTape)
		{

			driveCartesian(0, -speed, rotate);
		}
		else
		{
			driveCartesian(0, 0, 0);
			isDoneDriving = true;
		}
		return isDoneDriving;
	}

	public double getNavXYaw()
	{
		return navX.getYaw();
	}
	
	public void resetNavX()
	{
		navX.reset();
	}

	public void resetEncoder()
	{
		enc.reset();
	}

	public void calibrateNavX()
	{
		System.out.println("Calibrating NavX...");
		while(navX.isCalibrating())
		{

		}
		System.out.println("Calibration done.");
	}

	public void calibrateColorSensor()
	{
		try {
			Thread.sleep(250); // wait for thread to start
			mAMSColorSensor.get(crgb); // get initial data to assure there is something for any other robot methods the first time through since robotPeriodic runs after others

			// establish floor color and lighting conditions
			// loop to get average where the robot is parked at the start
			crgbUpperThreshold.C = 0;
			crgbUpperThreshold.R = 0;
			crgbUpperThreshold.G = 0;
			crgbUpperThreshold.B = 0;

			crgbLowerThreshold.C = 0;
			crgbLowerThreshold.R = 0;
			crgbLowerThreshold.G = 0;
			crgbLowerThreshold.B = 0;
			int numSamples = 60;
			for (int i = 1; i <= numSamples; i++)
			{
				mAMSColorSensor.get(crgb);
				crgbUpperThreshold.C += crgb.C;
				crgbUpperThreshold.R += crgb.R;
				crgbUpperThreshold.G += crgb.G;
				crgbUpperThreshold.B += crgb.B;

				crgbLowerThreshold.C += crgb.C;
				crgbLowerThreshold.R += crgb.R;
				crgbLowerThreshold.G += crgb.G;
				crgbLowerThreshold.B += crgb.B;
				Thread.sleep(18);
			}

			// gap between upper and lower thresholds to prevent jitter between the two states
			UpperThresholdFactor = 3; //  must be exceeded to determine tape found
			LowerThresholdFactor = 2; // must be below to reset tape found

			crgbUpperThreshold.C = crgbUpperThreshold.C*UpperThresholdFactor/numSamples; // compute the average and include the tape threshold factor
			crgbUpperThreshold.R = crgbUpperThreshold.R*UpperThresholdFactor/numSamples;
			crgbUpperThreshold.G = crgbUpperThreshold.G*UpperThresholdFactor/numSamples;
			crgbUpperThreshold.B = crgbUpperThreshold.B*UpperThresholdFactor/numSamples;

			crgbLowerThreshold.C = crgbLowerThreshold.C*LowerThresholdFactor/numSamples; // compute the average and include the tape threshold factor
			crgbLowerThreshold.R = crgbLowerThreshold.R*LowerThresholdFactor/numSamples;
			crgbLowerThreshold.G = crgbLowerThreshold.G*LowerThresholdFactor/numSamples;
			crgbLowerThreshold.B = crgbLowerThreshold.B*LowerThresholdFactor/numSamples;

			crgbUpperThreshold.C = 1500;
			crgbUpperThreshold.R = 250;
			crgbUpperThreshold.G = 0;
			crgbUpperThreshold.B = 300;

			crgbLowerThreshold.C = 0;
			crgbLowerThreshold.R = 0;
			crgbLowerThreshold.G = 0;
			crgbLowerThreshold.B = 0;

			//System.out.println("robotInit " + mAMSColorSensor);
			//System.out.println("crgbUpperThreshold " + crgbUpperThreshold + "; crgbLowerThreshold " + crgbLowerThreshold);
		}
		catch (InterruptedException e)
		{
			//e.printStackTrace();
		}
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
		
		public static final int DRIVE_40_AMP_TRIGGER = 60;
		public static final int DRIVE_40_AMP_LIMIT = 30;
		public static final int DRIVE_40_AMP_TIME = 4000;
		
		public static final int DRIVE_30_AMP_TRIGGER = 45;
		public static final int DRIVE_30_AMP_LIMIT = 25;
		public static final int DRIVE_30_AMP_TIME = 3000;
		
		public static final double DRIVE_RAMP_TIME = 0.125;
	}
}
