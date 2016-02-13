package hex.jglobalkey.handler;

import hex.jglobalkey.JGlobalKeyManager;

public interface GlobalKeyHandler{
	void handle(int[]keyIds,JGlobalKeyManager keyManager);
}
