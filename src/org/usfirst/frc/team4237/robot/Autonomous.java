package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.util.LightRing;
import org.usfirst.frc.team4237.robot.sensors.AMSColorSensor;
import org.usfirst.frc.team4237.robot.network.AutoSelect4237;
import org.usfirst.frc.team4237.robot.components.Elevator;
import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class Autonomous
{
	private Drivetrain drivetrain = Drivetrain.getInstance();
	//private Elevator elevator = Elevator.getInstance();
	//private Gripper gripper = Gripper.getInstance();
	private RaspberryPiReceiver raspberryPiReceiver = RaspberryPiReceiver.getInstance();

	private DriverStation driverStation = DriverStation.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Gripper gripper = Gripper.getInstance();
	private AutoSelect4237 autoSelect4237 = AutoSelect4237.getInstance();
	private String fieldColors;
	private DriverStation.Alliance allianceColor;
	private int angleSign;
	private AMSColorSensor.Constants.Color color;
	private Constants.AutoMode autoMode = Constants.AutoMode.kAutoLine;
	private Constants.AutoStage autoStage = Constants.AutoStage.kDrive1;
//	private LightRing greenCameraLight = new LightRing(Constants.GREEN_CAMERA_PORT);
//	private LightRing whiteCameraLight = new LightRing(Constants.WHITE_CAMERA_PORT);
//	private LightRing whiteFloorLight = new LightRing(Constants.WHITE_FLOOR_PORT);

	private String selectedTarget = null;
	private String selectedPosition = null;
	private String selectedBackupPlan = null;
	
	private Timer t = new Timer();

	private static Autonomous instance = new Autonomous();
	public static Autonomous getInstance()
	{
		return instance;
	}

	private Autonomous()
	{
		raspberryPiReceiver.start();
		autoSelect4237.start();
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
		
		System.out.println("GameSpecificMessage: " + fieldColors);
		System.out.println("Alliance color: " + allianceColor);
		
		
		selectedPosition = autoSelect4237.getData().getSelectedPosition();
		selectedTarget = autoSelect4237.getData().getSelectedTarget();
		selectedBackupPlan = autoSelect4237.getData().getSelectedBackupPlan();
		
		System.out.println("Selected position: " + selectedPosition);
		System.out.println("Selected target: " + selectedTarget);
		System.out.println("Selected backup plan: " + selectedBackupPlan);
		
		if(allianceColor == DriverStation.Alliance.Blue)
		{
			color = AMSColorSensor.Constants.Color.kBlue;
		}
		else if(allianceColor == DriverStation.Alliance.Red)
		{
			color = AMSColorSensor.Constants.Color.kRed;
		}
		
		
		angleSign = 1;
		if(selectedPosition.equalsIgnoreCase("left"))
		{
			angleSign = -1;
		}
		

		if(((fieldColors.charAt(1) == 'L' && selectedPosition.equalsIgnoreCase("right"))
				|| (fieldColors.charAt(1) == 'R' && selectedPosition.equalsIgnoreCase("left")))
				&& selectedTarget.equalsIgnoreCase("scale"))
		{
			autoMode = Constants.AutoMode.kScaleOnOppositeSide;
		}
		else if(((fieldColors.charAt(1) == 'R' && selectedPosition.equalsIgnoreCase("right"))
				|| (fieldColors.charAt(1) == 'L' && selectedPosition.equalsIgnoreCase("left")))
				&& selectedTarget.equalsIgnoreCase("scale"))
		{
			autoMode = Constants.AutoMode.kScaleOnSameSide;
		}
		else if(((fieldColors.charAt(0) == 'L' && selectedPosition.equalsIgnoreCase("left"))
				|| (fieldColors.charAt(0) == 'R' && selectedPosition.equalsIgnoreCase("right")))
				&& selectedTarget.equalsIgnoreCase("switch"))
		{
			autoMode = Constants.AutoMode.kSwitchOnSameSide;
		}
		else if(fieldColors.charAt(0) == 'L'
				&& selectedPosition.equalsIgnoreCase("center")
				&& selectedTarget.equalsIgnoreCase("switch"))
		{
			autoMode = Constants.AutoMode.kSwitchLeftFromMiddle;
		}
		else if(fieldColors.charAt(0) == 'R'
				&& selectedPosition.equalsIgnoreCase("center")
				&& selectedTarget.equalsIgnoreCase("switch"))
		{
			autoMode = Constants.AutoMode.kSwitchRightFromMiddle;
		}
		else if(selectedTarget.equalsIgnoreCase("auto line"))
		{
			autoMode = Constants.AutoMode.kAutoLine;
		}

		//backup auto routine
		if(((fieldColors.charAt(0) == 'L' && selectedPosition.equalsIgnoreCase("right"))
				|| (fieldColors.charAt(0) == 'R' && selectedPosition.equalsIgnoreCase("left")))
				&& selectedTarget.equalsIgnoreCase("switch"))
		{
			if(selectedBackupPlan.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(selectedBackupPlan.equalsIgnoreCase("scale"))
			{
				if(fieldColors.charAt(1) == 'R' && selectedPosition.equalsIgnoreCase("right")
					|| fieldColors.charAt(1) == 'L' && selectedPosition.equalsIgnoreCase("left"))
				{
					autoMode = Constants.AutoMode.kScaleOnSameSide;
				}
				else
				{
					autoMode = Constants.AutoMode.kScaleOnOppositeSide;
				}
			}
		}
		
		if(selectedPosition.equalsIgnoreCase("none"))
		{
			autoMode = Constants.AutoMode.kNone;
		}
		
		System.out.println("AutoMode: " + autoMode);
		drivetrain.resetEncoder();
		drivetrain.resetNavX();
		gripper.resetPivotEncoder();
//		greenCameraLight.set(true);
//		whiteCameraLight.set(true);
//		whiteFloorLight.set(true);
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
			
//			if(gripper.isAutoMiddlePivotDone())
//			{
//				gripper.autoSetMiddleTargetRange();
//			}
//			
//			if(autoStage == Constants.AutoStage.kSpin2 || autoStage == Constants.AutoStage.kDrive3ToLine)
//			{
//				if(!elevator.inTargetRange() && gripper.isAutoMiddlePivoterDone())
//				{
//					elevator.autoSetScaleTargetRange();
//				}
//			}
//			else if(autoStage == Constants.AutoStage.kDone && elevator.inTargetRange())
//			{
//				gripper.autoEject();
//			}
		}
		else if(autoMode == Constants.AutoMode.kSwitchOnSameSide)
		{
			switchOnSameSide();
			
//			gripper.autoSetMiddleTargetRange();
//			
//			if(autoStage == Constants.AutoStage.kDrive2Distance1)
//			{
//				if(!elevator.inTargetRange() && gripper.isAutoMiddlePivoterDone())
//				{
//					elevator.autoSetSwitchTargetRange();
//				}
//			}
//			else if(autoStage == Constants.AutoStage.kDone && elevator.inTargetRange())
//			{
//				gripper.autoEject();
//			}
		}
		else if(autoMode == Constants.AutoMode.kSwitchLeftFromMiddle)
		{
			switchLeftFromMiddle();
			
//			gripper.autoSetMiddleTargetRange();
//			
//			if(autoStage == Constants.AutoStage.kDrive2Distance1)
//			{
//				if(!elevator.inTargetRange() && gripper.isAutoMiddlePivoterDone())
//				{
//					elevator.autoSetSwitchTargetRange();
//				}
//			}
//			else if(autoStage == Constants.AutoStage.kDone && elevator.inTargetRange())
//			{
//				gripper.autoEject();
//			}
		}
		else if(autoMode == Constants.AutoMode.kSwitchRightFromMiddle)
		{
			switchRightFromMiddle();
			
//			gripper.autoSetMiddleTargetRange();
//			
//			if(autoStage == Constants.AutoStage.kDrive2Distance1)
//			{
//				if(!elevator.inTargetRange() && gripper.isAutoMiddlePivoterDone())
//				{
//					elevator.autoSetSwitchTargetRange();
//				}
//			}
//			else if(autoStage == Constants.AutoStage.kDone && elevator.inTargetRange())
//			{
//				gripper.autoEject();
//			}
		}
		else if(autoMode == Constants.AutoMode.kAutoLine)
		{
			autoLine();
//			
//			gripper.autoSetMiddleTargetRange();
		}
		else if(autoMode == Constants.AutoMode.kNone)
		{
			autoStage = Constants.AutoStage.kDone;
		}
		
		if(autoStage == Constants.AutoStage.kDone)
		{
//			greenCameraLight.set(false);
//			whiteCameraLight.set(false);
//			whiteFloorLight.set(false);
		}
	}

	public void autoLine()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(90, 0.5, 0))
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
			gripper.autoSetHorizontalTargetRange();
			if(drivetrain.driveDistance(5, 0.3, 0) && gripper.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			elevator.autoSetSwitchTargetRange();
			if(drivetrain.driveDistance(95, 0.75, -45) && elevator.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(15, 0.25, 0))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
			}
		}
	}

	public void switchRightFromMiddle()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			gripper.autoSetHorizontalTargetRange();
			if(drivetrain.driveDistance(5, 0.3, 0) && gripper.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			elevator.autoSetSwitchTargetRange();
			if(drivetrain.driveDistance(90, 0.75, 35) && elevator.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(9, 0.25, 0))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
			}
		}
	}

	public void switchOnSameSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			gripper.autoSetHorizontalTargetRange();
			if(drivetrain.driveDistance(5, 0.3, 0 * angleSign) && gripper.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			elevator.autoSetSwitchTargetRange();
			if(drivetrain.driveDistance(95, 0.75, -45 * angleSign) && elevator.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(15, 0.25, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
			}
		}
	}

	public void scaleOnSameSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(184, 0.4, 0))
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-55 * angleSign, 0.35))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			gripper.autoSetMiddleTargetRange();
			if(drivetrain.driveToColor(color, 0.35, -55 * angleSign) && gripper.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				Timer.delay(0.5);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(drivetrain.driveToColor(color, -0.175, -55 * angleSign))
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			elevator.autoSetScaleTargetRange();
			if(drivetrain.spinToBearing(0 * angleSign, 0.35) && elevator.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
			}
		}
	}

	public void scaleOnOppositeSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(220, 0.75, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-90 * angleSign, 0.35))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(drivetrain.driveToColor(color, 0.3, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(drivetrain.driveDistance(55, 0.4, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(40, 0.25, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			gripper.autoSetMiddleTargetRange();
			if(drivetrain.driveToColor(color, 0.3, -90 * angleSign) && gripper.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive2Distance3;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance3)
		{
			if(drivetrain.driveDistance(17, 0.25, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			elevator.autoSetScaleTargetRange();
			if(drivetrain.spinToBearing(0 * angleSign, 0.35) && elevator.inTargetRange())
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
			}
		}
	}

	public static class Constants
	{
		enum AutoMode {kScaleOnOppositeSide, kScaleOnSameSide, kSwitchLeftFromMiddle, kSwitchRightFromMiddle, kSwitchOnSameSide, kAutoLine, kNone}
		enum AutoStage {kDone, kDrive1, kSpin1, kDrive2ToLine1, kDrive2Distance1, kDrive2Distance2, kDrive2ToLine2, kDrive2Distance3, kSpin2, kDrive3ToLine}
		public static final int GREEN_CAMERA_PORT = 10;
		public static final int WHITE_CAMERA_PORT = 11;
		public static final int WHITE_FLOOR_PORT = 12;
	}
}
