package demo;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.data.PVADouble;
import org.epics.pva.data.PVAStructure;
import org.epics.pva.data.nt.PVATimeStamp;
import org.epics.pva.server.PVAServer;
import org.epics.pva.server.ServerPV;

/** PVA server with write access */
public class Step6Server
{
    public static void main(String[] args) throws Exception
    {
        Logger.getLogger("").setLevel(Level.WARNING);

        try
        (   PVAServer server = new PVAServer()        )
        {

            PVADouble value = new PVADouble("value", 3.14);
            PVATimeStamp time = new PVATimeStamp("timeStamp");
            PVAStructure data = new PVAStructure("data", "epics:nt/NTScalar:1.0",
                                                 value,
                                                 time);

            ServerPV pv = server.createPV("demo", data);
            System.out.println("Server for PV '" + pv.getName() + "' is running, stop with Ctrl-C");

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
