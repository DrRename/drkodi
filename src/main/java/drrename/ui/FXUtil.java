package drrename.ui;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollBar;

public class FXUtil {
    public static ScrollBar getListViewScrollBar(final ListView<?> listView) {

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

    public static void initAppMenu(MenuBar menuBar) {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
    }
}
