package com.unifin.jirareports.util;

import java.util.function.Function;

public class CompositionFuction {

    public static void main(String[] args) {
        Function<Integer, Integer> times2 = e -> {
            System.out.println("times2 " + e);
            return e * 2;
        };

        Function<Integer, Integer> squered = e -> {
            System.out.println("squered " + e);
            return e * e;
        };

        Function<Integer, Integer> after = times2.compose(x -> {
            System.out.println("eSTO ES EL AFTER");
            return x + 5;
        });

        //times2.compose(after).apply(4);
        Integer total = after.apply(5);
        System.out.println("total: "+total);
        System.out.println("-----------");
       /*times2.andThen(squered).apply(4);

        times2.compose(squered).apply(4);
        System.out.println("-------y");
        times2.andThen(squered).apply(4);*/
    }

}
