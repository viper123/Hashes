package com.hevsoft.hashes.calculator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hevsoft.hashes.SupportedAlgorithms;

/**
 * @author erusu
 *Implementation of HashCalculator interface. This object is not thread-safe.
 */
class HashCalculatorImpl implements HashCalculator,HashWorkerListener{

	private static final int THREAD_COUNT = 4;
	private ExecutorService threadPool;
	private HashCalculatorListener listener;
	private List<Worker> workers;
	
	public HashCalculatorImpl() {
		threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
		workers = new ArrayList<>();
	}
	
	@Override
	public HashCalculator setListener(HashCalculatorListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public HashCalculator computeHash(File file, SupportedAlgorithms[] algorithms) {
		if(file == null){
			throw new IllegalArgumentException("file is null");
		}
		if(algorithms == null || algorithms.length == 0 ){
			throw new IllegalArgumentException("algorithms is null or empty");
		}
		for(SupportedAlgorithms alg:algorithms){
			Worker worker = new Worker(alg, file);
			workers.add(worker);
			worker.setListener(this);
			threadPool.submit(worker);
		}
		
		return this;
	}

	@Override
	public void cancel(boolean callListener) {
		for(Worker worker:workers){
			worker.cancel();
		}
		workers.clear();
		if(callListener && listener != null){
			listener.onCanceled();
		}
	}

	@Override
	public void onException(Worker worker,Exception e) {
		workers.remove(worker);
		if(listener != null){
			listener.onException(e);
		}
	}

	@Override
	public void onHashComputed(Worker worker,SupportedAlgorithms alg, byte[] hash) {
		workers.remove(worker);
		if(listener == null){
			return;
		}
		listener.onHashComputed(alg,hash);
		if(workers.size() == 0){
			listener.onFinish();
		}
	}

}
