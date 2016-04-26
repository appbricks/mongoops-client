package org.mongoops.client.exeption;

public class MongoOpsException
    extends Throwable {

    public MongoOpsException(String mssgFormat, Object... args) {
        super(String.format(mssgFormat, args));
    }

    public MongoOpsException(Throwable cause, String mssgFormat, Object... args) {
        super(String.format(mssgFormat, args, cause));
    }
}
