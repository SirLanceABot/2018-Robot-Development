package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.util.LightRing;
import org.usfirst.frc.team4237.robot.sensors.AMSColorSensor;
import org.usfirst.frc.team4237.robot.network.AutoSelect4237;
import org.usfirst.frc.team4237.robot.components.Elevator;
import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class for autonomous robot functions
 * @author Julien Thrum, Mark Washington
 *
 */
public class Autonomous
{
	private Drivetrain drivetrain = Drivetrain.getInstance();

	private DriverStation driverStation = DriverStation.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Gripper gripper = Gripper.getInstance();
	private AutoSelect4237 autoSelect4237 = AutoSelect4237.getInstance();

	private LightRing greenCameraLight = new LightRing(Constants.GREEN_CAMERA_PORT);
	private LightRing whiteCameraLight = new LightRing(Constants.WHITE_CAMERA_PORT);
	private LightRing whiteFloorLight = new LightRing(Constants.WHITE_FLOOR_PORT);

	private String fieldColors = null;
	private DriverStation.Alliance allianceColor;
	private int angleSign;
	private int switchAngleSign = 1;
	private AMSColorSensor.Constants.Color color;
	private Constants.AutoMode autoMode = Constants.AutoMode.kNone;
	private Constants.AutoStage autoStage = Constants.AutoStage.kDrive1;
	//Yash's switch stuff
	private Constants.AutonStage autonStage = Constants.AutonStage.kDrive1;

	private String selectedPosition = null;
	private String planA = null;
	private String planB = null;
	private String planC = null;
	private boolean isFieldColorsSet = false;

	//	private boolean doneMovingComponent = false;
	private boolean doneMovingGripper = false;
	private boolean doneMovingElevator = false;
	private boolean doneDriving = false;
	//Yash's switch stuff
	private boolean doneShooting = false;

	private Timer timer = new Timer();

	private static Autonomous instance = new Autonomous();

	/**
	 * Method to get instance of autonomous code
	 * @return
	 */
	public static Autonomous getInstance()
	{
		return instance;
	}

	/**
	 * Constructor for Autonomous class
	 */
	private Autonomous()
	{
		autoSelect4237.start();
	}

	/**
	 * Autonomous initialization method
	 */
	public void init()
	{
		timer.stop();
		timer.reset();
		timer.start();

		selectedPosition = autoSelect4237.getData().getSelectedPosition();
		planA = autoSelect4237.getData().getPlanA();
		planB = autoSelect4237.getData().getPlanB();
		planC = autoSelect4237.getData().getPlanC();

		System.out.println("Selected position: " + selectedPosition);
		System.out.println("Plan A: " + planA);
		System.out.println("Plan B: " + planB);
		System.out.println("Plan C: " + planC);
		//		System.out.println("Selected position: " + selectedPosition);
		//		System.out.println("Selected target: " + selectedTarget);
		//		System.out.println("Selected backup plan: " + selectedBackupPlan);

		allianceColor = driverStation.getAlliance();
		System.out.println("Alliance color: " + allianceColor);

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
		
		if(fieldColors.charAt(0) == 'L')
		{
			switchAngleSign = -1;
		}

		drivetrain.resetEncoder();
		drivetrain.resetNavX();

		autoStage = Constants.AutoStage.kDrive1;

		turnLightRingsOn();

	}

	/**
	 * Method where the robot figures out which autonomous mode to run
	 * It takes data from the FMs and AutoSelect4237 to find the 
	 * correct mode to run.
	 */
	public void setAutoMode()
	{
		if(selectedPosition.equalsIgnoreCase("left"))
		{
			if(planA.equalsIgnoreCase("left scale") && fieldColors.charAt(1) == 'L')
			{
				autoMode = Constants.AutoMode.kScaleOnSameSide;
			}
			else if(planA.equalsIgnoreCase("right scale") && fieldColors.charAt(1) == 'R')
			{
				autoMode = Constants.AutoMode.kScaleOnOppositeSide;
			}
			else if(planA.equalsIgnoreCase("left switch") && fieldColors.charAt(0) == 'L')
			{
				autoMode = Constants.AutoMode.kSwitchOnSameSide;
			}
			else if(planA.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(planB.equalsIgnoreCase("left scale") && fieldColors.charAt(1) == 'L')
			{
				autoMode = Constants.AutoMode.kScaleOnSameSide;
			}
			else if(planB.equalsIgnoreCase("right scale") && fieldColors.charAt(1) == 'R')
			{
				autoMode = Constants.AutoMode.kScaleOnOppositeSide;
			}
			else if(planB.equalsIgnoreCase("left switch") && fieldColors.charAt(0) == 'L')
			{
				autoMode = Constants.AutoMode.kSwitchOnSameSide;
			}
			else if(planB.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(planC.equalsIgnoreCase("left scale") && fieldColors.charAt(1) == 'L')
			{
				autoMode = Constants.AutoMode.kScaleOnSameSide;
			}
			else if(planC.equalsIgnoreCase("right scale") && fieldColors.charAt(1) == 'R')
			{
				autoMode = Constants.AutoMode.kScaleOnOppositeSide;
			}
			else if(planC.equalsIgnoreCase("left switch") && fieldColors.charAt(0) == 'L')
			{
				autoMode = Constants.AutoMode.kSwitchOnSameSide;
			}
			else if(planC.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
		}
		else if(selectedPosition.equalsIgnoreCase("center"))
		{
			if(planA.equalsIgnoreCase("left switch") && fieldColors.charAt(0) == 'L')
			{
				autoMode = Constants.AutoMode.kSwitchLeftFromMiddle;
			}
			else if(planA.equalsIgnoreCase("right switch") && fieldColors.charAt(0) == 'R')
			{
				autoMode = Constants.AutoMode.kSwitchRightFromMiddle;
			}
			else if(planA.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(planB.equalsIgnoreCase("left switch") && fieldColors.charAt(0) == 'L')
			{
				autoMode = Constants.AutoMode.kSwitchLeftFromMiddle;
			}
			else if(planB.equalsIgnoreCase("right switch") && fieldColors.charAt(0) == 'R')
			{
				autoMode = Constants.AutoMode.kSwitchRightFromMiddle;
			}
			else if(planB.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
		}
		else if(selectedPosition.equalsIgnoreCase("right"))
		{
			if(planA.equalsIgnoreCase("right scale") && fieldColors.charAt(1) == 'R')
			{
				autoMode = Constants.AutoMode.kScaleOnSameSide;
			}
			else if(planA.equalsIgnoreCase("left scale") && fieldColors.charAt(1) == 'L')
			{
				autoMode = Constants.AutoMode.kScaleOnOppositeSide;
			}
			else if(planA.equalsIgnoreCase("right switch") && fieldColors.charAt(0) == 'R')
			{
				autoMode = Constants.AutoMode.kSwitchOnSameSide;
			}
			else if(planA.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(planB.equalsIgnoreCase("right scale") && fieldColors.charAt(1) == 'R')
			{
				autoMode = Constants.AutoMode.kScaleOnSameSide;
			}
			else if(planB.equalsIgnoreCase("left scale") && fieldColors.charAt(1) == 'L')
			{
				autoMode = Constants.AutoMode.kScaleOnOppositeSide;
			}
			else if(planB.equalsIgnoreCase("right switch") && fieldColors.charAt(0) == 'R')
			{
				autoMode = Constants.AutoMode.kSwitchOnSameSide;
			}
			else if(planB.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
			else if(planC.equalsIgnoreCase("right scale") && fieldColors.charAt(1) == 'R')
			{
				autoMode = Constants.AutoMode.kScaleOnSameSide;
			}
			else if(planC.equalsIgnoreCase("left scale") && fieldColors.charAt(1) == 'L')
			{
				autoMode = Constants.AutoMode.kScaleOnOppositeSide;
			}
			else if(planC.equalsIgnoreCase("right switch") && fieldColors.charAt(0) == 'R')
			{
				autoMode = Constants.AutoMode.kSwitchOnSameSide;
			}
			else if(planC.equalsIgnoreCase("auto line"))
			{
				autoMode = Constants.AutoMode.kAutoLine;
			}
		}
		else
		{
			autoMode = Constants.AutoMode.kNone;
		}
	}

	/**
	 * Autonomous periodic method
	 */
	public void periodic()
	{
		if(!isFieldColorsSet)
		{
			fieldColors = driverStation.getGameSpecificMessage(); //Get colors of field components from FMS

			if(fieldColors != null && !fieldColors.equals(""))
			{
				System.out.println("GameSpecificMessage: " + fieldColors);
				isFieldColorsSet = true;
				setAutoMode();
			}
		}
		else
		{
			if(autoMode == Constants.AutoMode.kScaleOnOppositeSide)
			{
				scaleOnOppositeSideNoColors();
			}
			else if(autoMode == Constants.AutoMode.kScaleOnSameSide)
			{
				if(autoSelect4237.getData().getGrabSecondCube())
				{
					if(autoSelect4237.getData().getHoldSecondCube())
					{
						scaleOnSameSideOnAngleTwoCubes();
					}
					else
					{
						if(autoSelect4237.getData().getPlaceSecondCubeInScale())
						{
							scaleOnSameSideOnAngleTwoCubesAndScale();
						}
						else if(autoSelect4237.getData().getPlaceSecondCubeInSwitch())
						{
							if((fieldColors.charAt(0) == 'L' && selectedPosition.equalsIgnoreCase("left")) || (fieldColors.charAt(0) == 'R' && selectedPosition.equalsIgnoreCase("right")))
							{
								scaleOnSameSideOnAngleTwoCubesAndSwitch();
							}
							else
							{
								scaleOnSameSideOnAngleTwoCubesAndScale();
							}
						}
					}
				}
				else
				{
					scaleOnSameSideOnAngleOneCube();
				}
			}
			else if(autoMode == Constants.AutoMode.kSwitchOnSameSide)
			{
				switchOnSameSideOnEnd();
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
			else if(autoMode == Constants.AutoMode.kNone)
			{
				autoStage = Constants.AutoStage.kDone;
			}
		}

		if(autoStage == Constants.AutoStage.kDone)
		{
			turnLightRingsOff();
		}
	}

	public void turnLightRingsOn()
	{
		whiteCameraLight.set(true);
		greenCameraLight.set(true);
		whiteFloorLight.set(true);
	}

	public void turnLightRingsOff()
	{
		whiteCameraLight.set(false);
		greenCameraLight.set(false);
		whiteFloorLight.set(false);
	}

	public void autoLine()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			System.out.println("Timer: " + timer.get());
			if(!drivetrain.driveDistance(90, 0.5, 0, 48) && timer.get() <= 4.0)
			{

			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				drivetrain.driveCartesian(0, 0, 0);
				drivetrain.resetEncoder();
			}
		}
	}
	public void switchFromMiddle()
	{
//		if(autonStage == Constants.AutonStage.kDrive1)
//		{
//			if(!doneDriving || !doneMovingGripper || !doneMovingElevator)
//			{
//				if(!doneMovingGripper)
//				{
//					doneMovingGripper = gripper.autoHorizontal();
//				}
//
//				if(!doneDriving)
//				{
//					doneDriving = drivetrain.strafeDistanceAtAngle(95, 21 * switchAngleSign, 1, 0);
//				}
//
//				if(!doneMovingElevator)
//				{
//					doneMovingElevator = elevator.autoSwitch();
//				}
//				
//				if(timer.get() >= 3.5)
//				{
//					drivetrain.driveCartesian(0, 0, 0);
//					gripper.pivotOff();
//					autonStage = Constants.AutonStage.kDone;
//				}
//			}
//			else
//			{
//				autonStage = Constants.AutonStage.kShoot1;
//				System.out.println("Entering: " + autonStage);
//				doneMovingGripper = false;
//				doneDriving = false;
//			}
//		}
		if(autonStage == Constants.AutonStage.kDrive1)
		{
			if(!doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(24, 0.9, 0, 7);
				}

				if(timer.get() >= 3.5)
				{
					drivetrain.driveCartesian(0, 0, 0);
					gripper.pivotOff();
					autonStage = Constants.AutonStage.kDone;
				}
			}
			else
			{
				autonStage = Constants.AutonStage.kSpin1;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kSpin1)
		{
			if(!doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(45 * switchAngleSign, 0.4);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive2;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive2)
		{
			if(!doneDriving || !doneMovingElevator || !doneMovingGripper)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(80, 0.9, 45 * switchAngleSign, 20);
				}
				
				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				autonStage = Constants.AutonStage.kSpin2;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kSpin2)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(0, 0.4);
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive3;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive3)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(37, 0.9, 0, 10);
			}
			else
			{
				autonStage = Constants.AutonStage.kShoot1;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kShoot1)
		{
			if(!doneShooting)
			{
				doneShooting = gripper.autoDrop();
			}
			else
			{
				autonStage = Constants.AutonStage.kDrive4;
				System.out.println("Entering: " + autonStage);
				doneShooting = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive4)
		{
			if(!doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(20, -0.9, 0, 5);
				}
					
				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				autonStage = Constants.AutonStage.kSpin3;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kSpin3)
		{
			if(!doneMovingElevator || !doneDriving || !doneMovingGripper)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-80 * switchAngleSign, 0.4);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoFloor();
				}
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive5;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive5)
		{
			if(!doneDriving)
			{
				gripper.intakeRotateCubeRight();
				doneDriving = drivetrain.driveDistance(41, 0.5, -80 * switchAngleSign, 10);
			}
			else
			{
				drivetrain.resetEncoder();
				gripper.intakeOff();
				autonStage = Constants.AutonStage.kDrive6;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive6)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(41, -0.9, -80 * switchAngleSign, 10);

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				autonStage = Constants.AutonStage.kSpin4;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kSpin4)
		{
			if(!doneMovingElevator || !doneDriving || !doneMovingGripper)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(0, 0.4);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive7;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive7)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(20, 0.9, 0, 5);
			}
			else
			{
				autonStage = Constants.AutonStage.kShoot2;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kShoot2)
		{
			if(!doneShooting)
			{
				doneShooting = gripper.autoDrop();
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive8;
				System.out.println("Entering: " + autonStage);
				doneShooting = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive8)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(54, -0.9, 0, 15);

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				autonStage = Constants.AutonStage.kSpin5;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kSpin5)
		{
			if(!doneMovingElevator || !doneDriving || !doneMovingGripper)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-75 * switchAngleSign, 0.4);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoFloor();
				}
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive9;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive9)
		{
			if(!doneDriving)
			{
				gripper.intakeRotateCubeRight();
				doneDriving = drivetrain.driveDistance(54, 0.5, -75 * switchAngleSign, 15);
			}
			else
			{
				drivetrain.resetEncoder();
				gripper.intakeOff();
				autonStage = Constants.AutonStage.kDrive10;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive10)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(54, -0.9, -75 * switchAngleSign, 15);

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				autonStage = Constants.AutonStage.kSpin6;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kSpin6)
		{
			if(!doneMovingElevator || !doneDriving || !doneMovingGripper)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(0, 0.4);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
				
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				drivetrain.resetEncoder();
				autonStage = Constants.AutonStage.kDrive11;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kDrive11)
		{
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(54, 0.9, 0, 15);
			}
			else
			{
				autonStage = Constants.AutonStage.kShoot3;
				System.out.println("Entering: " + autonStage);
				doneDriving = false;
			}
		}
		else if(autonStage == Constants.AutonStage.kShoot3)
		{
			if(!doneShooting)
			{
				doneShooting = gripper.autoDrop();
			}
			else
			{
				autonStage = Constants.AutonStage.kDone;
				System.out.println("Entering: " + autonStage);
				doneShooting = false;
			}
		}
	}
	
	public void switchLeftFromMiddle()
	{
//		if(autoStage == Constants.AutoStage.kDrive1)
//		{
//		if(!doneDriving || !doneMovingElevator || !doneMovingGripper)
//		{
//			if(!doneMovingGripper)
//			{
//				doneMovingGripper = gripper.autoHorizontal();
//			}
//
//			if(!doneMovingElevator)
//			{
//				doneMovingElevator = elevator.autoSwitch();
//			}
//
//			if(!doneDriving)
//			{
//				doneDriving = drivetrain.strafeDistanceAtAngle(95, -21, 1, 0);
//			}
//		}
//		else
//		{
//			gripper.autoDrop();
//			autoStage = Constants.AutoStage.kDone;
//			System.out.println("Entering: " + autoStage);
//			drivetrain.resetEncoder();
//			doneDriving = false;
//		}
//		}
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
			}
			//			if(!doneDriving && !doneMovingGripper)
			//			{
			//				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
			//				doneMovingGripper = gripper.autoHorizontal();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoHorizontal();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2Distance1;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingGripper = false;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneDriving || !doneMovingElevator || !doneMovingGripper)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(95, 0.75, -45, 48);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}

			//			if(!doneDriving && !doneMovingElevator)
			//			{
			//				doneDriving = drivetrain.driveDistance(95, 0.75, -45, 48);
			//				doneMovingElevator = elevator.autoSwitch();
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoSwitch();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(95, 0.75, -45, 48);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2Distance2;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingElevator = false;
			//				t.stop();
			//				t.reset();
			//				t.start();
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveSeconds(0.3, 1, 0))
			{
				gripper.ejectDrop();
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
			}
		}
	}

	public void switchRightFromMiddle()
	{
//		if(autoStage == Constants.AutoStage.kDrive1)
//		{
//		if(!doneDriving || !doneMovingElevator || !doneMovingGripper)
//		{
//			if(!doneMovingGripper)
//			{
//				doneMovingGripper = gripper.autoHorizontal();
//			}
//
//			if(!doneMovingElevator)
//			{
//				doneMovingElevator = elevator.autoSwitch();
//			}
//
//			if(!doneDriving)
//			{
//				doneDriving = drivetrain.strafeDistanceAtAngle(95, 21, 1, 0);
//			}
//		}
//		else
//		{
//			gripper.autoDrop();
//			autoStage = Constants.AutoStage.kDone;
//			System.out.println("Entering: " + autoStage);
//			drivetrain.resetEncoder();
//			doneDriving = false;
//		}
//		}
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
			}
			//			if(!doneDriving && !doneMovingGripper)
			//			{
			//				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
			//				doneMovingGripper = gripper.autoHorizontal();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoHorizontal();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2Distance1;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingGripper = false;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneDriving || !doneMovingElevator || !doneMovingGripper)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(90, 0.75, 35, 48);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}

			//			if(!doneDriving && !doneMovingElevator)
			//			{
			//				doneDriving = drivetrain.driveDistance(90, 0.75, 35, 48);
			//				doneMovingElevator = elevator.autoSwitch();
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoSwitch();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(90, 0.75, 35, 48);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2Distance2;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingElevator = false;
			//				t.stop();
			//				t.reset();
			//				t.start();
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveSeconds(0.3, 1, 0))
			{
				gripper.ejectDrop();
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
			}
		}
	}

	public void switchOnSameSideOnEnd()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			//			if(!doneDriving && !doneMovingGripper && !doneMovingElevator)
			//			{
			//				doneDriving = drivetrain.driveDistance(130, 0.6, 0, 36);
			//				doneMovingGripper = gripper.autoHorizontal();
			//				doneMovingElevator = elevator.autoSwitch();
			//			}
			if(!doneDriving || !doneMovingGripper || !doneMovingElevator)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(130, 0.6, 0, 36);
				}

				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-90 * angleSign, 0.35))
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				gripper.pivotOff();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(drivetrain.driveSeconds(0.2, 1.25, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.resetIntakeEncoder();
				gripper.ejectDrop();
				drivetrain.resetEncoder();
			}
		}
	}

	public void scaleOnSameSideOnAngleOneCube()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{	
			if(!doneMovingGripper || !doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoMiddle();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
				}

				if(timer.get() >= 3.5)
				{
					drivetrain.driveCartesian(0, 0, 0);
					gripper.pivotOff();
					autoStage = Constants.AutoStage.kDone;
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingGripper = false;
			}

			//			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			//			{
			//				drivetrain.driveDistance(220, 0.8, 0, 55);
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneDriving && t.get() <= 3.5)
			//			{
			//				doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2ToLine1;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingGripper = false;
			//			}
			//			
			//			if(t.get() >= 3.5)
			//			{
			//				drivetrain.driveCartesian(0, 0, 0);
			//				gripper.pivotOff();
			//				autoStage = Constants.AutoStage.kDone;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			//FIXME: take out !doneMovingElevator if gripper won't hit scale
			if(!doneDriving || !doneMovingElevator)
			{
				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}

			//			if(!doneMovingElevator && !doneDriving)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kSpin1;
			//				System.out.println("Entering: " + autoStage);
			//				doneMovingElevator = false;
			//				doneDriving = false;
			//				drivetrain.resetEncoder();
			//			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(!doneMovingElevator || !doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-45 * angleSign, 0.3);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				gripper.ejectShoot();
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(drivetrain.driveSeconds(-0.2, 1.0, -45 * angleSign))
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
			}
		}
		//TODO move elevator back down if enough time
	}

	public void scaleOnSameSideOnAngleTwoCubes()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{	
			if(!doneMovingGripper || !doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoMiddle();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
				}

				if(timer.get() >= 3.5)
				{
					drivetrain.driveCartesian(0, 0, 0);
					gripper.pivotOff();
					autoStage = Constants.AutoStage.kDone;
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingGripper = false;
			}

			//			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			//			{
			//				drivetrain.driveDistance(220, 0.8, 0, 55);
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneDriving && t.get() <= 3.5)
			//			{
			//				doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2ToLine1;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingGripper = false;
			//			}
			//			
			//			if(t.get() >= 3.5)
			//			{
			//				drivetrain.driveCartesian(0, 0, 0);
			//				gripper.pivotOff();
			//				autoStage = Constants.AutoStage.kDone;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneDriving || !doneMovingElevator)
			{
				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}

			//			if(!doneMovingElevator && !doneDriving)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kSpin1;
			//				System.out.println("Entering: " + autoStage);
			//				doneMovingElevator = false;
			//				doneDriving = false;
			//				drivetrain.resetEncoder();
			//			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(!doneMovingElevator || !doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-45 * angleSign, 0.3);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				gripper.ejectShoot();
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-150 * angleSign, 0.3);
				}
				else
				{
					gripper.intakeOff();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				drivetrain.resetEncoder();
				restartTimer();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneMovingGripper || !doneDriving ||!doneMovingElevator)
			{
				gripper.intake();
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(75, 0.25, -150 * angleSign, 30);
				}

				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoFloor();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
	}

	public void scaleOnSameSideOnAngleTwoCubesAndSwitch()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{	
			if(!doneMovingGripper || !doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoMiddle();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
				}

				if(timer.get() >= 3.5)
				{
					drivetrain.driveCartesian(0, 0, 0);
					gripper.pivotOff();
					autoStage = Constants.AutoStage.kDone;
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingGripper = false;
			}

			//			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			//			{
			//				drivetrain.driveDistance(220, 0.8, 0, 55);
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneDriving && t.get() <= 3.5)
			//			{
			//				doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2ToLine1;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingGripper = false;
			//			}
			//			
			//			if(t.get() >= 3.5)
			//			{
			//				drivetrain.driveCartesian(0, 0, 0);
			//				gripper.pivotOff();
			//				autoStage = Constants.AutoStage.kDone;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneDriving || !doneMovingElevator)
			{
				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}

			//			if(!doneMovingElevator && !doneDriving)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kSpin1;
			//				System.out.println("Entering: " + autoStage);
			//				doneMovingElevator = false;
			//				doneDriving = false;
			//				drivetrain.resetEncoder();
			//			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(!doneMovingElevator || !doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-45 * angleSign, 0.3);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				gripper.ejectShoot();
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-150 * angleSign, 0.3);
				}
				else
				{
					gripper.intakeOff();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneMovingGripper || !doneDriving ||!doneMovingElevator)
			{
				gripper.intake();
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(75, 0.25, -150 * angleSign, 30);
				}

				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoFloor();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
				doneMovingElevator = false;
				restartTimer();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!doneDriving || !doneMovingGripper || !doneMovingElevator)
			{
				if(!doneDriving)
				{
					if(timer.get() >= 1.0)
					{
						doneDriving = drivetrain.driveSeconds(0.25, 0.5, -180 * angleSign);
					}
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoSwitch();
				}

				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoHorizontal();
				}
			}
			else
			{
				gripper.ejectDrop();
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneMovingElevator = false;
				doneDriving = false;
			}
		}
	}

	public void scaleOnSameSideOnAngleTwoCubesAndScale()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{	
			if(!doneMovingGripper || !doneDriving)
			{
				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoMiddle();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
				}

				if(timer.get() >= 3.5)
				{
					drivetrain.driveCartesian(0, 0, 0);
					gripper.pivotOff();
					autoStage = Constants.AutoStage.kDone;
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingGripper = false;
			}

			//			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			//			{
			//				drivetrain.driveDistance(220, 0.8, 0, 55);
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneDriving && t.get() <= 3.5)
			//			{
			//				doneDriving = drivetrain.driveDistance(220, 0.8, 0, 55);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2ToLine1;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneDriving = false;
			//				doneMovingGripper = false;
			//			}
			//			
			//			if(t.get() >= 3.5)
			//			{
			//				drivetrain.driveCartesian(0, 0, 0);
			//				gripper.pivotOff();
			//				autoStage = Constants.AutoStage.kDone;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneDriving || !doneMovingElevator)
			{
				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}

			//			if(!doneMovingElevator && !doneDriving)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kSpin1;
			//				System.out.println("Entering: " + autoStage);
			//				doneMovingElevator = false;
			//				doneDriving = false;
			//				drivetrain.resetEncoder();
			//			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(!doneMovingElevator || !doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-45 * angleSign, 0.3);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				gripper.ejectShoot();
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-150 * angleSign, 0.3);
				}
				else
				{
					gripper.intakeOff();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneMovingGripper || !doneDriving ||!doneMovingElevator)
			{
				gripper.intake();
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(75, 0.25, -150 * angleSign, 30);
				}

				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoFloor();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoFloor();
				}
			}
			else
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
				doneMovingElevator = false;
				restartTimer();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!doneDriving || !doneMovingGripper)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(75, -0.4, -150 * angleSign, 12);
				}

				if(!doneMovingGripper)
				{
					doneMovingGripper = gripper.autoMiddle();
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance3;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance3)
		{
			if(!doneDriving || !doneMovingElevator)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(-45 * angleSign, 0.3);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				gripper.ejectShoot();
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
	}

	public void scaleOnOppositeSideNoColors()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(215, 0.8, 0 * angleSign, 55))
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
			}

			if(timer.get() >= 3.5)
			{
				autoStage = Constants.AutoStage.kDone;
				drivetrain.driveCartesian(0, 0, 0);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-90 * angleSign, 0.35))
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneDriving || !doneMovingGripper)
			{
				if(!doneMovingGripper)
				{
					if(!autoSelect4237.getData().getHoldCubeForOppositeScale())
					{
						doneMovingGripper = gripper.autoMiddle();
					}
					else
					{
						doneMovingGripper = true;
					}
				}

				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(130, 0.6, -90 * angleSign, 48);
				}
			}
			else
			{
				if(!autoSelect4237.getData().getHoldCubeForOppositeScale())
				{
					autoStage = Constants.AutoStage.kDrive2Distance2;
				}
				else
				{
					autoStage = Constants.AutoStage.kDone;
				}
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneMovingGripper = false;
				doneDriving = false;
			}

			//			if(!doneDriving && !doneMovingGripper)
			//			{
			//				doneDriving = drivetrain.driveDistance(130, 0.6, -90 * angleSign, 48);
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneMovingGripper)
			//			{
			//				doneMovingGripper = gripper.autoMiddle();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(130, 0.6, -90 * angleSign, 48);
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kDrive2Distance2;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneMovingGripper = false;
			//				doneDriving = false;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.driveDistance(71, 0.5, -90 * angleSign, 24);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
			}

			//			if(!doneMovingElevator && !doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(71, 0.5, -90 * angleSign, 24);
			//				doneMovingElevator = elevator.autoTopScale();
			//			}
			//			else if(!doneDriving)
			//			{
			//				doneDriving = drivetrain.driveDistance(71, 0.5, -90 * angleSign, 24);
			//			}
			//			else if(!doneMovingElevator)
			//			{
			//				doneMovingElevator = elevator.autoTopScale();
			//			}
			//			else
			//			{
			//				autoStage = Constants.AutoStage.kSpin2;
			//				System.out.println("Entering: " + autoStage);
			//				drivetrain.resetEncoder();
			//				doneMovingGripper = false;
			//				doneDriving = false;
			//			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneMovingElevator || !doneDriving)
			{
				if(!doneDriving)
				{
					doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.3);
				}

				if(!doneMovingElevator)
				{
					doneMovingElevator = elevator.autoTopScale();
				}
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneMovingElevator = false;
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
				gripper.ejectShoot();
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(drivetrain.driveSeconds(-.2, 1.0, 0))
			{
				gripper.intakeOff();
				autoStage = Constants.AutoStage.kDone;
			}
		}
	}

	public void restartTimer()
	{
		timer.stop();
		timer.reset();
		timer.start();
	}

	public static class Constants
	{
		enum AutoMode {kScaleOnOppositeSide, kScaleOnSameSide, kSwitchLeftFromMiddle, kSwitchRightFromMiddle, kSwitchOnSameSide, kAutoLine, kNone}
		enum AutoStage {
			kDone,                                  //Done

			kDrive1,                              //Drive forward

			kSpin1,                             //After first drive...
			//Switch on same side: 90 degree angle
			//Scale on same side: 45 degree angle
			//Scale on opposite side: 90 degree angle

			kDrive2ToLine1,                //Drive to white line

			kDrive2Distance1,            //Switch from middle, drive at +/-45 degree angle
			//Scale on opposite side, drive up to bump

			kDrive2Distance2,         //Scale on opposite side, drive after bump

			kDrive2ToLine2,         //Unused

			kDrive2Distance3,     //Unused as of now

			kSpin2,             //Used for collecting second cube

			kDrive3ToLine     //Used for scale on opposite side without color sensor
			
			
			//FIXME: Michael would like an auto mode for end of scale
		}
		
		enum AutonStage {
			kDone, 																									//done
			
			kDrive1, kDrive2, kDrive3, kDrive4, kDrive5, kDrive6, kDrive7, kDrive8, kDrive9, kDrive10, kDrive11,	//Drive a certain distance in a certain direction
			
			kSpin1, kSpin2, kSpin3, kSpin4, kSpin5, kSpin6,	kSpin7,														//Spin a certain amount
			
			kShoot1, kShoot2, kShoot3																				//Shoot Cube
		}

		public static final int GREEN_CAMERA_PORT = 10;
		public static final int WHITE_CAMERA_PORT = 11;
		public static final int WHITE_FLOOR_PORT = 12;
	}
}