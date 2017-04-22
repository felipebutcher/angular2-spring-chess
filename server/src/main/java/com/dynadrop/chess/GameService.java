package com.dynadrop.chess;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameService {

    @Autowired
    GameHandler counterHandler;

    /*@Scheduled(fixedDelay = 1000)
    public void sendCounterUpdate() {
        counterHandler.counterIncrementedCallback(counter.incrementAndGet());
    }*/

    Integer getValue() {
        return 123;
    }
}
