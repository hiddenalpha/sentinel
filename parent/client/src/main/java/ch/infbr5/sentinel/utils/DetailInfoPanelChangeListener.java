package ch.infbr5.sentinel.utils;

import java.util.EventListener;

public interface DetailInfoPanelChangeListener extends EventListener {
	public void detailInfoPanelChanged(DetailInfoPanelChangedEvent e);
}
