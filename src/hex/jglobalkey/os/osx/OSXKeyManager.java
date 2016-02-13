package hex.jglobalkey.os.osx;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import hex.input.KeyCombination;
import hex.jglobalkey.JGlobalKeyManager;

public abstract class OSXKeyManager extends JGlobalKeyManager{
	private static Carbon library=null;
	
	protected OSXKeyManager()throws UnsatisfiedLinkError{
		if(library==null)library=(Carbon)Native.loadLibrary(Carbon.class.getSimpleName(),Carbon.class);
	}
	
	@Override
	protected int listen(){
		
		return -1;
	}
	@Override
	protected boolean register(int id, KeyCombination keyCombination){
		Carbon.EventHotKeyID.ByValue hotkeyReference=new Carbon.EventHotKeyID.ByValue();
		hotkeyReference.id=id;
		hotkeyReference.signature=OS_TYPE("hk"+String.format("%02d",id));
		PointerByReference gMyHotKeyRef=new PointerByReference();
		return false;
	}
	
	private static int OS_TYPE(String osType) {
		byte[] bytes = osType.getBytes();
		return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
	}
}