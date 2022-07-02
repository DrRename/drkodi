package com.github.drrename;

import java.nio.file.Path;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import net.sf.kerner.utils.concurrent.Pipe;

public interface ConfirmDialogFactory {

	public static void showDialog(final Path file, final String newName, final Pipe<Integer> exReturnval) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				final Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Please confirm");
				alert.setHeaderText("Directory: " + file.getParent());
				alert.setContentText("Do you want to rename from\n'" + file.getFileName().toString() + "'\nto\n'" + newName + "'?");
				final ButtonType buttonTypeOne = new ButtonType("Yes");
				final ButtonType buttonTypeTwo = new ButtonType("Yes to all");
				final ButtonType buttonTypeThree = new ButtonType("No");
				final ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeThree, buttonTypeCancel);
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				final Optional<ButtonType> result = alert.showAndWait();
				try {
					if(result.get() == buttonTypeOne)
						exReturnval.put(0);
					else if(result.get() == buttonTypeTwo)
						exReturnval.put(3);
					else if(result.get() == buttonTypeThree)
						exReturnval.put(1);
					else
						exReturnval.put(2);
				} catch(final InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
