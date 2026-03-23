package com.example.threadlearning.domain.models;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;


public class FamilyMember {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Getter
    private final String memberId;


    public FamilyMember() {
        this.memberId = "member" + COUNTER.getAndIncrement();
    }
}
