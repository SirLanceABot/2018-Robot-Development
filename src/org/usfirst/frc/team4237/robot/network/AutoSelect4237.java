package org.usfirst.frc.team4237.robot.network;

import com.esotericsoftware.jsonbeans.Json;

import edu.wpi.first.wpilibj.Timer;

public class AutoSelect4237 extends Thread
{
	AutoSelect4237Data data = new AutoSelect4237Data();
	AutoSelect4237Receiver receiver = AutoSelect4237Receiver.getInstance();
	Json json = new Json();
	
	private static AutoSelect4237 instance = new AutoSelect4237();
	public static AutoSelect4237 getInstance()
	{
		return instance;
	}
	
	private AutoSelect4237()
	{
		
	}
	
	@Override
	public void run()
	{
		while (!this.interrupted())
		{
			setData(receiver.getRawData());
			Timer.delay(0.005);
		}
	}
	
	private synchronized void setData(String data)
	{
		this.data = json.fromJson(AutoSelect4237Data.class, data);
	}
	
	public synchronized AutoSelect4237Data getData()
	{
		return this.data;
	}
}
