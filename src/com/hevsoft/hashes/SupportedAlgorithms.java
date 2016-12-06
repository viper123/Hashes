package com.hevsoft.hashes;

/**
 * MD5 128 bits = 16bytes
 * SHA-1 160 bits =  20 bytes
 * SHA-256 256 bits = 32 bytes
 * SHA-384 384 bits = 48 bytes
 * SHA-512 512 bits = 64 bytes 
 */
public enum SupportedAlgorithms {
	
	MD5(16,"MD5"),
	SHA1(20,"SHA-1"),
	SHA256(32,"SHA-256"),
	SHA384(48,"SHA-384"),
	SHA512(64,"SHA-512");
	/**
	construct enum
	*/
	private SupportedAlgorithms(int keySize,String algorithm){
		this.keySize = keySize;
		this.algorithm = algorithm;
	}
	
	private int keySize;
	private String algorithm;
	
	public int getKeySize(){
		return keySize;
	}
	
	public static SupportedAlgorithms fromIndex(int index){
		for(SupportedAlgorithms alg:values()){
			if(alg.ordinal() == index){
				return alg;
			}
		}
		return null;
	}
	
	public String getAlgAsString(){
		return algorithm;
	}
}
