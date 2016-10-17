package com.hevsoft.hashes.calculator;

import com.hevsoft.hashes.SupportedAlgorithms;

interface HashWorkerListener {

	void onException(Worker worker,Exception e);
	void onHashComputed(Worker worker,SupportedAlgorithms alg,byte[] hash);
}
