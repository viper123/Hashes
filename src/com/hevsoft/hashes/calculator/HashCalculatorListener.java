package com.hevsoft.hashes.calculator;

import com.hevsoft.hashes.SupportedAlgorithms;

public interface HashCalculatorListener {

	public void onHashComputed(SupportedAlgorithms alg,byte [] data);
	
	public void onFinish();
	
	public void onException(Exception e);
	
	public void onCanceled();
}
