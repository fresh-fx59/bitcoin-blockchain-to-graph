package com.example.bitcoinblockchaintograph.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ForkJoinPool;

@Getter
public class PoolConfig {
    @Value("${pool.parallelism}")
    public static int parallelism;
    public static ForkJoinPool forkJoinPool = new ForkJoinPool(8);
}
