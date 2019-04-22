package main.java;

import main.java.blockchain.Block;
import main.java.util.StringUtil;
import main.java.wallet.Transaction;
import main.java.wallet.TransactionOutput;
import main.java.wallet.Wallet;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 */
public class NoobChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    //list of all unspent transactions.
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;
    public static Wallet walletA;
    public static Wallet walletB;

    public static void main(String[] args) {

        //Setup Bouncy castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();

        //Test public and private keys
        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

        //Create a test transaction from WalletA to walletB
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        //Verify the signature works and verify it from the public key
        System.out.println("Is signature verified");
        System.out.println(transaction.verifiySignature());

//        Instant start = Instant.now();
//        System.out.println("Trying to Mine block 1...");
//        addBlock(new Block("Hi im the first block", "0"));
//
//        System.out.println("Trying to Mine block 2... ");
//        addBlock(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).hash));
//
//        System.out.println("Trying to Mine block 3... ");
//        addBlock(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).hash));
//        Instant finish = Instant.now();
//
//        long timeElapsed = Duration.between(start, finish).toMillis();
//        System.out.println("\nTime spent mining (milliseconds): " + timeElapsed);
//
//        System.out.println("\nBlockchain is Valid: " + isChainValid());
//
//        String blockchainJson = StringUtil.getJson(blockchain);
//        System.out.println("\nThe block chain: ");
//        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // compare registered hash and calculateed hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            //check if hash is solved
            if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;

    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

}
