package main.java.wallet;

import main.java.NoobChain;
import main.java.util.StringUtil;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

    public String transactionId; // This is also the hash of the transaction.
    public PublicKey sender; // Senders address/public key.
    public PublicKey reciepient; // Recipients address/public key.
    public float value;
    public byte[] signature; // This is to prevent anybody else from spending funds in our wallet.

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // A rough count of how many transactions have been generated.

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This calculates the transaction hash (which will be used as its ID)
    private String calculateHash() {
        // increase the sequence to avoid two identical transactions having the same hash
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        value +
                        sequence
        );
    }

    // Signs all the data we don't wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verifies the data we signed hasn't been tampered with.
    public boolean verifiySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    // Returns true if new transaction could be created.
    public boolean processTransaction() {

        if (verifiySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gather transaction inputs (Make sure they are unspent)
        for (TransactionInput i : inputs) {
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        // Check if transaction is valid
        if (getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        // send value to recipient
        outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
        // send the left over 'change' back to sender
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        // add outputs to Unspent list
        for (TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.id, o);
        }

        // remove transaction inputs from UTXO lists as spent
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                // if Transaction can't be found, skip it
                continue;
            }
            NoobChain.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }

    // returns sum of inputs(UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) {
                // if Transaction can't be found, skip it
                continue;
            }
            total += i.UTXO.value;
        }
        return total;
    }

    // returns sum of outputs
    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

}
