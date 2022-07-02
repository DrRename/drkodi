package com.github.drrename;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drrename.event.AvailableRenamingStrategyEvent;
import com.kerner1000.drrename.RenamingStrategy;
import com.github.events1000.api.Event;
import com.github.events1000.api.Events;
import com.github.events1000.listener.api.AbstractSynchronousEventListener;
import com.github.events1000.listener.api.SynchronousEventListener;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	private final RenamingService sr = new RenamingService();
	@FXML
	private CheckBox checkBox;
	@FXML
	private ComboBox<RenamingStrategy> comboBoxRenamingStrategy;
	@FXML
	private TextField textFieldReplacementStringFrom;
	@FXML
	private TextField textFieldReplacementStringTo;
	@FXML
	private TextField textFieldStartDirectory;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button buttonGo;
	@FXML
	private VBox tilePane;
	private boolean working;

	@FXML
	MenuBar menuBar;

	private final SynchronousEventListener stategyListener = new AbstractSynchronousEventListener(AvailableRenamingStrategyEvent.EVENT_TOPIC) {

		@Override
		public void handle(final Event e) {

			if(logger.isDebugEnabled())
				logger.debug("Got event " + e);
			if(e instanceof AvailableRenamingStrategyEvent) {
				comboBoxRenamingStrategy.getItems().add(((AvailableRenamingStrategyEvent)e).getData());
				comboBoxRenamingStrategy.getSelectionModel().selectFirst();
			}
		}
	};

	public MainController() {

		init();
	}

	private void init() {

		Events.getInstance().registerListener(AvailableRenamingStrategyEvent.EVENT_TOPIC,stategyListener);
	}

	@FXML
	private void handleButtonActionGo(final ActionEvent event) {

		if(working) {
			buttonGo.setText("Cancelling");
			buttonGo.setDisable(true);
			sr.cancel();
		} else {
			sr.reset();
			sr.start();
		}
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac"))
			menuBar.useSystemMenuBarProperty().set(true);

		comboBoxRenamingStrategy.setItems(FXCollections.observableArrayList(getAvailableRenamingStrategies()));
		comboBoxRenamingStrategy.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RenamingStrategy>() {

			@Override
			public void changed(final ObservableValue<? extends RenamingStrategy> observable, final RenamingStrategy oldValue, final RenamingStrategy newValue) {

				textFieldReplacementStringFrom.setDisable(!newValue.isReplacing());
				textFieldReplacementStringTo.setDisable(!newValue.isReplacing());
			}
		});
		sr.renamingStrategyProperty().bind(comboBoxRenamingStrategy.valueProperty());
		sr.replacementStringFromProperty().bind(textFieldReplacementStringFrom.textProperty());
		sr.replacementStringToProperty().bind(textFieldReplacementStringTo.textProperty());
		sr.pathProperty().bind(textFieldStartDirectory.textProperty());
		sr.recursiveProperty().bind(checkBox.selectedProperty());
		sr.setPane(tilePane);
		progressBar.progressProperty().bind(sr.progressProperty());
		sr.setOnFailed(e -> {
			if(logger.isErrorEnabled())
				logger.error(sr.getException().getLocalizedMessage(), sr.getException());
			setWorking(false);
		});
		sr.setOnCancelled(e -> setWorking(false));
		sr.setOnRunning(e -> {
			setWorking(true);
			tilePane.getChildren().clear();
		});
		sr.setOnSucceeded(e -> setWorking(false));
	}

	private List<RenamingStrategy> getAvailableRenamingStrategies() {

		final List<RenamingStrategy> result = new ArrayList<>();
		// check the event history if strategies have been published already
		Events.getInstance().getHistory().forEach(e -> {
			if(e instanceof AvailableRenamingStrategyEvent)
				result.add(((AvailableRenamingStrategyEvent)e).getData());
		});
		return result;
	}

	void setWorking(final boolean working) {

		Platform.runLater(() -> setWorkingFX(working));
	}

	private void setWorkingFX(final boolean working) {

		buttonGo.setDisable(false);
		checkBox.setDisable(working);
		textFieldReplacementStringFrom.setDisable(working);
		textFieldReplacementStringTo.setDisable(working);
		textFieldStartDirectory.setDisable(working);
		comboBoxRenamingStrategy.setDisable(working);
		if(working)
			buttonGo.setText("Cancel");
		else
			buttonGo.setText("Go");
		progressBar.setVisible(working);
		this.working = working;
	}
}
