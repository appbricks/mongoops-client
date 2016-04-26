package org.mongoops.client.exeption;

public class MongoOpsTimeoutException
    extends MongoOpsException {

    public MongoOpsTimeoutException(String mssgFormat, Object... args) {
        super(mssgFormat, args);
    }

    public MongoOpsTimeoutException(Throwable cause, String mssgFormat, Object... args) {
        super(cause, mssgFormat, args);
    }
}
