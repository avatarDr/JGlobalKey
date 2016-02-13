package hex.jglobalkey.event;

import hex.event.SpecificListener;

public interface GlobalKeyListener extends SpecificListener{
	void onEvent(GlobalKeyEvent ev);
}