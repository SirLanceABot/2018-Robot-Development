package org.usfirst.frc.team4237.robot.util;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger
{
	private PrintWriter printWriter;

	private static Logger instance = new Logger();
	public static Logger getInstance()
	{
		return instance;
	}

	private Logger()
	{
		open();
	}

	public void logThrowable(Throwable throwable)
	{
		throwable.printStackTrace(printWriter);
	}

	public void log(String message)
	{
		printWriter.println(message);
	}

	public void open()
	{
		try
		{
			printWriter = new PrintWriter(new FileWriter("/home/lvuser/crashtracker.txt"), true);
		}
		catch(Exception e)
		{
			System.out.println("[Logger] A catastrophic error has occurred in the Logger subsystem!!");
		}
	}
	
	public void close()
	{
		printWriter.close();
	}
}
