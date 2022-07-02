package com.kerner1000.drrename;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;

public class FXUtil {
    static ScrollBar getListViewScrollBar(final ListView<?> listView) {

        ScrollBar scrollbar = null;
        for (final Node node : listView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                final ScrollBar bar = (ScrollBar) node;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    scrollbar = bar;
                }
            }
        }
        return scrollbar;
    }
}
