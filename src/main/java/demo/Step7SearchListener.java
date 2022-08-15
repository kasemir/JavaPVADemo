package demo;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pva.server.PVAServer;
import org.epics.pva.server.SearchHandler;

/** PVA server that logs searches */
public class Step7SearchListener
{
    public static void main(String[] args) throws Exception
    {
        // Log more detail to see network packages
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(logger.getLevel());

        // Start server without any PVs but with logging search handler
        CountDownLatch done = new CountDownLatch(1);
        SearchHandler search_handler = (seq, cid, name, addr) ->
        {
            System.out.println("Client at " + addr + " searches for '" + name + "', client ID " + cid + ", attempt " + seq);
            if (name.toLowerCase().equals("quit"))
                done.countDown();
            return true;
        };
        PVAServer server = new PVAServer(search_handler);
        System.out.println("Perform some searches, then try the PV 'quit'");
        done.await();
        server.close();
    }
}
