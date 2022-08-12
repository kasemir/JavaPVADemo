package demo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.client.PVAChannel;
import org.epics.pva.client.PVAClient;
import org.epics.pva.data.PVANumber;
import org.epics.pva.data.PVAStructure;

/** Another 'pv get' */
public class Step2Get 
{
    public static void main(String[] args) throws Exception
    {
        // Disable "INFO" messages
        Logger.getLogger("").setLevel(Level.WARNING);

        // Try-with-resources handles the close() calls for us
        try
        (   PVAClient client = new PVAClient();
            PVAChannel channel = client.getChannel("ramp");
        )
        {   // Adding timeout to the connection and 'get'
            channel.connect().get(2, TimeUnit.SECONDS);
            PVAStructure data = channel.read("").get(2, TimeUnit.SECONDS);

            // Decode pieces of the received data
            PVANumber value = data.get("value");
            PVANumber secs = data.locate("timeStamp.secondsPastEpoch");
            PVANumber nano = data.locate("timeStamp.nanoseconds");
            Instant time = Instant.ofEpochSecond(secs.getNumber().longValue(), nano.getNumber().longValue());
            LocalDateTime local = LocalDateTime.ofInstant(time, ZoneId.systemDefault());

            System.out.println(local + ": " + channel.getName() + " = " + value.getNumber());
        }
    }
}
