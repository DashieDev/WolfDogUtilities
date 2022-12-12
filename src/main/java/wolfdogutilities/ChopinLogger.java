package wolfdogutilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChopinLogger {
    private static Logger LOGGER = LogManager.getLogger("chopin");
    public static void l(String s) {
        LOGGER.info(s);
    }
    public static void p(String p) {
        System.out.println("[chopin] : " + p);
    }
}
