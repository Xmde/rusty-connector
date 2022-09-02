package rustyconnector.generic.lib.generic.server;

import rustyconnector.RustyConnector;
import rustyconnector.generic.lib.database.Redis;
import rustyconnector.generic.lib.generic.whitelist.Whitelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Proxy {
    RustyConnector plugin = null;
    String privateKey = null;
    Map<String, Whitelist> registeredWhitelists = new HashMap<>();
    List<Family> registeredFamilies = new ArrayList<>();
    String proxyWhitelist = "";
    Redis redis = null;

    void init();

    /**
     * Send a request over Redis asking all servers to register themselves
     */
    void requestGlobalRegistration();

    /**
     * Registers a whitelist to thre proxy. Saving it for when it needs to be used.
     * @param name The name of the whitelist
     * @param whitelist The whitelist itself
     */
    void registerWhitelist(String name, Whitelist whitelist);

    /**
     * Get's a whitelist which is used for the whole proxy
     * @return The whitelist
     */
    Whitelist getProxyWhitelist();

    /**
     * Get's a whitelist from the registered whitelists stored on the proxy
     * @param name Name of the whitelist to get
     * @return The whitelist
     */
    Whitelist getWhitelist(String name);
}
