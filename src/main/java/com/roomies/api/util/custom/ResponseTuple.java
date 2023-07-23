package com.roomies.api.util.custom;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseTuple<V1,V2,V3>{
    private V1 val1;
    private V2 val2;
    private V3 val3;

    public V1 getVal1() {
        return val1;
    }

    public V2 getVal2() {
        return val2;
    }

    public V3 getVal3() {
        return val3;
    }

    @Override
    public String toString() {
        return "ResponseTuple{" +
                "val1=" + val1 +
                ", val2=" + val2 +
                ", val3=" + val3 +
                '}';
    }
}
