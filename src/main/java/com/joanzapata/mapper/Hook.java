package com.joanzapata.mapper;

public interface Hook<S, D> {
    void extraMapping(S from, D to);
}
