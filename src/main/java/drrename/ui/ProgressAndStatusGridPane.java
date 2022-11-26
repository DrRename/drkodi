/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This file is part of Dr.Rename.
 *
 *     You can redistribute it and/or modify it under the terms of the GNU Affero
 *     General Public License as published by the Free Software Foundation, either
 *     version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT
 *     ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *     FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ProgressAndStatusGridPane extends GridPane {

    private final ProgressBar progressBar;

    private final HBox progressStatusBox;

    public ProgressAndStatusGridPane(){
        this.progressBar = new ProgressBar();
        this.progressStatusBox = new HBox(4);
        this.progressStatusBox.setPadding(new Insets(4,4,4,4));
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        getColumnConstraints().addAll(columnConstraints, columnConstraints);
        setPadding(new Insets(4,4,4,4));
        add(progressBar, 0, 0);
        add(progressStatusBox, 1, 0);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public HBox getProgressStatusBox() {
        return progressStatusBox;
    }
}
