package hex.jglobalkey.os.windows;

import com.sun.jna.IntegerType;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public interface User32 extends Library{
	boolean GetMessage(MSG lpMsg,Pointer hWnd,int wMsgFilterMin,int wMsgFilterMax);
	boolean PostThreadMessage(int idThread,int Msg,Parameter wParam,Parameter lParam);
	boolean RegisterHotKey(Pointer hWnd,int id,int fsModifiers,int vk);
	boolean UnregisterHotKey(Pointer hWnd,int id);
	
	class MSG extends Structure{
		public Pointer hWnd;
		public int message;
		public Parameter wParam;
		public Parameter lParam;
		public int time;
		public int x;
		public int y;
		
		@Override
		protected List getFieldOrder(){return Arrays.asList("hWnd","message","wParam","lParam","time","x","y");}
	}
	
	class Parameter extends IntegerType{
		public Parameter(){this(0);}
		public Parameter(long value){super(Pointer.SIZE,value);}
	}
}