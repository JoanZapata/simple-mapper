package com.joanzapata.mapper;

import java.lang.reflect.Method;

class HookWrapper<S, D> {

    private Hook<S, D> hook;

    HookWrapper(Hook<S, D> hook) {
        this.hook = hook;
    }

    public void apply(Object source, Object destination) {
        for (Method method : hook.getClass().getMethods()) {
            if ("extraMapping".equals(method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> sourceClass = parameterTypes[0];
                Class<?> destinationClass = parameterTypes[1];
                if (sourceClass.isAssignableFrom(source.getClass()) &&
                        destinationClass.isAssignableFrom(destination.getClass())) {
                    applySafe((S) source, (D) destination);
                }
                return;
            }
        }
    }

    private void applySafe(S source, D destination) {
        hook.extraMapping(source, destination);
    }
}
