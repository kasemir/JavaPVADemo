package demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.data.PVADouble;
import org.epics.pva.data.PVAStructure;
import org.epics.pva.server.PVAServer;
import org.epics.pva.server.ServerPV;
import org.epics.pva.server.WriteEventHandler;

/** PVA server with write access */
public class Step6Server
{
    public static void main(String[] args) throws Exception
    {
        Logger.getLogger("").setLevel(Level.WARNING);

        try
        (   PVAServer server = new PVAServer() )
        {
            PVADouble value = new PVADouble("value", 3.14);
            PVAStructure data = new PVAStructure("data", "epics:nt/NTScalar:1.0", value);

            // Create PV with handler for data written to the PV by clients
            CountDownLatch done = new CountDownLatch(1);
            WriteEventHandler handler = (channel, changes, written) ->
            {
                System.out.println("Somebody wrote to " + channel.getName() + ":");
                System.out.println(written);
                System.out.println("I'm quitting");
                done.countDown();
            };
            ServerPV pv = server.createPV("demo", data, handler);
            System.out.println("Server for PV '" + pv.getName() + "' is running, stop by writing something to the PV");
            while (! done.await(1, TimeUnit.SECONDS))
            {
                value.set(value.get() + 1.0);
                pv.update(data);
            }

            pv.close();
        }
    }
}
