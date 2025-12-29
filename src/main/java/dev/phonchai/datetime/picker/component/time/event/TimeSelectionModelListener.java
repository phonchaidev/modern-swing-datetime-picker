package dev.phonchai.datetime.picker.component.time.event;

import java.util.EventListener;

public interface TimeSelectionModelListener extends EventListener {

    void timeSelectionModelChanged(TimeSelectionModelEvent e);
}
