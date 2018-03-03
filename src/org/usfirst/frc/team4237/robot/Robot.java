package org.usfirst.frc.team4237.robot;

import org.usfirst.frc.team4237.robot.components.Drivetrain;
import org.usfirst.frc.team4237.robot.components.Elevator;
import org.usfirst.frc.team4237.robot.components.Gripper;
import org.usfirst.frc.team4237.robot.control.Xbox;
import org.usfirst.frc.team4237.robot.network.AutoSelect4237;
import org.usfirst.frc.team4237.robot.network.RaspberryPiReceiver;
import org.usfirst.frc.team4237.robot.vision.Vision;
import org.usfirst.frc.team4237.robot.sensors.Sonar;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot
{
	private UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
	private Autonomous autonomous = Autonomous.getInstance();
	
	public Robot()
	{
		
	}
	
	@Override
	public void robotInit()
	{
		Drivetrain.getInstance().calibrateNavX();
		Drivetrain.getInstance().calibrateColorSensor();
		System.out.println("Starting robot!");
		Thread drivetrain = new Thread(Drivetrain.getInstance());
		drivetrain.start();
		System.out.println("Drivetrain thread started!");
		
		Elevator.getInstance().start();
		System.out.println("Elevator thread started!");
		
		Gripper.getInstance().start();
		System.out.println("Gripper thread started!");
				
		//Vision.getInstance();
	}
	
	@Override
	public void disabledInit()
	{
		System.out.println("Robot is disabled");
	}
	
	@Override
	public void disabledPeriodic()
	{
		//autonomous.turnLightRingsOff();
		Drivetrain.getInstance().printTestInfo();
	}
	
	@Override
	public void teleopInit()
	{
		//camera.setExposureManual(13);
		System.out.println("Entering teleop");
		Gripper.getInstance().setTeleopLimits();
		//Drivetrain.getInstance().raiseServo();
		
		//turn light rings off
	}
	
	@Override
	public void teleopPeriodic()
	{
		
		//autonomous.turnLightRingsOn();
	}
	
	@Override
	public void autonomousInit()
	{
		//camera.setExposureManual(0);
		System.out.println("Entering autonomous");
		Gripper.getInstance().setAutoLimits();
		//Drivetrain.getInstance().lowerServo();
		autonomous.init();
	}
	
	@Override
	public void autonomousPeriodic()
	{
		
		autonomous.periodic();
	}
}
