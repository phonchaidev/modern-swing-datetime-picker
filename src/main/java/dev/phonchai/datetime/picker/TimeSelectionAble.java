package dev.phonchai.datetime.picker;

import java.time.LocalTime;

public interface TimeSelectionAble {

    boolean isTimeSelectedAble(LocalTime time, boolean hourView);
}
