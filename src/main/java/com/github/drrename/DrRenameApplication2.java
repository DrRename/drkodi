package com.github.drrename;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drrename.event.AvailableRenamingStrategyEvent;
import com.github.drrename.strategy.MediaMetadataRenamingStrategy;
import com.github.drrename.strategy.RegexReplaceRenamingStrategy;
import com.kerner1000.drrename.RenamingStrategy;
import com.github.drrename.strategy.SimpleReplaceRenamingStrategy;
import com.github.drrename.strategy.ToLowerCaseRenamingStrategy;
import com.github.events1000.api.Events;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DrRenameApplication2 extends Application {

	private final static Logger logger = LoggerFactory.getLogger(DrRenameApplication2.class);

	public static void main(final String[] args) {

		launch(args);
	}

	@Override
	public void start(final Stage stage) throws Exception {

		if(logger.isInfoEnabled()) {
			logger.info("Application version " + getClass().getPackage().getImplementationVersion());
		}
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView2.fxml"));
		final Parent root = loader.load();
		final Scene scene = new Scene(root, 600, 600);
		String s = getClass().getPackage().getImplementationVersion();
		if(s == null) {
			s = "dev version";
		}
		stage.setTitle("Dr.Rename " + s);
		stage.setScene(scene);
		stage.show();
		fireInitEvents();
	}

	private void fireInitEvents() {

		// notify the UI about available renaming strategies
		getRenamingStrategies().forEach(s -> fireAvailableStrategyEvent(s));
	}

	private void fireAvailableStrategyEvent(final RenamingStrategy s) {

		Events.getInstance().emit(new AvailableRenamingStrategyEvent(s));
	}

	static List<RenamingStrategy> getRenamingStrategies() {

		final List<RenamingStrategy> result = new ArrayList<>();
		result.add(new SimpleReplaceRenamingStrategy());
		result.add(new RegexReplaceRenamingStrategy());
		result.add(new ToLowerCaseRenamingStrategy());
		result.add(new MediaMetadataRenamingStrategy());
		return result;
	}
}
