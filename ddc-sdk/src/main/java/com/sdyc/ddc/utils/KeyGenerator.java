package com.sdyc.ddc.utils;

import com.radiance.tonclient.*;
import java.util.concurrent.CompletableFuture;

public class KeyGenerator {
    private TONContext context;
    private Crypto crypto;

    public KeyGenerator() throws TONException{
        context = TONContext.create(new Client.ClientConfig(
                new Client.NetworkConfig(PropertiesReader.config.getString("network"))
        ));
        crypto = new Crypto(context);
    }

    public CompletableFuture<Crypto.KeyPair> generate() {
        return crypto.generateRandomSignKeys();
    }

    public void destroy() {
        context.destroy();
    }

    public static Crypto.KeyPair getKey(){
        try {
            KeyGenerator g = new KeyGenerator();
            CompletableFuture<Crypto.KeyPair> future = g.generate().whenComplete((keys, ex) -> {
                g.destroy();
            });
            return future.get();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}