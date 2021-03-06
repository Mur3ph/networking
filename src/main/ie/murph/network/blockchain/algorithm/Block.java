package main.ie.murph.network.blockchain.algorithm;

import java.util.Date;

import org.apache.log4j.Logger;

import main.ie.murph.network.utility.StringUtil;

public class Block {
	private static final Logger LOGGER = Logger.getLogger(Block.class);
	public String hash;
	public String previousHash; 
	private String data; 									// Our data will be a simple message.
	private long timeStampInMillisecondsSinceUnixEpoch; 	// As number of milliseconds since 1/1/1970. Unix epoch..
	private int nonce; 										// A Nonce is an arbitrary number used only once in a cryptographic communication
	
	public Block(String data, String previousHash ) {
		LOGGER.info("++Block()");
		this.data = data;
		this.previousHash = previousHash;
		this.timeStampInMillisecondsSinceUnixEpoch = new Date().getTime();
		this.hash = calculateHash(); //Make sure we calculate this hash value, after we set the other values.
		LOGGER.info("++Block()");
	}
	
	//Calculate new hash based on blocks contents
	public String calculateHash() {
		LOGGER.info("++calculateHash()");
		String calculatedhash = StringUtil.applySHA256CryptoAlgorithm( 
				previousHash +
				Long.toString(timeStampInMillisecondsSinceUnixEpoch) +
				Integer.toString(nonce) + 
				data 
				);
		LOGGER.info("++calculateHash()");
		return calculatedhash;
	}
	
	//Increases Nonce value until hash target is reached.
	public void mineBlock(int difficultyLength) {
		LOGGER.info("++mineBlock()");
		String hashTarget = StringUtil.getDificultyString(difficultyLength); //Create a string with difficulty * "0" 
		while(!hash.substring( 0, difficultyLength).equals(hashTarget)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined! : " + hash);
		LOGGER.info("++mineBlock(): Block Mined! : \" + hash");
	}
	
}
