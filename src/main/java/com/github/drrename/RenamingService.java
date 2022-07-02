/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.drrename;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drrename.model.JobRename;
import com.kerner1000.drrename.RenamingStrategy;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Alexander Kerner
 *
 */
public class RenamingService extends Service<Void> implements JobRename.Listener {

	private final Logger log = LoggerFactory.getLogger(RenamingService.class);
	private final StringProperty path = new SimpleStringProperty(null);
	private final StringProperty replacementStringFrom = new SimpleStringProperty(null);
	private final StringProperty replacementStringTo = new SimpleStringProperty(null);
	private final BooleanProperty recursive = new SimpleBooleanProperty(false);
	private final ObjectProperty<RenamingStrategy> renamingStrategy = new SimpleObjectProperty<>(null);
	public VBox pane;

	@Override
	protected Task<Void> createTask() {

		final Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				if(log.isInfoEnabled()) {
					log.info("Starting in " + path.getValue());
					log.info("Strategy: " + renamingStrategy.getValue());
					log.info("Recursive: " + recursive.getValue());
				}
				renamingStrategy.getValue().setReplacementStringFrom(getReplacementStringFrom());
				renamingStrategy.getValue().setReplacementStringTo(getReplacementStringTo());
				final JobRename j = new JobRename(renamingStrategy.getValue(), path.getValue(), recursive.getValue());
				j.getListener().add(RenamingService.this);
				return j.call();
			}
		};
		return task;
	}

	public VBox getPane() {

		return pane;
	}

	public final String getPath() {

		return pathProperty().get();
	}

	public final RenamingStrategy getRenamingStrategy() {

		return renamingStrategyProperty().get();
	}

	public final String getReplacementStringFrom() {

		return replacementStringFromProperty().get();
	}

	public final String getReplacementStringTo() {

		return replacementStringToProperty().get();
	}

	public final boolean isRecursive() {

		return recursiveProperty().get();
	}

	@Override
	public void nextFile(final String oldName, final String newName) {

		final Text t1 = new Text(oldName);
		t1.setFill(Color.TOMATO);
		final Text t2 = new Text(" -> ");
		final Text t3 = new Text(newName);
		t3.setFill(Color.MEDIUMSEAGREEN);
		final TextFlow tf = new TextFlow();
		tf.getChildren().addAll(t1, t2, t3);
		Platform.runLater(() -> {
			final int k = pane.getChildren().size();
			if(k > 100)
				pane.getChildren().remove(0);
			pane.getChildren().add(tf);
		});
	}

	public final StringProperty pathProperty() {

		return path;
	}

	public final BooleanProperty recursiveProperty() {

		return recursive;
	}

	public final ObjectProperty<RenamingStrategy> renamingStrategyProperty() {

		return renamingStrategy;
	}

	public final StringProperty replacementStringFromProperty() {

		return replacementStringFrom;
	}

	public final StringProperty replacementStringToProperty() {

		return replacementStringTo;
	}

	public void setPane(final VBox pane) {

		this.pane = pane;
	}

	public final void setPath(final String path) {

		pathProperty().set(path);
	}

	public final void setRecursive(final boolean recursive) {

		recursiveProperty().set(recursive);
	}

	public final void setRenamingStrategy(final RenamingStrategy renamingStrategy) {

		renamingStrategyProperty().set(renamingStrategy);
	}

	public final void setReplacementStringFrom(final String replacementStringFrom) {

		replacementStringFromProperty().set(replacementStringFrom);
	}

	public final void setReplacementStringTo(final String replacementStringTo) {

		replacementStringToProperty().set(replacementStringTo);
	}
}
