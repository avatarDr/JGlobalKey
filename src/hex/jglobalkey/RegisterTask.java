package hex.jglobalkey;

import hex.input.KeyCombination;

import java.util.concurrent.*;

public class RegisterTask extends FutureTask<Boolean>{
	private final KeyCombination keyCombination;
	private final int id;
	private final boolean repeat;
	
	public RegisterTask(JGlobalKeyManager manager,int id,KeyCombination keyCombination,boolean repeat){
		super(()->manager.register(id,keyCombination));
		this.keyCombination=keyCombination;
		this.id=id;
		this.repeat=repeat;
	}
	
	public int getId(){return id;}
	
	public KeyCombination getKeyCombination(){return keyCombination;}
	
	public boolean getResult(){
		try{return get();}
		catch(ExecutionException|InterruptedException ignored){return false;}
	}
}