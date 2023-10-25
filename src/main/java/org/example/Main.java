package org.example;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args)
            throws ExecutionException, InterruptedException {
       //test concurrent api
        Executor executor = Executors.newFixedThreadPool(10);
        CompletableFuture<Integer> future = CompletableFuture
                .supplyAsync(()->10,executor)
                .thenApplyAsync(result->result+10,executor)
                .thenApplyAsync(result->result*10,executor);
//        future.thenAcceptAsync(System.out::println);
        //test case 2, run multiple future in parallel
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{
            return "hello";
        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            return "nafiul";
        });
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(()->{
            return "islam";
        });
        CompletableFuture<Void> joinAllFutures = CompletableFuture.allOf(future1,future2,future3);
        joinAllFutures.thenRun(() -> {
            // All futures completed
//            System.out.println(Thread.currentThread().getName());
            String result1 = future1.join();
            String result2 = future2.join();
            String result3 = future3.join();
//            System.out.println(result1 + ", " + result2 + ", " + result3);
        });

        //handling single thread exception
        CompletableFuture<Integer> handlingError = CompletableFuture.supplyAsync(()-> {
            return 10 / 0;
        });
        handlingError.exceptionally(e->{
            System.out.println("Exception occurred "+e.getMessage());
            return 0;
        }).thenAccept(result-> System.out.println(result));
        //handling multiple thread exception
        CompletableFuture<Integer> handlingError1 = CompletableFuture.supplyAsync(() -> {
            // Some long-running operation
            return 10;
        });

        CompletableFuture<Integer> handlingError2 = CompletableFuture.supplyAsync(() -> {
            int result = 10 / 3;
            return result;
        });

        CompletableFuture<Integer> handlingError3 = CompletableFuture.supplyAsync(() -> {
            // Some long-running operation
            return 20;
        });

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(handlingError, handlingError2, handlingError3);

        allFutures.exceptionally(ex -> {
            System.out.println("Exception occurred: " + ex.getMessage());
            return null; // Default value to return if there's an exception
        }).thenRun(() -> {
            // All futures completed
            int result1 = handlingError1.join();
            int result2 = handlingError2.join();
            int result3 = handlingError3.join();
            System.out.println(result1 + ", " + result2 + ", " + result3);
        });

        //composeassync
        CompletableFuture<String> future4 = CompletableFuture.supplyAsync(()->"Hello");
        CompletableFuture<String> future5 = future4.thenComposeAsync(s->
                CompletableFuture.supplyAsync(()->s + " nafiul islam"));
        future5.thenAccept(result-> System.out.println(result));


    }
}