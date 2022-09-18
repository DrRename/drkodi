package com.github.drrename;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import com.kerner1000.drrename.RenamingBean;
import drrename.RenamingService;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drrename.event.AvailableRenamingStrategyEvent;
import com.kerner1000.drrename.RenamingStrategy;
import com.github.events1000.api.Event;
import com.github.events1000.api.Events;
import com.github.events1000.listener.api.AbstractSynchronousEventListener;
import com.github.events1000.listener.api.SynchronousEventListener;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.kerner.utils.pair.PairSame;
import net.sf.kerner.utils.pair.PairSameImpl;

public class MainController2 implements Initializable {

	private static final Logger logger = LoggerFactory.getLogger(MainController2.class);
	@FXML
	private TextField textFieldStartDirectory;
	@FXML
	private TextField textFieldReplacementStringFrom;
	@FXML
	private TextField textFieldReplacementStringTo;
	// @FXML
	// private TextArea areaInputFiles;
	private final RenamingService sr;
	// @FXML
	// private TextArea areaOutputFiles;
	@FXML
	private ScrollPane scroll1;
	@FXML
	private ScrollPane scroll2;
	@FXML
	private Pane content1;
	@FXML
	private Pane content2;
	private ChangeListener<? super String> textFieldChangeListener;
	private ChangeListener<? super String> replaceStringFromChangeListener;
	private ChangeListener<? super String> replaceStringToChangeListener;
	private List<RenamingBean> files;
	@FXML
	private ComboBox<RenamingStrategy> comboBoxRenamingStrategy;
	@FXML
	private Button buttonGo;
	private boolean working;
	@FXML
	private ProgressBar progressBar;
	@FXML
	MenuBar menuBar;
	private String currentInputString;
	private final SynchronousEventListener stategyListener = new AbstractSynchronousEventListener(AvailableRenamingStrategyEvent.EVENT_TOPIC) {

		@Override
		public void handle(final Event e) {

			if(logger.isDebugEnabled()) {
				logger.debug("Got event " + e);
			}
			if(e instanceof AvailableRenamingStrategyEvent) {
				comboBoxRenamingStrategy.getItems().add(((AvailableRenamingStrategyEvent)e).getData());
				comboBoxRenamingStrategy.getSelectionModel().selectFirst();
			}
		}
	};

	public MainController2() {

		sr = new RenamingService();
		init();
	}

	private void init() {

		Events.getInstance().registerListener(AvailableRenamingStrategyEvent.EVENT_TOPIC,stategyListener);
		textFieldChangeListener = (e, o, n) -> {
			currentInputString = n;
			Platform.runLater(() -> updateInputView());
		};
		replaceStringFromChangeListener = (e, o, n) -> Platform.runLater(() -> updateOutputView());
		replaceStringToChangeListener = (e, o, n) -> Platform.runLater(() -> updateOutputView());
	}

	private void updateInputView() {

		content1.getChildren().clear();
		content2.getChildren().clear();
		try {
			files = getEntries(Paths.get(currentInputString));
			for(int i = 0; i < files.size(); i++) {
				final RenamingBean f = files.get(i);
				final PairSame<TextField> n = buildRenameEntryNode(f);
				content1.getChildren().add(n.getFirst());
				content2.getChildren().add(n.getSecond());
			}
		} catch(final IOException e) {
		}
	}

	private PairSame<TextField> buildRenameEntryNode(final RenamingBean f) {

		final TextField tLeft = new TextField(f.getOldPath().getFileName().toString());
		final TextField tRight = new TextField();
		tLeft.setEditable(false);
		tRight.setEditable(false);
		final Color vColor = Color.web("#f5f5f5");
		tLeft.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
		tRight.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
		// System.err.println(tRight.getTextFill());
		tRight.textProperty().bind(Bindings.createStringBinding(calcValue(f), f.getException(), f.getNewPath()));
		// tRight.styleProperty().bind(Bindings.createStringBinding(calcValue2(f), tRight.textProperty()));
		tRight.textProperty().addListener((e, o, n) -> {
			if(!f.getOldPath().getFileName().toString().equals(f.getNewPath().get())) {
				final Animation animation = new Transition() {

					{
						setCycleDuration(Duration.millis(2000));
						setInterpolator(Interpolator.EASE_OUT);
					}

					@Override
					protected void interpolate(final double frac) {

						final Color vColor = new Color(0.60, 0.90, 0.60, frac);
						tRight.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
					}
				};
				animation.play();
			} else {
				final Animation animation = new Transition() {

					{
						setCycleDuration(Duration.millis(2000));
						setInterpolator(Interpolator.EASE_IN);
					}

					@Override
					protected void interpolate(final double frac) {

						final Color vColor = new Color(0.60, 0.90, 0.60, 1 - frac);
						tRight.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
					}
				};
				animation.play();
			}
		});
		return new PairSameImpl<>(tLeft, tRight);
	}

	private Callable<String> calcValue2(final RenamingBean f) {

		return () -> {
			if(!f.getOldPath().getFileName().toString().equals(f.getNewPath().get()))
				return "-fx-background-color: orange;";
			return null;
		};
	}

	private Callable<String> calcValue(final RenamingBean f) {

		return () -> {
			if(f.getException().get() != null)
				return f.getException().get().getMessage();
			return f.getNewPath().getValue() != null ? f.getNewPath().getValue() : null;
		};
	}

	private void updateOutputView() {

		final Optional<RenamingStrategy> strategy = initAndGetStrategy();
		if((strategy.isPresent()) && (files != null)) {
			for(final RenamingBean p : files) {
				try {
					p.preview(strategy.get());
				} catch(final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// areaOutputFiles.textProperty().set(newNames.stream().collect(Collectors.joining("\n")));
		}
	}

	private Optional<RenamingStrategy> initAndGetStrategy() {

		final RenamingStrategy strategy = comboBoxRenamingStrategy.getSelectionModel().getSelectedItem();
		if(strategy == null)
			return Optional.empty();
		strategy.setReplacementStringFrom(textFieldReplacementStringFrom.getText());
		strategy.setReplacementStringTo(textFieldReplacementStringTo.getText());
		return Optional.of(strategy);
	}

	public static List<RenamingBean> getEntries(final Path dir) throws IOException {

		final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(final Path file) throws IOException {

				return Files.isRegularFile(file);
			}
		};
		final List<RenamingBean> entries = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for(final Iterator<Path> it = stream.iterator(); it.hasNext();) {
				entries.add(new RenamingBean(it.next()));
			}
		}
		return entries;
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac"))
			menuBar.useSystemMenuBarProperty().set(true);

		textFieldStartDirectory.textProperty().addListener(textFieldChangeListener);
		textFieldReplacementStringFrom.textProperty().addListener(replaceStringFromChangeListener);
		textFieldReplacementStringTo.textProperty().addListener(replaceStringToChangeListener);
		comboBoxRenamingStrategy.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RenamingStrategy>() {

			@Override
			public void changed(final ObservableValue<? extends RenamingStrategy> observable, final RenamingStrategy oldValue, final RenamingStrategy newValue) {

				textFieldReplacementStringFrom.setDisable(!newValue.isReplacing());
				textFieldReplacementStringTo.setDisable(!newValue.isReplacing());
				updateOutputView();
			}
		});
		progressBar.progressProperty().bind(sr.progressProperty());
		sr.setOnFailed(e -> {
			if(logger.isErrorEnabled()) {
				logger.error(sr.getException().getLocalizedMessage(), sr.getException());
			}
			setWorking(false);
		});
		sr.setOnCancelled(e -> setWorking(false));
		sr.setOnRunning(e -> {
			setWorking(true);
		});
		sr.setOnSucceeded(e -> setWorking(false));
		scroll1.vvalueProperty().bindBidirectional(scroll2.vvalueProperty());
	}

	public boolean isWorking() {

		return working;
	}

	public void setWorking(final boolean working) {

		this.working = working;
		Platform.runLater(() -> updateInputView());
		Platform.runLater(() -> updateOutputView());
	}

	private List<RenamingStrategy> getAvailableRenamingStrategies() {

		final List<RenamingStrategy> result = new ArrayList<>();
		// check the event history if strategies have been published already
		Events.getInstance().getHistory().forEach(e -> {
			if(e instanceof AvailableRenamingStrategyEvent) {
				result.add(((AvailableRenamingStrategyEvent)e).getData());
			}
		});
		return result;
	}

	@FXML
	private void handleMenuItemRegexTips(final ActionEvent event) {

		try {
			final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/RegexTipsView.fxml"));
			final Parent root = loader.load();
			final Stage stage = new Stage();
			final Scene scene = new Scene(root);
			stage.setTitle("Regex Tips");
			stage.setScene(scene);
			stage.setWidth(400);
			stage.setHeight(400);
			stage.show();
		} catch(final IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@FXML
	private void handleButtonActionGo(final ActionEvent event) {

		if(working) {
			buttonGo.setText("Cancelling");
			buttonGo.setDisable(true);
			sr.cancel();
		} else {
			sr.reset();
			final Optional<RenamingStrategy> s = initAndGetStrategy();
			if(s.isPresent()) {
				sr.setEvents(files);
				sr.setStrategy(s.get());
				sr.start();
			}
		}
	}
}
