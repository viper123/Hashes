package com.hevsoft.hashes.calculator;

import java.io.File;

import com.hevsoft.hashes.SupportedAlgorithms;

public interface HashCalculator {

	public HashCalculator setListener(HashCalculatorListener listener);
	
	public HashCalculator computeHash(File file, SupportedAlgorithms[] algorithms);
	
	public void  cancel(boolean callListener);
	
	public static enum Factory{
		INSTANCE;
		
		public HashCalculator getHashCalculator(){
			return new HashCalculatorImpl(); 
		}
	}
}
