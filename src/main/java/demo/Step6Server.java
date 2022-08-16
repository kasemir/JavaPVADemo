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


/*  PVA Servers based on core-pva (Java) or PVXS (C++, also used by p4p python and its gateway)
 *  always allow name searches via their TCP port.
 * 
 *  To run PVA over TCP, the client needs to be configured to connect to that TCP port:
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
 * 
 *  With plain servers like IOCs this makes little sense because
 *  each client would have to set their EPICS_PVA_NAME_SERVERS to a list of
 *  all(!) IOCs. The normal broadcast/multicast mechanism via UDP is more
 *  practical.
 * 
 *  For a PVA gateway, on the other hand, a TCP-only connection is often
 *  preferred because it can more easily be opened via firewalls.
 */


 /* IPv6 Hints
  *
  * By default, this server will also serve via IPv6, and can be reached
  * by core-pva respectively PVXS clients as long as the somewhat cumbersome IPv6
  * addresses are properly configured.
  *
  * 1) During start of the server, note sections like these:
  *    FINE: Awaiting searches and sending beacons on UDP IPv6 address 0:0:0:0:0:0:0:0 (ANY LOCAL) port 5076, TTL 1
  *    FINE: Listening to UDP multicast IPv6 address ff02:0:0:0:0:0:42:1 (MULTICAST) port 5076, TTL 1, interface lo0
  *    CONFIG: Listening on TCP /[0:0:0:0:0:0:0:0]:5075
  *
  *   The multicast address ff02::42:1 is the IPv6 equivalent to the IPv4 broadcast,
  *   but note that the address is specific to an interface like "eth0" or "lo0".
  *
  *   The TCP listener will default to 5075 and support both IPv4 and IPv6
  *   when it lists an IPv6 address.
  *   As mentioned in the NAME_SERVERS example, the TCP port may deviate from 5075.
  *
  * 2) Start client for the multicast address:
  *  
  *    EPICS_PVA_AUTO_ADDR_LIST=no  \
  *    EPICS_PVA_ADDR_LIST='[ff02::42:1]@lo0'  \
  *    pvxmonitor demo
  *
  * 3) "lo" local interface may not support multicast, reach via localhost UDP
  *  
  *    EPICS_PVA_AUTO_ADDR_LIST=no  \
  *    EPICS_PVA_ADDR_LIST='[::1]:5076'  \
  *    pvxmonitor demo
  *
  * 3) Start client for the TCP port
  *
  *    EPICS_PVA_AUTO_ADDR_LIST=no  \
  *    EPICS_PVA_NAME_SERVERS='[::1]:5075' \
  *    pvxmonitor demo
  */

