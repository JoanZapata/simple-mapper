package com.joanzapata.mapper;

class HookWrapper<S, D> {

    private Hook<S, D> hook;

    private Class<S> sourceClass;

    private Class<D> destinationClass;

    HookWrapper(Hook<S, D> hook, Class<S> source, Class<D> destination) {
        this.hook = hook;
        this.sourceClass = source;
        this.destinationClass = destination;
    }

    public void apply(Object source, Object destination) {
        if (sourceClass.isAssignableFrom(source.getClass()) &&
                destinationClass.isAssignableFrom(destination.getClass())) {
            applySafe((S) source, (D) destination);
        }
    }

    private void applySafe(S source, D destination) {
        hook.extraMapping(source, destination);
    }
}
