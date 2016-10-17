package com.hevsoft.hashes.calculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.hevsoft.hashes.SupportedAlgorithms;
import com.hevsoft.hashes.common.Handler;

class Worker implements Runnable{

	private static final int BUFFER_SIZE = 8192;
	private Object CANCEL_LOCK = new Object();
	private SupportedAlgorithms alg;
	private HashWorkerListener listener;
	private File file;
	private boolean canceled = false;
	private Handler handler;
	
	public Worker(SupportedAlgorithms alg,File file) {
		this.alg = alg;
		this.file = file;
		this.handler = new Handler();
	}
	
	@Override
	public void run() {
		InputStream fiStream = null;
		try{
			MessageDigest digest = MessageDigest.getInstance(alg.getAlgAsString());
			fiStream = new FileInputStream(file);
		    int n = 0;
		    byte[] buffer = new byte[BUFFER_SIZE];
		    while (n != -1 && !isCanceled()) {
		        n = fiStream.read(buffer);
		        if (n > 0) {
		            digest.update(buffer, 0, n);
		        }
		    }
		    byte [] data =  digest.digest();
		    deliverSuccess(data);
			
		}catch(NoSuchAlgorithmException ex){
			ex.printStackTrace();
			deliverError(ex);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			deliverError(ex);
		} catch (IOException ex) {
			ex.printStackTrace();
			deliverError(ex);
		}catch(Exception ex){
			ex.printStackTrace();
			deliverError(ex);
		}finally{
			try {
				fiStream.close();
			} catch (IOException e) {
				//ignore exception
			} 
		}
	}
	
	private void deliverSuccess(byte [] data){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if(listener!=null && !isCanceled()){
					listener.onHashComputed(Worker.this,alg,data );
				}
			}
		});
		
	}
	
	private void deliverError(Exception e){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if(listener != null && !isCanceled()){
					listener.onException(Worker.this,e);
				}
			}
		});
		
	}
	
	public  void cancel(){
		synchronized (CANCEL_LOCK) {
			this.canceled = true;
		}
	}
	
	public boolean isCanceled(){
		synchronized (CANCEL_LOCK) {
			return this.canceled;
		}
	}

	public void setListener(HashWorkerListener listener) {
		this.listener = listener;
	}

}
