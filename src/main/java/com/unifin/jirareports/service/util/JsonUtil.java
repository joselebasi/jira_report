package com.unifin.jirareports.service.util;

@FunctionalInterface
public interface JsonUtil<T, U, V, R> {
    R apply(T t, U u, V v);
}
