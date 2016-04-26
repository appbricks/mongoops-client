package org.mongoops.client.exeption;

public class MongoOpsAzNotFound
    extends MongoOpsException {

    public MongoOpsAzNotFound(String mssgFormat, Object... args) {
        super(mssgFormat, args);
    }

    public MongoOpsAzNotFound(Throwable cause, String mssgFormat, Object... args) {
        super(cause, mssgFormat, args);
    }
}
