package com.joanzapata.mapper;

import java.lang.reflect.Method;

class CustomMapperWrapper<S, D> {

    private final CustomMapper<S, D> customMapper;

    CustomMapperWrapper(CustomMapper<S, D> customMapper) {
        this.customMapper = customMapper;
    }

    public boolean isApplicable(Object source, Class destination) {
        final Method[] methods = customMapper.getClass().getMethods();

        for (Method method : methods) {
            if ("map".equals(method.getName())) {
                Class<?> parameterType = method.getParameterTypes()[0];
                Class<?> returnType = method.getReturnType();
                // Ignore Object because it's too large
                if (parameterType != Object.class && returnType != Object.class &&
                        // Parameter type of the user function will be assigned the source object
                        parameterType.isAssignableFrom(source.getClass()) &&
                        // The destination object will be assigned the object returned from user function
                        destination.isAssignableFrom(returnType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public D apply(S source, Class<D> destination) {
        if (isApplicable(source, destination)) {
            return applySafe((S) source);
        } else return null;
    }

    private D applySafe(S source) {
        return customMapper.map(source);
    }
}
