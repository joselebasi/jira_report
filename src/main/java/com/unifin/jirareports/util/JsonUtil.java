package com.unifin.jirareports.util;

@FunctionalInterface
public interface JsonUtil<T, U, V, R> {
    R apply(T t, U u, V v);
}
