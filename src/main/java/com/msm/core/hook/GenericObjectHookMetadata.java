package com.msm.core.hook;

public abstract class GenericObjectHookMetadata implements ObjectHookMetadata {

    @Override
    public String type() {
        return objectName() + ":" + phase();
    }

    @Override
    public String objectName() {
        return "GenericHookObject";
    }
}
