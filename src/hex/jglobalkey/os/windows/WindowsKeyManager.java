package hex.jglobalkey.os.windows;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import hex.input.KeyCombination;
import hex.jglobalkey.JGlobalKeyManager;
import hex.math.Bitwise;

import java.util.logging.Logger;

public class WindowsKeyManager extends JGlobalKeyManager{
	
	/**
	 * 
	 */
	public static final int MODIFIER_NO_REPEAT=0x4000;
	
	/**
	 * Timeout, in ms, between first and second hotkey event triggering.
	 */
	public static final int WINDOWS_TIMEOUT_FIRST=500;
	
	/**
	 * Timeout, in ms, between hotkey event triggering.
	 */
	public static final int WINDOWS_TIMEOUT_GENERAL=33;
	
	private static final int WM_HOTKEY=0x0312;
	private static final int WM_APP_REGISTER=0x8000;
	private static User32 library=null;
	
	private final User32.MSG msg=new User32.MSG();
	
	private int id=-1;
	
	public WindowsKeyManager()throws UnsatisfiedLinkError{if(library==null)library=(User32)Native.loadLibrary("User32",User32.class,W32APIOptions.DEFAULT_OPTIONS);}
	
	@Override
	protected void notifyRegisterTask(){
		if(id==-1)Logger.getLogger("jglobalkey").severe("NotifyRegisterTask before thread id has been set");
		library.PostThreadMessage(id,WM_APP_REGISTER,null,null);
	}
	
	@Override
	protected void threadInit(){id=((Kernel32)Native.loadLibrary("Kernel32",Kernel32.class)).GetCurrentThreadId();}
	
	@Override
	protected int listen(){
		library.GetMessage(msg,null,0,0);
		if(msg.message==WM_HOTKEY)return isListening()?msg.wParam.intValue():-1;
		else if(msg.message==WM_APP_REGISTER){
			//TODO: regTask
			return -1;
		}
		else Logger.getLogger("jglobalkey").severe("Received unexpected message with type = "+msg.message);
		return -1;
	}
	
	@Override
	protected boolean register(int id,KeyCombination keyCombination){
		if(keyCombination==null)return library.UnregisterHotKey(null,id);
		library.UnregisterHotKey(null,id);
		int key=keyCombination.getKey();
		int modifiers=keyCombination.getModifiers();
		return library.RegisterHotKey(null,id,convertModifiers(modifiers,key),key);
	}
	
	@Override
	protected void doRoutine(){
		library.GetMessage(msg,null,0,0);
		if(msg.message==WM_HOTKEY) doDispatch();
		else if(msg.message==WM_APP_REGISTER) doRegister();
		else Logger.getLogger("jglobalkey").severe("Received unexpected message of type: "+msg.message);
	}
	
	private static int convertModifiers(int modifiers,int key){
		if(Bitwise.hasMask(modifiers,1))modifiers+=3;
		if(Bitwise.hasMask(modifiers,8))modifiers-=7;
		modifiers+=MODIFIER_NO_REPEAT;
		return modifiers;
	}
	
	private interface Kernel32 extends StdCallLibrary{int GetCurrentThreadId();}
}