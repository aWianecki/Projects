package serverSide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.util.Scanner;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.obex.*;

import clocks.ClockRunner;
import packet.HeaderName;
import packet.HeaderType;

/** This is a server for the Dual Authenticator using Bluetooth. This will establish a connection and wait
 * for new incoming connections to interpret. If these connections are not what the server is looking for,
 * it will return the adequate response codes in return.
 * @author Izzy Beraja
 * @author Anthony Wianecki
**/
public class ObexServer 
{
    private static final String serverUUID = new UUID(0x1105).toString();
    private static final String SERVER_NAME = "DualAuthentication";
	private static final int CLOCK_PERIOD = 1000;
	private static final int CLOCK_TIMEOUT = 0;
    private static String serverUrl;
    private static ClockRunner clock;
    private static String macAddr;    

    public static void main(String[] args)
    {
    	System.out.println("STARTING SERVER");
    	int attempts = 0;
    	while(attempts < 10)
    	{
	    	try
	    	{
		    	if(LocalDevice.getLocalDevice().getDiscoverable() == 0)
		    		LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
		
		    	serverUrl = "btgoep://localhost:" + serverUUID + ";name=" + SERVER_NAME;
		        SessionNotifier serverConnection = (SessionNotifier) Connector.open(serverUrl);
		        System.out.println("WAITING FOR CONNECTION");
		        while(true) 
		        {
		            RequestHandler handler = new RequestHandler();
		            serverConnection.acceptAndOpen(handler);
					attempts = 0;
		            System.out.println("CONNECTION RECEIVED\n");
		        }
	    	}
	    	catch (Exception e)
	    	{
	    		if(attempts == 0)
	    			System.out.println("Ran into an error. Starting over...");
	    		else
	    			System.out.println("Trying again (" + attempts + ")");
	    			//e.printStackTrace();
	    	}
	    	attempts++;
    	}
    	
    	System.out.println("Server Timeout Reached - Exiting...");
    }

    private static class RequestHandler extends ServerRequestHandler 
    {    	
    	public int onGet(Operation op)
    	{
    		System.out.println("KEY REQUEST RECEIVED AT " + clock.toString());			
    		try
    		{          
    			//Scanner in = new Scanner(System.in);
    			//in.nextLine();
            	//Output the data from clock
	    		OutputStream os = op.openOutputStream();
	    		byte[] key = clock.getKey();
	    		//System.out.println(Arrays.toString(key));
	    		int size = key.length;
	    		//System.out.println(size);
	    		os.write(size);
	    		os.write(key);
	    		os.close();
	    		System.out.println("KEY SENT\n");
	    		return ResponseCodes.OBEX_HTTP_OK;
    		}
    		catch (IOException e)
    		{
            	System.out.println("CONNECTION INTERRUPTED\n");
            	//e.printStackTrace();
    			return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
    		}
    	}
    	
        public int onPut(Operation op) 
        {
        	System.out.println("RECEIVING SEED");
            //Prevents future clients from connecting to this server
            
            try 
            {
                HeaderSet hs = op.getReceivedHeaders();
                HeaderName name = HeaderName.getFromString((String)hs.getHeader(HeaderSet.NAME));
                HeaderType type = HeaderType.getFromString((String)hs.getHeader(HeaderSet.TYPE));
                String thisMac = (String)hs.getHeader(HeaderSet.DESCRIPTION);

                if(clock != null && !thisMac.equals(macAddr))
            		return ResponseCodes.OBEX_HTTP_BAD_REQUEST;
                
                macAddr = thisMac;
                
                //If it is not a valid request
                //Should either be a Seed with a request or a Key with a request
                
                if(!(name.equals(HeaderName.SDV) && type.equals(HeaderType.REQ))) {
                	return ResponseCodes.OBEX_HTTP_BAD_REQUEST;
                }
                
                InputStream is = op.openInputStream();
                
                StringBuffer buf = new StringBuffer();
                
                int data;
                while ((data = is.read()) != -1) {
                    buf.append((char) data);
                }
                
                //TODO clock code to start with value from buf
                if(clock == null){
                	System.out.println("SETTING UP CLOCK\n");
                }
                else {
                	System.out.println("RESETTING CLOCK\n");
                }
                	clock = new ClockRunner(CLOCK_PERIOD, CLOCK_TIMEOUT, buf.toString());
 //             System.out.println(buf.toString());
                clock.run();
                op.close();
                return ResponseCodes.OBEX_HTTP_OK;
            } 
            catch (IOException e) 
            {
                //e.printStackTrace();
            	System.out.println("CONNECTION INTERRUPTED\n");
                return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
            }
        }
    }
}
