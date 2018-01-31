package org.usfirst.frc.team4237.robot.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Class for receiving data from the Raspberry Pi
 * REMEMBER: UDP has no return address 
 * @author Mark Washington, Ben Puzycki, Darryl Wrong
 */
public class RaspberryPiReceiver extends Thread	
{
	private static RaspberryPiReceiver instance;
	static //Static block to get around the constructor throwing IOException
	{
		try	
		{
			 instance = new RaspberryPiReceiver(); 
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Returns instance of RaspberryPiReceiver
	 * @return Instance of RaspberryPiReceiver
	 */
	public static RaspberryPiReceiver getInstance()
	{
		return instance;
	}
	
	private DatagramSocket rxsocket = new DatagramSocket(Constants.PORT);
	private DatagramPacket packet = null;
	private String data;
	
	private RaspberryPiReceiver() throws IOException
	{
		
	}
	
	/**
	 * Method that runs when thread is started
	 */
	public synchronized void run()
	{
		while (!this.interrupted())
		{
			try
			{
				byte[] buffer = new byte[Constants.PACKETSIZE];
				this.packet = new DatagramPacket(buffer, buffer.length);
				try
				{
					this.rxsocket.receive(packet);
					this.data = new String(packet.getData(), 0, packet.getLength());
					System.out.println(this.data);
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the most current data received by the pi
	 * @return
	 */
	public String getValue()
	{
		return this.data;
	}
	
	/**
	 * Constants class for constants related to RaspberryPiReceiver
	 * @author Mark
	 *
	 */
	public static class Constants
	{
		private static final int PACKETSIZE = 256;
		private static final int PORT = 5802;
	}
}
