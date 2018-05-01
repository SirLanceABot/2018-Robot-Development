# 2018 FRC Robot Code
Team 4237's FRC robot code for the 2018 robot, "Milkman". Milkman's code is written in Java and based off of the WPILib control system.

## Packages
### org.usfirst.team4237.robot.components

This package contains the class files for each primary component used on the robot.
Each component's class inherits the Component interface to ensure that each class includes a method for printing information for testing.

It includes:
* Drivetrain
* Elevator
* Gripper
* Light Ring
* Climber (unused)

### org.usfirst.team4237.robot.control

This package contains classes for use in controlling the robot.
The most important class in this package is the Xbox class,
which inherits from WPILib's Joystick class and makes it slightly more usable
when writing code by adding constants for each button.
This package also contains classes for both the driver's controller
and the operator's controller, limited to one instance of each.

### org.usfirst.team4237.robot.network
This package contains classes for use in communicating between the robot,
the driver station's autonomous selector, and a raspberry pi.

### org.usfirst.team4237.robot.sensors
This package contains classes for sensors used on the robot,
as well as some wrapper classes that we wrote for sensors just in case.

### org.usfirst.team4237.robot.util
This package contains utility classes for use with the color sensor and debugging.

### org.usfirst.team4237.robot.vision
This package contains classes used for identifying reflective tape targets on the field
with computer vision on a raspberry pi for the purpose of aligning the robot to accomplish a goal.
This year it was unused in favor of faster preprogrammed autonomous routines.

### org.usfirst.team4237.robot
This package contains the main classes used to control the robot in both of its main modes, teleoperated and autonomous.

## Autonomous
The Autonomous class controls the robot during the first 15 seconds of each match.

### init()
The __init()__ method initializes all necessary variables
and attempts to receive data from AutoSelect4237.

### setAutoMode()
The __setAutoMode()__ method decides which autonomous routine the robot should run depending
on the selected position, the field colors, and the current alliance color.

### periodic()
The __periodic()__ method loops continously during the 15 second autonomous period. At the start,
it checks to see if the robot has received data from the field containing the colors of each field element.
If it has not, it will repeatedly check until it receives data.
