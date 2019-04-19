package main.java;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data; // Our data will be a simple message.
    private long timeStamp; // As number of milliseconds since 1/1/1970.

    // Block Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        // Making sure we do this after we set the other values.
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        timeStamp +
                        data
                );
    }
}
