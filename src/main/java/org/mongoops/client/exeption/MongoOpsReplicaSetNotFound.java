package org.mongoops.client.exeption;

public class MongoOpsReplicaSetNotFound
    extends MongoOpsException {

    public MongoOpsReplicaSetNotFound(String mssgFormat, Object... args) {
        super(mssgFormat, args);
    }

    public MongoOpsReplicaSetNotFound(Throwable cause, String mssgFormat, Object... args) {
        super(cause, mssgFormat, args);
    }
}
