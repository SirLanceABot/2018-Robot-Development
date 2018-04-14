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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot
{
	private Autonomous autonomous = Autonomous.getInstance();

	private Drivetrain drivetrain = Drivetrain.getInstance();
	private Gripper gripper = Gripper.getInstance();
	private Elevator elevator = Elevator.getInstance();

	private double previousNanoTime;
	private double currentNanoTime;

	public Robot()
	{
		previousNanoTime = System.nanoTime();
		currentNanoTime = System.nanoTime();
	}

	@Override
	public void robotInit()
	{
		drivetrain.resetNavX();

		drivetrain.calibrateNavX();
		drivetrain.calibrateColorSensor();

		System.out.println("Starting robot!");

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
		//printSensorValues();
	}

	@Override
	public void teleopInit()
	{
		System.out.println("Entering teleop");
		gripper.setTeleopLimits();
		autonomous.turnLightRingsOff();
		drivetrain.omniWheelUp();
	}

	@Override
	public void teleopPeriodic()
	{
		drivetrain.teleop();
		elevator.teleop();
		gripper.teleop();
	}

	@Override
	public void autonomousInit()
	{
		//camera.setExposureManual(0);
		System.out.println("Entering autonomous");
		gripper.setAutoLimits();
		//elevator.retractClimber();
		autonomous.init();
	}

	@Override
	public void autonomousPeriodic()
	{
		printSensorValues();
		//drivetrain.run();
		//elevator.run();
		//gripper.run();

		autonomous.periodic();
	}

	public void printSensorValues()
	{
		System.out.print("Encoder: " + Drivetrain.getInstance().getEncInches() + 
				"\tNavX: " + Drivetrain.getInstance().getNavXYaw() + 
				"\tColors: "); Drivetrain.getInstance().printColors();
				System.out.print("\tElevator pot: " + Elevator.getInstance().getStringPot() + 
						"\tPivot pot: " + Gripper.getInstance().getPivotPot() + 
						"\tIntake encoder left: " + Gripper.getInstance().getLeftIntakeEncoder() + 
						"\tIntake encoder right: " + Gripper.getInstance().getRightIntakeEncoder() + '\n');
	}

	public double getDeltaTime()
	{
		currentNanoTime = System.nanoTime();

		double dt = currentNanoTime - previousNanoTime;
		previousNanoTime = currentNanoTime;

		return dt;
	}
}
