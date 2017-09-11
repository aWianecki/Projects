package clientSide;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.Properties;
//import java.util.Arrays;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.obex.*;

import com.intel.bluetooth.BlueCoveImpl;

import clocks.ClockRunner;
import packet.HeaderName;
import packet.HeaderType;

/** An OBEX client which can search for services and send data back and forth between this client and the server. <br><br>
 *  This client was developed for a senior design project that uses bluetooth to establish a constant dual authentication.
 *  
 *  <br><br>Latest Update: 4/21/2017
 * @author Izzy Beraja
 * @author Anthony Wianecki
 * @author Honorary Mention: Jon Okawa**/

public class ObexClient 
{
	private final String SERVICE_NAME = "DualAuthentication";
	private final int CLOCK_PERIOD = 1000;
	private final int CLOCK_TIMEOUT = 5000;
	private ClientSession clientSession;
	private ClockRunner clock;
	private String[] args;
	//private static final String OUTPUT_FILE = "config.properties";
    
	public ObexClient(String[] args)
	{
		this.args = args;
		BlueCoveImpl.setConfigProperty("bluecove.obex.timeout", "5000");
		//Create clock
		clock = new ClockRunner(CLOCK_PERIOD, CLOCK_TIMEOUT, null);
	}
	
	/** Starts the client
		@param args <br> <b>args[0]</b> is the RemoteDeviceSearch. When true, it will search all nearby devices for the service.
		If args[0] is false, only devices that are labeled as preknown (paired) are searched. <br>
	 * <b>args[1]</b> is for a MAC address. If not null, it will only search for services at the MAC address specified. 
	 * Set to null otherwise. 
	 * @return <b>True</b> if connection was established, <b>False</b> if connection was not established**/
    public boolean startConnection()
    {
		String serverURL = getServices(args);
        
        if(serverURL == null)
        {
        	//System.out.println("Service Not Found - Exiting...");
        	return false;
        }

        //System.out.println("Connecting to " + serverURL);
        
        try
        {
	        clientSession = (ClientSession) Connector.open(serverURL, 0, true);
	        HeaderSet hsConnectReply = clientSession.connect(null);
	        if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) 
	        {
	            System.out.println("Failed to connect - Exiting...");
	            return false;
	        }
	        //Setup the server
	        setupServerClock();
	        return true;
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
        return false;
    }
    
    /** Closes a ClientSession that is open
     * @param clientSession - connection you would like to close**/
    public boolean closeConnection()
    {
    	try
    	{
	    	clientSession.disconnect(null);
	    	clientSession.close();
    	}
    	catch(Exception e)
    	{
    		System.out.println("Disconnected improperly");
    		return false;
    	}
    	return true;
    }
    
    /** Gets services based on the arguments provided. First argument is ignored.
     * Second argument is the MAC address of the device to search services on.
     * If second argument is null it simply returns all devices hosting SERVICE_NAME
     *      **/
    private String getServices(String[] args)
    {
    	//ArrayList<String> serverUrlList = new ArrayList<String>();
    	
        try 
        {
        	//String[] searchArgs = null;
			ServicesSearch.main(args);
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
        catch (InterruptedException e) 
        {
			e.printStackTrace();
		}
        
        if (ServicesSearch.serviceFound.size() == 0) 
        {
            return null;
        }
        // Select the first service found with name SERVICE_NAME
        for(int i = 0; i < ServicesSearch.serviceFound.size(); i++)
        {
        	if(ServicesSearch.serviceFound.elementAt(i)[0].equals(SERVICE_NAME))
        	{
        		return (String)ServicesSearch.serviceFound.elementAt(i)[1];
        	}
        }
        System.out.println("Service wasn't found");
        return null;
    }
    
    /** Setup for the server clock. This is for first time setup and resyncing
     * @throws BluetoothStateException When bluetooth state is either unavailable or broken 
     * @throws IOException When an attempt to write fails
     * @throws UnsupportedEncodingException Requires the ability to encode bytes using the iso-8859-1 encoding**/
    private void setupServerClock() throws BluetoothStateException, InterruptedIOException, IOException, UnsupportedEncodingException
    {        
        //Create PUT Operation
		//Prepare seed for sending
		HeaderSet head = clientSession.createHeaderSet();
		head.setHeader(HeaderSet.NAME, HeaderName.SDV.toString());
		head.setHeader(HeaderSet.TYPE, HeaderType.REQ.toString());
		head.setHeader(HeaderSet.DESCRIPTION, LocalDevice.getLocalDevice().getBluetoothAddress());
		
        Operation op = clientSession.put(head);
        
        //interpret response codes
        /*int response = op.getResponseCode();
        
        System.out.println(response == ResponseCodes.OBEX_HTTP_OK);
        
        switch(response)
        {
        	case ResponseCodes.OBEX_HTTP_BAD_REQUEST:
        		System.out.println("Error: Bad Request - Data not sent");
        		return;
        	case ResponseCodes.OBEX_HTTP_UNAVAILABLE:
        		System.out.println("Error: Server operation failed");
        		return;
        	case ResponseCodes.OBEX_HTTP_NO_CONTENT:
        		System.out.println("Error: Data is empty");
        		return;
        	case ResponseCodes.OBEX_HTTP_NOT_IMPLEMENTED:
        		System.out.println("Error: Not Implemented");
        		return;
        }*/
        
		byte[] data = null;
		data = clock.getSeed().getBytes("iso-8859-1");

        // Send clock seed to server
        OutputStream os = op.openOutputStream();
        os.write(data);
        
        //Finish Operation
        os.close();
        op.close();
        
        //Start the clock
        clock.run();
    }
    
    /* Asks the server for its key value. It will stall here if the server cannot be reached
     * after a certain amount of time set up by bluecove
     */
    private byte[] getKey() throws InterruptedIOException, IOException
    {    
    	Operation op = clientSession.get(null);
    	
    	//interpret response codes
    	/*int response = op.getResponseCode();
        switch(response)
        {
        	case ResponseCodes.OBEX_HTTP_BAD_REQUEST:
        		System.out.println("Bad Request");
        		return null;
        	case ResponseCodes.OBEX_HTTP_UNAVAILABLE:
        		System.out.println("Server operation failed");
        		return null;
        }*/

        InputStream is = op.openInputStream();
        
        int len = is.read();
        //System.out.println(len);
        
        if(len < 0)
        	return null;
        
        byte[] buf = new byte[len];
        is.read(buf);
        	//System.out.println("read is -1");
        
        //System.out.println(Arrays.toString(buf));
        
        //Finish Operation
        is.close();
		op.close();

        return buf;
    }
    
    /** This function checks to see if both client and server have the same key values
     	@return <b>True</b> if both keys are the same <br><b>False</b> if keys are not the same*/
	public boolean isValid() throws IOException
	{
		return clock.keyFound(getKey());
	}
	
	/** <b><i>clockReset</b></i>
	 * <blockquote><code>public void clockReset()</code></blockquote>
	 * This function will reset the clock of both the client and server to the same value. This is
	 * useful if you want to resynchronize the values of the client and server after they get back
	 * into range
	 * */
	public void clockReset() throws BluetoothStateException, IOException, UnsupportedEncodingException
	{
		setupServerClock();
	}
	
	public boolean isConnected()
	{
		return clientSession != null;
	}
}