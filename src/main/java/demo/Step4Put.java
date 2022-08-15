package demo;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.client.PVAChannel;
import org.epics.pva.client.PVAClient;
import org.epics.pva.data.PVAString;

/** Basic 'pv put' */
public class Step4Put 
{
    public static void main(String[] args) throws Exception
    {
        Logger.getLogger("").setLevel(Level.WARNING);

        try
        (   PVAClient client = new PVAClient();
            PVAChannel channel = client.getChannel("ramp.CALC");
        )
        {
            channel.connect().get(2, TimeUnit.SECONDS);

            // Write the "value", which happens to be a string, as a plain string
            // Could similarly write scalar int or double as just 42 or 3.14
            channel.write("value", "A<100 ? A+2 : 0").get();

            TimeUnit.SECONDS.sleep(5);

            // Alternatively, assuming a more complex structure, create that as PVA... data
            // PVAStructure data = new PVAStructure("data", "complex_type",
            //                                      new PVADouble("value", 3.14),
            //                                      new PVAInt("counter", 42),
            //                                      new PVAString("owner", "Fred"));
            PVAString data = new PVAString("value", "A<10 ? A+1 : 0");
            channel.write("value", data).get();
        }
    }
}
