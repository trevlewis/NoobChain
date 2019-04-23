package main.java.blockchain;

import main.java.util.StringUtil;
import main.java.wallet.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>(); // Our data will be a simple message.
    private long timeStamp; // As number of milliseconds since 1/1/1970.
    private int nonce;

    // Block Constructor
    public Block(String previousHash) {
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
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);

        // Create a string with difficulty * "0"
        String target = StringUtil.getDificultyString(difficulty);

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!!!: " + hash);
    }

    // Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        // Process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) {
            return false;
        }
        if (!previousHash.equals("0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to Block!");
        return true;
    }

}
