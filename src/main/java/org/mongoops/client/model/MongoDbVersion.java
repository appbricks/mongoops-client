package org.mongoops.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Comparator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MongoDbVersion
    implements Comparable<MongoDbVersion> {

    private List<MongoDbBuild> builds;
    private String name;

    public List<MongoDbBuild> getBuilds() {
        return builds;
    }

    public void setBuilds(List<MongoDbBuild> builds) {
        this.builds = builds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof  MongoDbVersion) {

            String myVersion = this.name;
            String otherVersion = ((MongoDbVersion) o).name;

            if (myVersion != null && otherVersion != null) {
                return myVersion.equals(otherVersion);
            }
            return myVersion == otherVersion;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(MongoDbVersion o) {

        // Order mongo db version objects in descending order

        String[] mySemver = this.name.split("\\.");
        assert (mySemver.length >= 3);

        String[] otherSemver = ((MongoDbVersion) o).name.split("\\.");
        assert (otherSemver.length >= 3);

        for (int i = 0; i < 3; i++) {

            int myVersion = Integer.parseInt(mySemver[i]);
            int otherVersion = Integer.parseInt(otherSemver[i]);

            if (myVersion < otherVersion) {
                return 1;
            }
            if (myVersion > otherVersion) {
                return -1;
            }
        }

        return 0;
    }
}
