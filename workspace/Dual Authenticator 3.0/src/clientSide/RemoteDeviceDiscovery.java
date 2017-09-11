package clientSide;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import javax.bluetooth.*;

/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery 
{

    public static final Vector<RemoteDevice> devicesDiscovered = new Vector<RemoteDevice>();
    private static String deviceMacAddr;

    public static void main(String[] args) throws IOException, InterruptedException 
    {
    	boolean remoteDeviceSearch = true;
    	if(args != null && args.length > 0)
    	{
    		remoteDeviceSearch = Boolean.parseBoolean(args[0]);
    		if(args.length > 1)
    			deviceMacAddr = args[1];
    	}    	
    	if(remoteDeviceSearch)
    		System.out.println("Searching all nearby devices");
    	
        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() 
        {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) 
            {
                //System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
            	if(deviceMacAddr == null)
            		devicesDiscovered.addElement(btDevice);
            	else if(deviceMacAddr != null && btDevice.getBluetoothAddress().equals(deviceMacAddr))
            		devicesDiscovered.addElement(btDevice);

                /*try 
                {
                    System.out.println("     name " + btDevice.getFriendlyName(false));
                } 
                catch (IOException cantGetDeviceName) 
                {
                }*/
            }

            public void inquiryCompleted(int discType) 
            {
                //System.out.println("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent)
                {
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) { }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) { }
        };

        synchronized(inquiryCompletedEvent) 
        {
        	//If we only want to check paired devices
        	if(!remoteDeviceSearch)
        	{
            	//If we have a specified device to connect to

        		RemoteDevice[] rd = LocalDevice.getLocalDevice().getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
        		if(rd == null)
        		{
        			System.out.println("No paired devices found");
        		}
        		else if(deviceMacAddr != null)
            		for(RemoteDevice device: rd)
            		{
            			//System.out.println(device.getBluetoothAddress() + " " + deviceMacAddr);
            			if(device.getBluetoothAddress().equals(deviceMacAddr))
            			{
            				devicesDiscovered.add(device);
            			}
            		}
            	else
            		devicesDiscovered.addAll(Arrays.asList(rd));
        	}
        	//Otherwise check all nearby devices
        	else
        	{
	        	boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
	        	
	            if (started) 
	            {
	                //System.out.println("wait for device inquiry to complete...");
	                inquiryCompletedEvent.wait();
	                //System.out.println(devicesDiscovered.size() +  " device(s) found");
	            }
        	}
        }
    }

}
