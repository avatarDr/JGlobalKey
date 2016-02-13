package hex.jglobalkey.handler;

import hex.jglobalkey.event.GlobalKeyEvent;
import hex.jglobalkey.JGlobalKeyManager;

public class PressReleaseHandler implements GlobalKeyHandler{
	
	private int pressedId=-1;
	private int tolerance=15;
	private int currentTolerance=0;
	
	@Override
	public void handle(int[]keyIds,JGlobalKeyManager keyManager){
		int newId=keyIds.length>0?keyIds[keyIds.length-1]:-1;
		//Util.debug(pressedId,newId,"len="+keyIds.length,System.currentTimeMillis());
		if(newId!=pressedId){
			boolean changed=true;
			if(pressedId!=-1){
				// probably it is no longer pressed
				
				if(newId==-1&&currentTolerance<tolerance){
					currentTolerance++;
					changed=false;
				}
				else{
					currentTolerance=0;
					keyManager.getEventManager().postEvent(new GlobalKeyEvent(pressedId,keyManager.getGlobalKey(pressedId),false));
				}
			}
			if(changed){
				pressedId=newId;
				if(pressedId!=-1){
					keyManager.getEventManager().postEvent(new GlobalKeyEvent(pressedId,keyManager.getGlobalKey(pressedId),true));
				}
			}
		}
	}
}
