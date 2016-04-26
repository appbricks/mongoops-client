package org.mongoops.client.exeption;

public class MongoOpsDeploymentException
    extends MongoOpsException {

    public MongoOpsDeploymentException(String mssgFormat, Object... args) {
        super(mssgFormat, args);
    }

    public MongoOpsDeploymentException(Throwable cause, String mssgFormat, Object... args) {
        super(cause, mssgFormat, args);
    }
}
