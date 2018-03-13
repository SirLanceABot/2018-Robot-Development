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

public class Autonomous
{
	private Drivetrain drivetrain = Drivetrain.getInstance();

	private DriverStation driverStation = DriverStation.getInstance();
	private Elevator elevator = Elevator.getInstance();
	private Gripper gripper = Gripper.getInstance();
	private AutoSelect4237 autoSelect4237 = AutoSelect4237.getInstance();
	private String fieldColors = null;
	private DriverStation.Alliance allianceColor;
	private int angleSign;
	private AMSColorSensor.Constants.Color color;
	private Constants.AutoMode autoMode = Constants.AutoMode.kNone;
	private Constants.AutoStage autoStage = Constants.AutoStage.kDrive1;
	private LightRing greenCameraLight = new LightRing(Constants.GREEN_CAMERA_PORT);
	private LightRing whiteCameraLight = new LightRing(Constants.WHITE_CAMERA_PORT);
	private LightRing whiteFloorLight = new LightRing(Constants.WHITE_FLOOR_PORT);

	private String selectedPosition = null;
	private String planA = null;
	private String planB = null;
	private String planC = null;
	private boolean isFieldColorsSet = false;
	
//	private boolean doneMovingComponent = false;
	private boolean doneMovingGripper = false;
	private boolean doneMovingElevator = false;
	private boolean doneDriving = false;
	
	private Timer t = new Timer();

	private static Autonomous instance = new Autonomous();
	public static Autonomous getInstance()
	{
		return instance;
	}

	private Autonomous()
	{
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

		drivetrain.resetEncoder();
		drivetrain.resetNavX();

		autoStage = Constants.AutoStage.kDrive1;
		
		turnLightRingsOn();
	}

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
	 * Autonomous loop
	 */
	public void periodic()
	{
		if(!isFieldColorsSet)
		{
			fieldColors = driverStation.getGameSpecificMessage();
			
			if(fieldColors != null || !fieldColors.equals(""))
			{
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
					if(autoSelect4237.getData().getPlaceSecondCubeInSwitch() &&
						((fieldColors.charAt(1) == 'L' && fieldColors.charAt(0) == 'L' && selectedPosition.equalsIgnoreCase("left")) ||
						 (fieldColors.charAt(1) == 'R' && fieldColors.charAt(0) == 'R' && selectedPosition.equalsIgnoreCase("right"))))
					{
						scaleOnSameSideOnAngle2CubeAndSwitch();
					}
					else if(autoSelect4237.getData().getPlaceSecondCubeInScale())
					{
						scaleOnSameSideOnAngle2CubeAndScale();
					}
					else
					{
						scaleOnSameSideOnAngle2Cube();
					}
				}
				else
				{
					scaleOnSameSideOnAngle1Cube();
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
			System.out.println("Timer: " + t.get());
			if(!drivetrain.driveDistance(90, 0.5, 0, 48) && t.get() <= 4.0)
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

	public void switchLeftFromMiddle()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving && !doneMovingGripper)
			{
				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneDriving && !doneMovingElevator)
			{
				doneDriving = drivetrain.driveDistance(95, 0.75, -45, 48);
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(95, 0.75, -45, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(15, 0.25, 0, 48))
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
			if(!doneDriving && !doneMovingGripper)
			{
				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(5, 0.3, 0, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			
			if(!doneDriving && !doneMovingElevator)
			{
				doneDriving = drivetrain.driveDistance(90, 0.75, 35, 48);
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(90, 0.75, 35, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(9, 0.25, 0, 48))
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
			if(!doneDriving && !doneMovingGripper)
			{
				doneDriving = drivetrain.driveDistance(15, 0.3, 0 * angleSign, 48);
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(5, 0.3, 0 * angleSign, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneDriving && !doneMovingElevator)
			{
				doneDriving = drivetrain.driveDistance(90, 0.4, -45 * angleSign, 48);
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(90, 0.4, -45 * angleSign, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
				t.stop();
				t.reset();
				t.start();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(10, 0.25, 0 * angleSign, 48) || t.get() >= 2)
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
			}
		}
	}
	
	public void switchOnSameSideOnEnd()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving && !doneMovingGripper && !doneMovingElevator)
			{
				doneDriving = drivetrain.driveDistance(150, 0.6, 0, 36);
				doneMovingGripper = gripper.autoHorizontal();
				doneMovingElevator = elevator.autoSwitch();
			}
			else if(doneDriving && doneMovingGripper && doneMovingElevator)
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
				doneMovingGripper = false;
			}
			
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(150, 0.6, 0, 36);
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
			if(drivetrain.driveSeconds(0.2, 2, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				drivetrain.resetEncoder();
			}
		}
	}
	
	public void scaleOnSameSideOnAngle1Cube()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			{
				drivetrain.driveDistance(260, 0.8, 0, 55);
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneDriving && t.get() <= 3.5)
			{
				doneDriving = drivetrain.driveDistance(260, 0.8, 0, 55);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
				Timer.delay(0.5);
			}
			
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneMovingElevator = elevator.autoTopScale();
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-45 * angleSign, 0.3))
			{
				gripper.autoEject();
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.driveSeconds(-0.2, 1.0, -45 * angleSign))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
	}
	
	public void scaleOnSameSideOnAngle2Cube()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			{
				drivetrain.driveDistance(260, 0.8, 0, 55);
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneDriving && t.get() <= 3.5)
			{
				doneDriving = drivetrain.driveDistance(260, 0.8, 0, 55);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
				Timer.delay(0.5);
			}
			
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneMovingElevator = elevator.autoTopScale();
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-45 * angleSign, 0.3))
			{
				gripper.autoEject();
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(-170 * angleSign, 0.3);
				
			}
			else if(!doneMovingElevator)
			{
				gripper.setAutoEjecting(false);
				doneMovingElevator = elevator.autoFloor();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(-170 * angleSign, 0.3);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				t.stop();
				t.reset();
				t.start();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneMovingGripper && !doneDriving)
			{
				gripper.setAutoIntaking(true);
				doneMovingGripper = gripper.autoHorizontal();
				doneDriving = drivetrain.driveDistance(30, 0.2, -170 * angleSign, 30);
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(30, 0.2, -170 * angleSign, 30);
			}
			else
			{
				if(t.get() >= 2.0)
				{
					gripper.setAutoIntaking(false);
				}
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
	}

	public void scaleOnSameSideOnAngle2CubeAndSwitch()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			{
				drivetrain.driveDistance(260, 0.8, 0, 55);
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneDriving && t.get() <= 3.5)
			{
				doneDriving = drivetrain.driveDistance(260, 0.8, 0, 55);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
				Timer.delay(0.5);
			}
			
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneMovingElevator = elevator.autoTopScale();
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-45 * angleSign, 0.3))
			{
				gripper.autoEject();
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(-170 * angleSign, 0.3);
				
			}
			else if(!doneMovingElevator)
			{
				gripper.setAutoEjecting(false);
				doneMovingElevator = elevator.autoFloor();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(-170 * angleSign, 0.3);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				t.stop();
				t.reset();
				t.start();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneMovingGripper && !doneDriving)
			{
				gripper.autoHorizontal();
				doneMovingGripper = gripper.autoHorizontal();
				doneDriving = drivetrain.driveDistance(30, 0.2, -170 * angleSign, 30);
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(30, 0.2, -170 * angleSign, 30);
			}
			else
			{
				if(t.get() >= 2.0)
				{
					gripper.setAutoIntaking(false);
				}
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!doneDriving && !doneMovingGripper && !doneMovingElevator)
			{
				doneDriving = drivetrain.driveSeconds(0.2, 1.0, -180 * angleSign);
				doneMovingElevator = elevator.autoSwitch();
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(doneDriving && doneMovingGripper && doneMovingElevator)
			{
				gripper.setAutoEjecting(true);
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneMovingElevator = false;
				doneDriving = false;
			}
			
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveSeconds(0.2, 1.0, -180 * angleSign);
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
	}
	
	public void scaleOnSameSideOnAngle2CubeAndScale()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(!doneDriving && !doneMovingGripper && t.get() <= 3.5)
			{
				drivetrain.driveDistance(260, 0.8, 0, 55);
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneDriving && t.get() <= 3.5)
			{
				doneDriving = drivetrain.driveDistance(260, 0.8, 0, 55);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
				Timer.delay(0.5);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneMovingElevator = elevator.autoTopScale();
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.25, 0);
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-45 * angleSign, 0.3))
			{
				gripper.autoEject();
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(-170 * angleSign, 0.3);
				
			}
			else if(!doneMovingElevator)
			{
				gripper.setAutoEjecting(false);
				doneMovingElevator = elevator.autoFloor();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(-170 * angleSign, 0.3);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				doneMovingElevator = false;
				doneDriving = false;
				t.stop();
				t.reset();
				t.start();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(!doneMovingGripper && !doneDriving)
			{
				gripper.autoHorizontal();
				doneMovingGripper = gripper.autoHorizontal();
				doneDriving = drivetrain.driveDistance(30, 0.2, -170 * angleSign, 30);
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoHorizontal();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(30, 0.2, -170 * angleSign, 30);
			}
			else
			{
				if(t.get() >= 2.0)
				{
					gripper.setAutoIntaking(false);
				}
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!doneDriving && !doneMovingGripper && !doneMovingElevator)
			{
				doneDriving = drivetrain.driveSeconds(-0.2, 2.0, -170 * angleSign);
				doneMovingElevator = elevator.autoTopScale();
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(doneDriving && doneMovingGripper && doneMovingElevator)
			{
				autoStage = Constants.AutoStage.kDrive2Distance3;
				System.out.println("Entering: " + autoStage);
				doneMovingGripper = false;
				doneMovingElevator = false;
				doneDriving = false;
			}
			
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveSeconds(-0.2, 2.0, -170 * angleSign);
			}
			
			if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			
			if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance3)
		{
			if(drivetrain.spinToBearing(-45 * angleSign, 0.3))
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
			}
		}
	}
	
	public void scaleOnSameSide()
	{
		drivetrain.printTestInfo();
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(179, 0.7, 0, 48))
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-60 * angleSign, 0.35))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine1;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(!doneDriving && !doneMovingGripper)
			{
				doneDriving = drivetrain.driveToColor(color, 0.2, -60 * angleSign);
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveToColor(color, 0.2, -60 * angleSign);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
				Timer.delay(0.5);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(drivetrain.driveToColor(color, -0.175, -60 * angleSign))
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneDriving && !doneMovingElevator)
			{
				doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.35);
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.35);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				System.out.println("Entering: " + autoStage);
				doneDriving = false;
				doneMovingElevator = false;
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2Distance3;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
				doneDriving = false;
				t.stop();
				t.reset();
				t.start();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance3)
		{
			if(drivetrain.driveSeconds(-.2, 1.0, 0))
			{
				autoStage = Constants.AutoStage.kDone;
			}
		}
	}
	
	public void scaleOnOppositeSideNoColors()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(215, 0.8, 0 * angleSign, 55) || t.get() >= 3.5)
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
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
			if(!doneDriving && !doneMovingGripper)
			{
				doneDriving = drivetrain.driveDistance(130, 0.75, -90 * angleSign, 48);
				doneMovingGripper = gripper.autoMiddle();
			}
			if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(130, 0.75, -90 * angleSign, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(!doneMovingElevator && !doneDriving)
			{
				doneDriving = drivetrain.driveDistance(71, 0.5, -90 * angleSign, 24);
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(71, 0.5, -90 * angleSign, 24);
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneMovingGripper = false;
				doneDriving = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(drivetrain.spinToBearing(0 * angleSign, 0.3))
			{
				autoStage = Constants.AutoStage.kDrive3ToLine;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneMovingElevator = false;
				doneDriving = false;
			}
//			if(!doneDriving && !doneMovingElevator)
//			{
//				doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.35);
//				doneMovingElevator = elevator.autoTopScale();
//			}
//			else if(!doneDriving)
//			{
//				doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.35);
//			}
//			else if(!doneMovingElevator)
//			{
//				doneMovingElevator = elevator.autoTopScale();
//			}
//			else
//			{
//				autoStage = Constants.AutoStage.kDrive3ToLine;
//				System.out.println("Entering: " + autoStage);
//				drivetrain.resetEncoder();
//				doneMovingElevator = false;
//				doneDriving = false;
//			}
		}
		else if(autoStage == Constants.AutoStage.kDrive3ToLine)
		{
			if(drivetrain.driveToColor(AMSColorSensor.Constants.Color.kWhite, 0.2, 0 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
				gripper.setAutoEjecting(true);
				System.out.println("Time: " + t.get());
				doneDriving = false;
				t.stop();
				t.reset();
				t.start();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(drivetrain.driveSeconds(-.2, 1.0, 0))
			{
				autoStage = Constants.AutoStage.kDone;
			}
		}
	}

	public void scaleOnOppositeSide()
	{
		if(autoStage == Constants.AutoStage.kDrive1)
		{
			if(drivetrain.driveDistance(185, 0.75, 0 * angleSign, 48))
			{
				autoStage = Constants.AutoStage.kSpin1;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin1)
		{
			if(drivetrain.spinToBearing(-90 * angleSign, 0.35))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine1)
		{
			if(drivetrain.driveToColor(color, 0.25, -90 * angleSign))
			{
				autoStage = Constants.AutoStage.kDrive2Distance1;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance1)
		{
			if(drivetrain.driveDistance(55, 0.4, -90 * angleSign, 48))
			{
				autoStage = Constants.AutoStage.kDrive2Distance2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance2)
		{
			if(drivetrain.driveDistance(40, 0.25, -90 * angleSign, 48))
			{
				autoStage = Constants.AutoStage.kDrive2ToLine2;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2ToLine2)
		{
			if(!doneDriving && !doneMovingGripper)
			{
				doneDriving = drivetrain.driveDistance(225, 0.25, -90 * angleSign, 48);
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneMovingGripper)
			{
				doneMovingGripper = gripper.autoMiddle();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.driveDistance(225, 0.25, -90 * angleSign, 48);
			}
			else
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingGripper = false;
			}
		}
		else if(autoStage == Constants.AutoStage.kDrive2Distance3)
		{
			if(drivetrain.driveDistance(17, 0.25, -90 * angleSign, 48))
			{
				autoStage = Constants.AutoStage.kSpin2;
				System.out.println("Entering: " + autoStage);
			}
		}
		else if(autoStage == Constants.AutoStage.kSpin2)
		{
			if(!doneDriving && !doneMovingElevator)
			{
				doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.35);
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneMovingElevator)
			{
				doneMovingElevator = elevator.autoTopScale();
			}
			else if(!doneDriving)
			{
				doneDriving = drivetrain.spinToBearing(0 * angleSign, 0.35);
			}
			else
			{
				autoStage = Constants.AutoStage.kDone;
				System.out.println("Entering: " + autoStage);
				drivetrain.resetEncoder();
				doneDriving = false;
				doneMovingElevator = false;
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
