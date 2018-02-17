package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.sensors.AMSColorSensor;
import org.usfirst.frc.team4237.robot.network.AutoSelect4237;
//import org.usfirst.frc.team4237.robot.components.Elevator;
//import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class Autonomous
{
	private Drivetrain drivetrain = Drivetrain.getInstance();
	//private Elevator elevator = Elevator.getInstance();
	//private Gripper gripper = Gripper.getInstance();
	private RaspberryPiReceiver raspberryPiReceiver = RaspberryPiReceiver.getInstance();

	/*
	 * This class will handle switching between
	 * raspberry pi and roborio for vision
	 */
	
	private DriverStation driverStation = DriverStation.getInstance();
	private AutoSelect4237 autoSelect4237 = AutoSelect4237.getInstance();
	private String fieldColors;
	private DriverStation.Alliance allianceColor;
	private int angleSign;
	private AMSColorSensor.Constants.Color color;
	private Constants.AutoMode autoMode = Constants.AutoMode.kAutoLine;
	private Constants.AutoStage autoStage = Constants.AutoStage.kDrive1;
	
	private Timer t = new Timer();

	private static Autonomous instance = new Autonomous();
	public static Autonomous getInstance()
	{
		return instance;
	}
	
	private Autonomous()
	{
		raspberryPiReceiver.start();
	}
	
	/**
	 * Autonomous init method
	 */
	public void init()
	{
		t.stop();
		t.reset();
		t.start();
		
		//grab values from fms and autoselect
		fieldColors = driverStation.getGameSpecificMessage();
		allianceColor = driverStation.getAlliance();
		if(allianceColor == DriverStation.Alliance.Blue)
		{
			color = AMSColorSensor.Constants.Color.kBlue;
		}
		else if(allianceColor == DriverStation.Alliance.Red)
		{
			color = AMSColorSensor.Constants.Color.kRed;
		}

		if(autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("right"))
		{
			angleSign = 1;
		}
		else
		{
			angleSign = -1;
		}

		if(((fieldColors.charAt(1) == 'L' && autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("right"))
				|| (fieldColors.charAt(1) == 'R' && autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("left")))
				&& autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("scale"))
		{
			autoMode = Constants.AutoMode.kScaleOnOppositeSide;
		}
		else if(((fieldColors.charAt(1) == 'R' && autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("right"))
				|| (fieldColors.charAt(1) == 'L' && autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("left")))
				&& autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("scale"))
		{
			autoMode = Constants.AutoMode.kScaleOnSameSide;
		}
		else if(((fieldColors.charAt(0) == 'L' && autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("left"))
				|| (fieldColors.charAt(0) == 'R' && autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("right")))
				&& autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("switch"))
		{
			autoMode = Constants.AutoMode.kSwitchOnSameSide;
		}
		else if(fieldColors.charAt(0) == 'L'
				&& autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("center")
				&& autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("switch"))
		{
			autoMode = Constants.AutoMode.kSwitchLeftFromMiddle;
		}
		else if(fieldColors.charAt(0) == 'R'
				&& autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("center")
				&& autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("switch"))
		{
			autoMode = Constants.AutoMode.kSwitchRightFromMiddle;
		}
		else if(autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("auto line"))
		{
			autoMode = Constants.AutoMode.kAutoLine;
		}
		
		//backup auto routine
		if(((fieldColors.charAt(0) == 'L'
				&& autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("right"))
			|| (fieldColors.charAt(0) == 'R'
				&& autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("left")))
			&& autoSelect4237.getData().getSelectedTarget().equalsIgnoreCase("switch"))
		{
			if(autoSelect4237.getData().getSelectedBackupPlan().equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(autoSelect4237.getData().getSelectedBackupPlan().equalsIgnoreCase("scale"))
			{
				if(fieldColors.charAt(1) == 'R'
					&& autoSelect4237.getData().getSelectedPosition().equalsIgnoreCase("right"))
				{
					autoMode = Constants.AutoMode.kScaleOnSameSide;
				}
				else
				{
					autoMode = Constants.AutoMode.kScaleOnOppositeSide;
				}
			}
		}
		drivetrain.resetEncoder();
		drivetrain.resetNavX();
		autoStage = Constants.AutoStage.kDrive1;
	}
	
	/**
	 * Autonomous loop
	 */
	public void periodic()
	{
		if(autoMode == Constants.AutoMode.kScaleOnOppositeSide)
		{
			scaleOnOppositeSide();
		}
		else if(autoMode == Constants.AutoMode.kScaleOnSameSide)
		{
			scaleOnSameSide();
		}
		else if(autoMode == Constants.AutoMode.kSwitchOnSameSide)
		{
			switchOnSameSide();
		}
		else if(autoMode == Constants.AutoMode.kSwitchLeftFromMiddle)
		{
			switchLeftFromMiddle();
		}
		else if(autoMode == Constants.AutoMode.kSwitchRightFromMiddle)
		{
			switchRightFromMiddle();
		}
		else if(autoMode == Constants.AutoMode.kAutoLine)
		{
			autoLine();
		}
	}
	
	public void autoLine()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!drivetrain.driveDistance(90, 0.5, 0))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				drivetrain.resetEncoder();
			}
		}
	}
	
	public void switchLeftFromMiddle()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!drivetrain.driveDistance(5, 0.3, 0))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!drivetrain.driveDistance(95, 0.75, -45))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!drivetrain.driveDistance(15, 0.25, 0))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Time: " + t.get());
			}
		}
	}
	
	public void switchRightFromMiddle()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!drivetrain.driveDistance(5, 0.3, 0))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!drivetrain.driveDistance(90, 0.75, 35))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!drivetrain.driveDistance(9, 0.25, 0))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Time: " + t.get());
			}
		}
	}
	
	public void switchOnSameSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!drivetrain.driveDistance(5, 0.3, 0 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!drivetrain.driveDistance(95, 0.75, -45 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!drivetrain.driveDistance(15, 0.25, 0 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Time: " + t.get());
			}
		}
	}

	public void scaleOnSameSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!drivetrain.driveDistance(184, 0.75, 0))	//white line is at 288 inches
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(!drivetrain.spinToBearing(-55 * angleSign, 0.5))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!drivetrain.driveToColor(color, 0.3, -55 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				drivetrain.resetEncoder();
				Timer.delay(0.5);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(!drivetrain.driveToColor(color, -0.175, -55 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kSpin2;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!drivetrain.spinToBearing(0 * angleSign, 0.5))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(!drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Time: " + t.get());
			}
		}
	}

	public void scaleOnOppositeSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!drivetrain.driveDistance(220, 0.75, 0 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(!drivetrain.spinToBearing(-90 * angleSign, 0.5))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!drivetrain.driveToColor(color, 0.3, -90 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!drivetrain.driveDistance(55, 0.4, -90 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!drivetrain.driveDistance(40, 0.25, -90 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(!drivetrain.driveToColor(color, 0.3, -90 * angleSign))
			{

			}
			else
			{

				autoStage = Constants.AutoStage.kDrive2Distance3;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance3)
		{
			if(!drivetrain.driveDistance(17, 0.25, -90 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kSpin2;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!drivetrain.spinToBearing(0 * angleSign, 0.5))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(!drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Time: " + t.get());
			}
		}
	}
	
	public static class Constants
	{
		enum AutoMode {kScaleOnOppositeSide, kScaleOnSameSide, kSwitchLeftFromMiddle, kSwitchRightFromMiddle, kSwitchOnSameSide, kAutoLine, kNone}
		enum AutoStage {kDone, kDrive1, kSpin1, kDrive2ToLine1, kDrive2Distance1, kDrive2Distance2, kDrive2ToLine2, kDrive2Distance3, kSpin2, kDrive3ToLine}
	}
}
