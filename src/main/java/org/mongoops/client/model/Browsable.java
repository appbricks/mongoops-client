package org.mongoops.client.model;

import java.util.List;

public interface Browsable<T> {

    List<Link> getLinks();

    List<T> getResults();

    int getTotalCount();
}
