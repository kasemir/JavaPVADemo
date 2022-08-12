package demo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.client.MonitorListener;
import org.epics.pva.client.PVAChannel;
import org.epics.pva.client.PVAClient;
import org.epics.pva.data.PVANumber;
import org.epics.pva.data.PVAStructure;

/** 'pv monitor' */
public class Step3Monitor
{
    // Helper for decoding time stamp
    static String formatTime(PVAStructure data)
    {
        try
        {
            PVANumber secs = data.locate("timeStamp.secondsPastEpoch");
            PVANumber nano = data.locate("timeStamp.nanoseconds");
            Instant time = Instant.ofEpochSecond(secs.getNumber().longValue(), nano.getNumber().longValue());
            LocalDateTime local = LocalDateTime.ofInstant(time, ZoneId.systemDefault());
            return String.format("%04d-%02d-%02d %02d:%02d:%02d.%09d",
                                 local.getYear(),
                                 local.getMonthValue(),
                                 local.getDayOfMonth(),
                                 local.getHour(),
                                 local.getMinute(),
                                 local.getSecond(),
                                 local.getNano());
        }
        catch (Exception ex)
        {
            return "??";
        }
    }

    public static void main(String[] args) throws Exception
    {
        Logger.getLogger("").setLevel(Level.WARNING);

        try
        (   PVAClient client = new PVAClient();
            PVAChannel channel = client.getChannel("ramp");
        )
        {
            channel.connect().get(2, TimeUnit.SECONDS);
            
            // Listener that will receive all updates
            MonitorListener listener = (pv, changes, overruns, data) ->
            {
                PVANumber value = data.get("value");
                String time = formatTime(data);
                System.out.println(time + ": " + pv.getName() + " = " + value.getNumber());
            };
            AutoCloseable subscription = channel.subscribe("", listener);
            TimeUnit.SECONDS.sleep(5);
            subscription.close();
        }
    }
}
