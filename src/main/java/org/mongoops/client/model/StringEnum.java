package org.mongoops.client.model;

abstract class StringEnum {

    private String type;

    protected StringEnum(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
