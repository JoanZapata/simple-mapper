package com.joanzapata.mapper;

/**
 * Define a CustomMapper when you want
 * to map parts of your model by yourself,
 * or using another mapper.
 * @param <S> Source type
 * @param <D> Destination type
 */
public interface CustomMapper<S, D> {

    /**
     * Map the given source into the destination.
     * @param source The source object.
     * @return The destination object you've created using the source object.
     */
    D map(S source);

}
