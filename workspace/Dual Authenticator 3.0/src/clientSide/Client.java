package clientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;

import userInterface.DisplayConnection;

public class Client 
{
	public static void main(String[] args) 
	{
		String[] arg = {"false", "BC14EF6339C9"};
		DisplayConnection display = new DisplayConnection();
		display.drawUI();
		while(true)
		{
			//Find server and connect to service
			ObexClient obexClient = new ObexClient(arg);
			display.setColor(display.COLOR_GREY);

			System.out.println("Starting connection...");
			if(!obexClient.startConnection())
			{
				System.out.println("Connection failed");
				continue;
			}
			System.out.println("Connection with server established!");
			
			BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));		
			String line = null;
			try
			{
				while(true)
				{
					try
					{
						System.out.println("Type \"Quit\" to exit. Any other key press checks validity");
						line = cin.readLine();
						if(line.equalsIgnoreCase("Quit"))
						{
							cin.close();
							obexClient.closeConnection();
							System.exit(0);
						}
						if(obexClient.isValid())
						{
							display.setColor(display.COLOR_GREEN);
							System.out.println("Valid");
						}
						else
						{
							display.setColor(display.COLOR_RED);
							System.out.println("Not Valid");
							if(!obexClient.isConnected())
							{
								cin.close();
								obexClient.closeConnection();
								System.exit(0);
							}
							else
							{
								System.out.println("Resetting clock...");
								obexClient.clockReset();
							}
						}
					}
					catch(InterruptedIOException e)
					{
						System.out.println("Not Valid - Timeout Occured");
						display.setColor(display.COLOR_RED);
						//obexClient.closeConnection();
						//System.exit(1);
						//e.printStackTrace();
					}
				}
			}

			catch(IOException e)
			{
				obexClient.closeConnection();
				System.out.println("Server connection failure, restart device");
				display.setColor(display.COLOR_RED);
				e.printStackTrace();
				//System.exit(1);
			}
		}
	}
}
