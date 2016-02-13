package hex.jglobalkey.event;

import hex.event.type.Event;
import hex.input.KeyCombination;

public class GlobalKeyEvent extends Event{
	private final int id;
	private final KeyCombination keyCombination;
	private final boolean press;
	
	public GlobalKeyEvent(int id,KeyCombination keyCombination,boolean press){
		this.id=id;
		this.keyCombination=keyCombination;
		this.press=press;
	}
	
	public int getId(){return id;}
	public KeyCombination getKeyCombination(){return keyCombination;}
	
	public boolean isPress(){return press;}
}