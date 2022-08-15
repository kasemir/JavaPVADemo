package demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.data.PVADouble;
import org.epics.pva.data.PVAStructure;
import org.epics.pva.server.PVAServer;
import org.epics.pva.server.ServerPV;
import org.epics.pva.server.WriteEventHandler;

/** PVA server with write access
 * 
 *  Test with "pvmonitor demo" and "pvput demo 42"
 */
public class Step6Server
{
    public static void main(String[] args) throws Exception
    {
        // Run on non-default but known TCP port?
        // System.setProperty("EPICS_PVA_SERVER_PORT", "5085");

        // Log more detail to see network packages
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(logger.getLevel());

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


/*  For TCP-only client demo:
 * 
 *  1) Run this server
 *     Take note of the port number listed in the message.
 *     If this is the only PVA server, it'll be 5075:
 * 
 *     CONFIG: Listening on TCP .........:5075
 * 
 *     Otherwise, it will be some random free port.
 *     Could set EPICS_PVA_SERVER_PORT=5085 to place
 *     server on known non-default port...
 *     
 *  2) Run PVA client like this, using port name obtained above:
 * 
 *     EPICS_PVA_NAME_SERVERS=127.0.0.1:5075   \
 *     EPICS_PVA_AUTO_ADDR_LIST=no             \
 *     pvxget demo
 * 
 *     Must use "pvxget" from PVXS instead of the "pvget" from older C++
 *     implementation which lacks NAME_SERVERS support!
 */

