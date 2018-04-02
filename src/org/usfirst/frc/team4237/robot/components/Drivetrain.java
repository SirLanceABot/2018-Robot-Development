package org.usfirst.frc.team4237.robot.components;

import org.usfirst.frc.team4237.robot.Autonomous;
import org.usfirst.frc.team4237.robot.control.DriverXbox;
import org.usfirst.frc.team4237.robot.control.Xbox;
import org.usfirst.frc.team4237.robot.control.Xbox.Constants;
import org.usfirst.frc.team4237.robot.sensors.AMSColorSensor;
import org.usfirst.frc.team4237.robot.util.Colors;
import org.usfirst.frc.team4237.robot.util.LightRing;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.PWM.PeriodMultiplier;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class Drivetrain extends MecanumDrive implements Component
{
	private DriverXbox xbox = DriverXbox.getInstance();

	private boolean aButton = false;
	private boolean bButton = false;
	private boolean xButton = false;
	private boolean yButton = false;

	private double rightXAxis = 0.0;
	private double rightYAxis = 0.0;
	private double leftXAxis = 0.0;
	private double leftYAxis = 0.0;

	private double servoPosition = 0.5;

	private static WPI_TalonSRX frontLeftMasterMotor = new WPI_TalonSRX(Constants.FRONT_LEFT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX frontLeftFollowerMotor = new WPI_TalonSRX(Constants.FRONT_LEFT_FOLLOWER_MOTOR_PORT);

	private static WPI_TalonSRX frontRightMasterMotor = new WPI_TalonSRX(Constants.FRONT_RIGHT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX frontRightFollowerMotor = new WPI_TalonSRX(Constants.FRONT_RIGHT_FOLLOWER_MOTOR_PORT);

	private static WPI_TalonSRX rearLeftMasterMotor = new WPI_TalonSRX(Constants.REAR_LEFT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX rearLeftFollowerMotor = new WPI_TalonSRX(Constants.REAR_LEFT_FOLOWER_MOTOR_PORT);

	private static WPI_TalonSRX rearRightMasterMotor = new WPI_TalonSRX(Constants.REAR_RIGHT_MASTER_MOTOR_PORT);
	private static WPI_TalonSRX rearRightFollowerMotor = new WPI_TalonSRX(Constants.REAR_RIGHT_FOLLOWER_MOTOR_PORT);

	private static Servo servo = new Servo(Constants.SERVO_PORT);


	private Encoder enc = new Encoder(0, 1, false, EncodingType.k4X);
	private AHRS navX = new AHRS(I2C.Port.kMXP);

	private Timer startUpTimer = new Timer();
	private Timer t = new Timer();
	private boolean resetTimer = true;
	private boolean isTimerDone = false;

	//color sensor stuff
	private AMSColorSensor colorSensor = new AMSColorSensor(AMSColorSensor.Constants.PORT, AMSColorSensor.Constants.ADDRESS);
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

		startUpTimer.stop();
		startUpTimer.reset();

		frontLeftFollowerMotor.follow(frontLeftMasterMotor);
		frontRightFollowerMotor.follow(frontRightMasterMotor);
		rearLeftFollowerMotor.follow(rearLeftMasterMotor);
		rearRightFollowerMotor.follow(rearRightMasterMotor);

		frontLeftMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontLeftMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontLeftMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);

		frontLeftFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontLeftFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontLeftFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);


		rearLeftMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearLeftMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearLeftMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);

		rearLeftFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearLeftFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearLeftFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);


		frontRightMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontRightMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontRightMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);

		frontRightFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		frontRightFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		frontRightFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);


		rearRightMasterMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearRightMasterMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearRightMasterMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);

		rearRightFollowerMotor.configContinuousCurrentLimit(Constants.DRIVE_40_AMP_LIMIT, 10);
		rearRightFollowerMotor.configPeakCurrentLimit(Constants.DRIVE_40_AMP_TRIGGER, Constants.DRIVE_40_AMP_TIME);
		rearRightFollowerMotor.configOpenloopRamp(Constants.DRIVE_RAMP_TIME, Constants.DRIVE_RAMP_RATE_TIMEOUT);

		frontRightMasterMotor.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, 0);
		frontRightMasterMotor.setSensorPhase(true);
		
		servo.setPeriodMultiplier(PeriodMultiplier.k1X);
		servo.setBounds(2.4, 0, 0, 0, 0.6);		// Current bounds are 8.5 turns, (2.5, 0, 0, 0, 0.5) for 9 turns

		//this.calibrateNavX();

		this.calibrateColorSensor();
	}

	public double getEncInches()
	{
		return frontRightMasterMotor.getSelectedSensorPosition(0) / 135.0;
	}

	public void teleop()
	{
		try
		{
			//System.out.println("Encoder tic: " + frontRightMasterMotor.getSelectedSensorPosition(0) + "  Distance: " + getEncInches());

			System.out.println(DriverStation.getInstance().isOperatorControl() + " " + DriverStation.getInstance().isEnabled());
			
			if (DriverStation.getInstance().isOperatorControl() && DriverStation.getInstance().isEnabled())
			{
				if (Math.abs(xbox.getRawAxis(1)) > 0.2)
				{
					leftYAxis = -xbox.getRawAxis(Xbox.Constants.LEFT_STICK_Y_AXIS);
				}
				else leftYAxis = 0;

				if (Math.abs(xbox.getRawAxis(0)) > 0.2)
				{
					leftXAxis = xbox.getRawAxis(Xbox.Constants.LEFT_STICK_X_AXIS);
				}
				else leftXAxis = 0;

				if (Math.abs(xbox.getRawAxis(4)) > 0.2)
				{
					rightXAxis = xbox.getRawAxis(Xbox.Constants.RIGHT_STICK_X_AXIS);
				}
				else rightXAxis = 0;


				if(xbox.getRawButton(Xbox.Constants.RIGHT_BUMPER))
				{
					this.driveCartesian(leftXAxis, leftYAxis, (this.getNavXYaw()) / 50);
				}
				else
				{
					this.driveCartesian(leftXAxis, leftYAxis, rightXAxis);
				}

			}
			//drivetrain.debugPrintCurrent();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Drive the distance passed into the method
	 * @return
	 */
	public boolean driveDistance(int inches, double maxSpeed, int heading, int stoppingDistance)
	{
		boolean isDoneDriving = false;
		double x = Math.abs(getEncInches());
		double startingSpeed = 0.3;
		double stoppingSpeed = 0.175;
		int startingDistance = 12;
		int direction = 1;
		double rotate = (navX.getYaw() - heading) / 50;

		if(maxSpeed < 0)
		{
			direction = -1;
		}
		
		if (x <= inches)
		{
			if(x <= startingDistance)
			{
				driveCartesian(0, ((maxSpeed - (startingSpeed * direction)) / startingDistance) * x + (startingSpeed * direction), -rotate);
			}
			else if(x >= startingDistance && x <= inches - stoppingDistance)
			{
				driveCartesian(0, maxSpeed, -rotate);
			}
			else
			{
				driveCartesian(0, stoppingSpeed * direction, -rotate);
			}
		}
		else
		{
			driveCartesian(0, 0, 0);
			isDoneDriving = true;
		}
		//System.out.println("Distance: " + x);
		return isDoneDriving;
	}

	/**
	 * Strafe at a specific angle. 0 degrees is North
	 * @return
	 */
	public boolean strafeDistanceAtAngle(int inches, double angle, double speed, int heading)
	{
		boolean isDoneDriving = false;
		double x = Math.abs(getEncInches());
		double rotate = (navX.getYaw() - heading) / 50;
		double strafeSpeed = Math.sin(angle) * speed;
		double forwardSpeed = Math.cos(angle) * speed;

		if(angle < 0)
		{
			strafeSpeed *= -1;
		}

		if(x < inches)
		{
			driveCartesian(strafeSpeed, forwardSpeed, rotate);
		}
		else
		{
			driveCartesian(0, 0, 0);
			isDoneDriving = true;
		}

		return isDoneDriving;
	}

	public boolean driveSeconds(double speed, double time, int heading)
	{
		isTimerDone = false;
		double rotate = (navX.getYaw() - heading) / 50;
		
		if(resetTimer)
		{
			t.stop();
			t.reset();
			t.start();
			resetTimer = false;
		}
		
		if(t.get() <= time)
		{
			driveCartesian(0, speed, -rotate);
		}
		else
		{
			driveCartesian(0, 0, 0);
			isTimerDone = true;
			resetTimer = true;
		}
		return isTimerDone;
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
			driveCartesian(0, 0, -speed);
		}
		else
		{
			driveCartesian(0, 0, 0);
			isDoneSpinning = true;
		}
		//System.out.println("heading: " + heading);
		return isDoneSpinning;
	}

	public boolean driveToColor(AMSColorSensor.Constants.Color color, double speed, int heading)
	{
		boolean isDoneDriving = false;
		boolean foundTape = false;
		colorSensor.get(crgb);

		double rotate = (navX.getYaw() - heading) / 50;

		if(color == AMSColorSensor.Constants.Color.kRed)
		{
			foundTape = crgb.R > crgbUpperThreshold.R;
			System.out.println("RED COLOR FOUND: " + foundTape);
		}
		else if(color == AMSColorSensor.Constants.Color.kBlue)
		{
			foundTape = crgb.B > crgbUpperThreshold.B;
		System.out.println("BLUE COLOR FOUND: " + foundTape);
		}
		else if(color == AMSColorSensor.Constants.Color.kWhite)
		{
			foundTape = crgb.C > crgbUpperThreshold.C;
			System.out.println("WHITE COLOR FOUND: " + foundTape);
		}
		if(!foundTape)
		{

			driveCartesian(0, speed, -rotate);
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
		frontRightMasterMotor.setSelectedSensorPosition(0, 0, 0);
		Timer.delay(0.06);
	}

	public void printColors()
	{
		System.out.print(colorSensor);
	}

	public void printHeading()
	{
		System.out.print("Heading: " + navX.getYaw());
	}

	public void printEncoder()
	{
		System.out.println("Encoder: " + enc.getRaw());
	}

	public double getServo()
	{
		return servo.get();
	}

	public void calibrateNavX()
	{	
		System.out.println("Calibrating NavX...");

		startUpTimer.start();
		while(navX.isCalibrating() && startUpTimer.get() < 5.0)
		{
			Timer.delay(0.005);
		}
		if (startUpTimer.get() >= 5.0)
		{
			System.out.println("ERROR! [Drivetrain] Error while calibrating NavX!");
		}
		System.out.println("Calibration done.");
	}

	public void calibrateColorSensor()
	{

		Timer.delay(0.25); // wait for thread to start
		colorSensor.get(crgb); // get initial data to assure there is something for any other robot methods the first time through since robotPeriodic runs after others

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
			colorSensor.get(crgb);
			crgbUpperThreshold.C += crgb.C;
			crgbUpperThreshold.R += crgb.R;
			crgbUpperThreshold.G += crgb.G;
			crgbUpperThreshold.B += crgb.B;

			crgbLowerThreshold.C += crgb.C;
			crgbLowerThreshold.R += crgb.R;
			crgbLowerThreshold.G += crgb.G;
			crgbLowerThreshold.B += crgb.B;
			Timer.delay(0.018);
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

		crgbUpperThreshold.C = 4500;
		crgbUpperThreshold.R = 1000;
		crgbUpperThreshold.G = 0;
		crgbUpperThreshold.B = 800;

		crgbLowerThreshold.C = 0;
		crgbLowerThreshold.R = 0;
		crgbLowerThreshold.G = 0;
		crgbLowerThreshold.B = 0;

		//System.out.println("robotInit " + mAMSColorSensor);
		//System.out.println("crgbUpperThreshold " + crgbUpperThreshold + "; crgbLowerThreshold " + crgbLowerThreshold);
	}


	public void omniWheelUp()
	{
		servoPosition = 0.5 + (1.0 / 8.5) * 0.5;
		servo.set(servoPosition);
	}
	
	public void omniWheelDown()
	{
		servoPosition = 0.5;
		servo.set(servoPosition);
	}
	
	public void rotateServoClockwise(int angle)
	{
		double rotation = (double)angle / 360.0;
		servoPosition += ((1.0/8.5) * rotation);
		servo.set(servoPosition);
	}

	public void rotateServoCounterClockwise(int angle)
	{
		double rotation = (double)angle / 360.0;
		servoPosition -= ((1.0/8.5) * rotation);
		servo.set(servoPosition);
	}

	@Override
	public void printTestInfo()
	{
		System.out.println("[Drivetrain] Encoder position: " + getEncInches() + " NavX: " + navX.getYaw() + " colors: " + colorSensor.toString());
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

		public static final int DRIVE_RAMP_RATE_TIMEOUT = 10; //ms

		public static final double DRIVE_RAMP_TIME = 0.25;	//FIXME: normally 0.5

		public static final int SERVO_PORT = 0;
	}




}