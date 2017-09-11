package clientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.*;

/**
 *
 * Minimal Services Search example.
 */
public class ServicesSearch 
{

    static final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);

    static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);

    public static final Vector<String[]> serviceFound = new Vector<String[]>();
    
    private static String macAddr;

    public static void main(String[] args) throws IOException, InterruptedException 
    {

        // First run RemoteDeviceDiscovery and use discoved device
        RemoteDeviceDiscovery.main(args);
        
        if(args != null && args.length > 2)
        	macAddr = args[1];

        serviceFound.clear();

        UUID serviceUUID = OBEX_OBJECT_PUSH;

        final Object serviceSearchCompletedEvent = new Object();

        DiscoveryListener listener = new DiscoveryListener() 
        {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) { }

            public void inquiryCompleted(int discType) { }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) 
            {
                for (int i = 0; i < servRecord.length; i++) 
                {
                    String url = servRecord[i].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);
                    if (url == null) 
                    {
                        continue;
                    }

                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    if (serviceName != null) 
                    {
                        //System.out.println("service " + serviceName.getValue() + " found " + url);
                        serviceFound.add(new String[]{(String)serviceName.getValue(), url});
                    } 
                    else 
                    {
                        //System.out.println("service found " + url);
                        serviceFound.add(new String[]{"",url});
                    }
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) 
            {
                //System.out.println("service search completed!");
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }

        };

        
        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };
        
        if(RemoteDeviceDiscovery.devicesDiscovered.size() == 0)
        {
        	//No devices found, do nothing
        	return;
        }
        else if(RemoteDeviceDiscovery.devicesDiscovered.size() == 1 && macAddr != null)
        {
        	synchronized(serviceSearchCompletedEvent)
        	{
        		LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, (RemoteDevice) RemoteDeviceDiscovery.devicesDiscovered.elementAt(0), listener);
        		serviceSearchCompletedEvent.wait();
        	}
        }
        else
        {
	        int count = 1;
	        for(RemoteDevice device: RemoteDeviceDiscovery.devicesDiscovered)
	        {
	        	System.out.println(count + ": " + device.getFriendlyName(false) + "\t\t(" + device + ")");
	        	count++;
	        }
	        
	        System.out.println("0: Search all devices");
	        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
	        System.out.println("Q: Quit");
	        System.out.print("Pick the device to connect to: ");
	        String response = bf.readLine();
	        if(response.equalsIgnoreCase("Q"))
	        {
	        	System.out.println("Quitting");
	        	return;
	        }
	        int index = 0;
	        try
	        {
	        	index = Integer.parseInt(response);
	        }
	        catch(NumberFormatException e)
	        {
	        	System.out.println("Invalid input - Quitting");
	        	return;
	        }
	        //bf.close();
	        if(index > 0 && index < count)
	        {
	        	synchronized(serviceSearchCompletedEvent)
	        	{
	        		LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, (RemoteDevice) RemoteDeviceDiscovery.devicesDiscovered.elementAt(index - 1), listener);
	        		System.out.println("Searching for services of " + RemoteDeviceDiscovery.devicesDiscovered.elementAt(index - 1).getFriendlyName(false));
	        		serviceSearchCompletedEvent.wait();
	        	}
	        }
	        else
	        {
		        for(Enumeration<RemoteDevice> en = RemoteDeviceDiscovery.devicesDiscovered.elements(); en.hasMoreElements(); ) {
		            RemoteDevice btDevice = (RemoteDevice)en.nextElement();
		            synchronized(serviceSearchCompletedEvent) 
		            {
		                //System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
		                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
		                serviceSearchCompletedEvent.wait();
		            }
		        }
	        }
	    }
    }

}
