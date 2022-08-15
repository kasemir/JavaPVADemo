package demo;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.data.PVADouble;
import org.epics.pva.data.PVAString;
import org.epics.pva.data.PVAStructure;
import org.epics.pva.data.nt.PVATimeStamp;
import org.epics.pva.server.PVAServer;
import org.epics.pva.server.ServerPV;

/** Basic PVA server */
public class Step5Server
{
    public static void main(String[] args) throws Exception
    {
        Logger.getLogger("").setLevel(Level.WARNING);

        try
        (   PVAServer server = new PVAServer() )
        {
            // Construct a data structure with value and time, claim it's an NTScalar, with some extra stuff
            PVADouble value = new PVADouble("value", 3.14);
            PVATimeStamp time = new PVATimeStamp("timeStamp");
            PVAStructure data = new PVAStructure("data", "epics:nt/NTScalar:1.0",
                                                 value,
                                                 time,
                                                 new PVAString("owner", "Fred"),
                                                 new PVAString("color", "pale enticing turquois"));

            // Create PV
            ServerPV pv = server.createPV("demo", data);
            System.out.println("Server for PV '" + pv.getName() + "' is running.");
            System.out.println();
            System.out.println("Check with `pvmonitor -M raw demo` what's changing.");
            System.out.println("Stop via Ctrl-C");

            // Keep updating the PV's data
            while (true)
            {
                value.set(value.get() + 1.0);
                time.set(Instant.now());
                pv.update(data);
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}
