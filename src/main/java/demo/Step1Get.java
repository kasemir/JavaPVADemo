package demo;

import org.epics.pva.client.PVAChannel;
import org.epics.pva.client.PVAClient;
import org.epics.pva.data.PVAStructure;

/** First 'pv get' */
public class Step1Get 
{
    public static void main(String[] args) throws Exception
    {
        // Create client
        PVAClient client = new PVAClient();
        
        // Create channel, await connection
        PVAChannel channel = client.getChannel("ramp");
        channel.connect().get();
        
        // Read, wait for result, print 
        PVAStructure data = channel.read("").get();
        System.out.println(data);

        // Close channel and client
        channel.close();
        client.close();
    }
}
