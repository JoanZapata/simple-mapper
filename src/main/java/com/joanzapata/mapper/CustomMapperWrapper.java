package com.joanzapata.mapper;

import java.lang.reflect.Method;

class CustomMapperWrapper<S, D> {

    private final CustomMapper<S, D> customMapper;

    CustomMapperWrapper(CustomMapper<S, D> customMapper) {
        this.customMapper = customMapper;
    }

    public D apply(Object source, Class destination) {
        for (Method method : customMapper.getClass().getMethods()) {
            if ("map".equals(method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> sourceClass = parameterTypes[0];
                Class<?> destinationClass = method.getReturnType();
                if (sourceClass.isAssignableFrom(source.getClass()) &&
                        destinationClass.isAssignableFrom(destination)) {
                    return applySafe((S) source);
                }
            }
        }
        return null;
    }

    private D applySafe(S source) {
        return customMapper.map(source);
    }
}
