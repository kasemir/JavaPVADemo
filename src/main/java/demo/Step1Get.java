package demo;

import org.epics.pva.client.PVAChannel;
import org.epics.pva.client.PVAClient;
import org.epics.pva.data.PVAStructure;

/** First 'pv get' */
public class Step1Get 
{
    public static void main(String[] args) throws Exception
    {
        PVAClient client = new PVAClient();
        PVAChannel channel = client.getChannel("ramp");
        channel.connect().get();
        PVAStructure data = channel.read("").get();
        System.out.println(data);
        channel.close();
        client.close();
    }
}
