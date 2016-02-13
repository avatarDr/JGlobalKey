package hex.jglobalkey;

import hex.collections.ArrayQueue;
import hex.event.HexEventManager;
import hex.event.Listener;
import hex.input.KeyCombination;
import hex.jglobalkey.event.GlobalKeyEvent;
import hex.jglobalkey.os.windows.WindowsKeyManager;
import hex.os.OS;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class JGlobalKeyManager{
	private static JGlobalKeyManager instance=null;
	
	private final Map<Integer,KeyCombination>keyMap=new HashMap<>();
	private final Queue<RegisterTask>taskQueue=new ArrayQueue<>();
	private final KeyThread thread=new KeyThread();
	private final Object oldLock=new Object();
	
	private final Lock lock=new ReentrantLock();
	private final Condition condition=lock.newCondition();
	
	private HexEventManager eventManager=new HexEventManager(Executors.newSingleThreadExecutor());
	private boolean listening=true;
	
	protected void dispatch(int[]keyIds){
		synchronized(oldLock){
			for(int id:keyIds)getEventManager().postEvent(new GlobalKeyEvent(id,keyMap.get(id),true));
		}
	}
	protected abstract int listen();
	protected abstract void notifyRegisterTask();
	protected abstract boolean register(int id,KeyCombination keyCombination);
	protected void threadInit(){}
	
	public HexEventManager getEventManager(){return eventManager;}
	public KeyCombination getGlobalKey(int id){synchronized(oldLock){return keyMap.get(id);}}
	public int[]getRegisteredIds(){synchronized(oldLock){
		int[]result=new int[keyMap.size()];
		int counter=0;
		for(Integer i:keyMap.keySet())result[counter]=i;
		return result;
	}}
	
	public boolean isListening(){return listening;}
	
	public void registerListener(Listener listener){eventManager.registerListener(listener,GlobalKeyEvent.class);}
	
	public void setEventManager(HexEventManager eventManager){this.eventManager=eventManager;}
	public boolean setGlobalKey(int id,KeyCombination keyCombination)throws KeyRegisterException{return setGlobalKey(id,keyCombination,false);}
	public boolean setGlobalKey(int id,KeyCombination keyCombination,boolean repeat)throws KeyRegisterException{
		if(id<0)throw new IllegalArgumentException("id must be non-negative");
		RegisterTask task=new RegisterTask(this,id,keyCombination,repeat);
//		try{
//			lock.lock();
//			
//			condition.await();
//			
//		}
//		catch(InterruptedException ex){throw new KeyRegisterException(ex);}
//		finally{
//			lock.unlock();
//		}
		
		synchronized(oldLock){
			if(keyCombination==null&&!keyMap.containsKey(id))return true;
			taskQueue.add(task);
			if(thread.isAlive())notifyRegisterTask();
			else thread.start();
		}
		try{return task.get();}
		catch(ExecutionException|InterruptedException ex){throw new KeyRegisterException(ex);}
	}
	public void setListening(boolean listening){this.listening=listening;}
	
	public void unregisterListener(Listener l){eventManager.unregisterListener(l,GlobalKeyEvent.class);}
	
	public static JGlobalKeyManager defaultInstance()throws UnsatisfiedLinkError{return instance==null?instance=newInstance():instance;}
	
	public static JGlobalKeyManager newInstance()throws UnsatisfiedLinkError{
		switch(OS.getSystemOS().getFamily()){
			case OS_X:return null;
			case X11:return null;
			case WINDOWS:return new WindowsKeyManager();
			default:return null;
		}
	}
	
	private class KeyThread extends Thread{
		
		private KeyThread(){setName(JGlobalKeyManager.this.getClass().getSimpleName()+"Thread");}
		
		@Override
		public void run(){
			threadInit();
			while(!interrupted()){
				synchronized(oldLock){
					while(!taskQueue.isEmpty()){
						RegisterTask task=taskQueue.poll();
						task.run();
						if(task.getResult()){
							if(task.getKeyCombination()==null)keyMap.remove(task.getId());
							else keyMap.put(task.getId(),task.getKeyCombination());
						}
					}
				}
				int keyId=listen();
				if(keyId!=-1)dispatch(new int[]{keyId});
			}
		}
	}
	
	// Feb 16
	
	protected void doRoutine(){
		
	}
	protected void doRegister(){}
	protected void doDispatch(){}
}