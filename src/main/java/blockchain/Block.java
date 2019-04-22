package main.java.blockchain;

import main.java.util.StringUtil;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data; // Our data will be a simple message.
    private long timeStamp; // As number of milliseconds since 1/1/1970.
    private int nonce;

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
                        nonce +
                        data
        );
    }

    public void mineBlock(int difficulty) {
        // Create a string with difficulty * "0"
        String target = StringUtil.getDificultyString(difficulty);

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!!!: " + hash);
    }

}
