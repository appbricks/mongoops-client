package org.mongoops.client.service;

import java.util.List;

/**
 * Exposes a local mongo agent registry
 */
public interface MongoAgentRegistry {

    /**
     * Return the availability zone of the
     * given hostname.
     *
     * @param hostname
     *      host whose availability zone is being queried
     * @return
     *      the availability zone of the host
     */
    String getAvailabilityZone(String hostname);

    /**
     * Return the type of service that can
     * be hosted by the server identified
     * by the given hostname.
     *
     * @param hostname
     *      host whose service type is being queried
     * @return
     *      type of service (i.e. small, medium, large, etc.)
     */
    String getServiceType(String hostname);

    /**
     * Get list of distinct availability zones
     * of all registered servers.
     *
     * @return
     *      list of all availability zones
     */
    List<String> getAvailabilityZones();

    /**
     * Get list of distinct service types
     * that can be offered by all registered
     * servers.
     *
     * @return
     *      list of all service types (i.e. small, medium, large, etc.)
     */
    List<String> getServiceTypes();
}
