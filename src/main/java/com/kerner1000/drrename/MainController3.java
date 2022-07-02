package com.kerner1000.drrename;

import com.github.drrename.*;
import com.github.drrename.strategy.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import net.sf.kerner.utils.pair.PairSame;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@FxmlView("/fxml/MainView3.fxml")
public class MainController3 implements Initializable, ApplicationListener<ApplicationEvent> {
	
	private final ListFilesService listFilesService;
	private final PreviewService previewService;
	private final ConfigurableApplicationContext applicationContext;
	private final RenamingService2 renamingService;
	public TextField textFieldStartDirectory;
	public ListView<Control> content1;
	public ListView<Control> content2;

	private List<RenamingBean> entries;
	@FXML
	private ComboBox<RenamingStrategy> comboBoxRenamingStrategy;
	@FXML
	private TextField textFieldReplacementStringFrom;
	@FXML
	private TextField textFieldReplacementStringTo;
	private ChangeListener<? super String> replaceStringFromChangeListener;
	private ChangeListener<? super String> replaceStringToChangeListener;
	@FXML
	private Button buttonGo;
	private boolean working;
	private ChangeListener<? super String> textFieldChangeListener;

	@FXML
	private ProgressBar progressBar;
	@FXML
	MenuBar menuBar;

	@FXML
	Node layer01;

	@FXML
	Node layer02_1;

	@FXML
	Node layer02_2;

	@FXML
	Node layer02_3;

	@FXML
	Node layer03_1;

	@FXML
	Node layer03_2;

	@FXML
	Node layer03_3;

	@FXML
	Node layer04_1;

	@FXML
	Node layer04_2;

	private final BuildProperties buildProperties;

	private static PseudoClass test = PseudoClass.getPseudoClass("test");

	public MainController3(ListFilesService listFilesService, PreviewService previewService, ConfigurableApplicationContext applicationContext, BuildProperties buildProperties) {
		this.listFilesService = Objects.requireNonNull(listFilesService);
		this.previewService = Objects.requireNonNull(previewService);
		this.applicationContext = Objects.requireNonNull(applicationContext);
		this.buildProperties = buildProperties;
		this.renamingService = new RenamingService2();
		this.entries = new ArrayList<>();
		initServices();
	}

	private void applyRandomColors() {
		Stream.of(layer01, layer02_1,  layer02_3, layer03_1, layer03_2, layer03_3, layer04_1, layer04_2).forEach(l -> l.setStyle("-fx-background-color: " + getRandomColorString()));
	}

	private String getRandomColorString() {
		return String.format("#%06x", new Random().nextInt(256*256*256));
	}

	public static List<Path> filesToPathList(Collection<File> files) {
		return files.stream().map(File::toPath).collect(Collectors.toList());
	}

	private void registerInputChangeListener() {
		replaceStringFromChangeListener = (e, o, n) -> Platform.runLater(() -> updateOutputView());
		replaceStringToChangeListener = (e, o, n) -> Platform.runLater(() -> updateOutputView());
		textFieldChangeListener = (e, o, n) -> Platform.runLater(() -> updateInputView(Path.of(n)));
		textFieldReplacementStringFrom.textProperty().addListener(replaceStringFromChangeListener);
		textFieldReplacementStringTo.textProperty().addListener(replaceStringToChangeListener);
		textFieldStartDirectory.textProperty().addListener(textFieldChangeListener);
		comboBoxRenamingStrategy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			textFieldReplacementStringFrom.setDisable(!newValue.isReplacing());
			textFieldReplacementStringTo.setDisable(!newValue.isReplacing());
			updateOutputView();
		});
	}

	private void initServices(){
		setRenamingServiceCallbacks();
		setPreviewServiceCallbacks();
	}

	private void setPreviewServiceCallbacks() {
		previewService.setOnRunning(e -> {
			working = true;
			buttonGo.setDisable(true);
			log.debug("Running");
		});
		previewService.setOnFailed(e -> {
			log.error(e.toString());
		});
		previewService.setOnSucceeded(e -> {
			log.debug("Succeeded");
			working = false;
			buttonGo.setDisable(false);
			buttonGo.setText("Go");
		});
		previewService.setOnReady(e -> {
			working = false;
			buttonGo.setDisable(false);
			buttonGo.setText("Go");
			log.debug("Ready");
		});
	}

	private void setRenamingServiceCallbacks() {
		renamingService.setOnRunning(e -> {
			working = true;
			buttonGo.setDisable(true);
			log.debug("Running");
		});
		renamingService.setOnFailed(e -> {
			log.error(e.toString());
		});
		renamingService.setOnSucceeded(e -> {
			log.debug("Succeeded");
			working = false;
			buttonGo.setDisable(false);
			buttonGo.setText("Go");
		});
		renamingService.setOnReady(e -> {
			working = false;
			buttonGo.setDisable(false);
			buttonGo.setText("Go");
			log.debug("Ready");
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac"))
			menuBar.useSystemMenuBarProperty().set(true);
		/* Make scrolling of both lists symmetrical */
		Platform.runLater(() -> {
			FXUtil.getListViewScrollBar(content1).valueProperty()
					.bindBidirectional(FXUtil.getListViewScrollBar(content2).valueProperty());
		});
		content1.setOnDragOver(event -> {
			if ((event.getGestureSource() != content1) && event.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});
		content1.setOnDragDropped(event -> {
			final Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				try {
					updateInputView(filesToPathList(db.getFiles()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				success = true;
			}
			/*
			 * let the source know whether the string was successfully transferred and used
			 */
			event.setDropCompleted(success);
			event.consume();
		});
		comboBoxRenamingStrategy.getItems().add(new SimpleReplaceRenamingStrategy());
		comboBoxRenamingStrategy.getItems().add(new ToLowerCaseRenamingStrategy());
		comboBoxRenamingStrategy.getItems().add(new MediaMetadataRenamingStrategy());
		comboBoxRenamingStrategy.getItems().add(new RegexReplaceRenamingStrategy());

		comboBoxRenamingStrategy.getSelectionModel().selectFirst();

		registerInputChangeListener();

		if(buildProperties
				.getVersion() != null && buildProperties.getVersion().contains("SNAPSHOT"))
		applyRandomColors();

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
			log.error(e.getLocalizedMessage(), e);
		}
	}

	private void updateInputView(final Collection<Path> files) throws IOException {

		cancelCurrentOperation();
		clearView();
		initListFilesService(files);
		progressBar.progressProperty().bind(listFilesService.progressProperty());
		progressBar.visibleProperty().bind(listFilesService.runningProperty());
		startService(listFilesService);
	}

	private void updateInputView(final Path path)  {
		cancelCurrentOperation();
		clearView();
		initListFilesService(path);
		progressBar.progressProperty().bind(listFilesService.progressProperty());
		progressBar.visibleProperty().bind(listFilesService.runningProperty());
		startService(listFilesService);
	}

	private void initListFilesService(Path path)  {
		listFilesService.setFiles(Collections.singleton(path));
	}

	private void initListFilesService(Collection<Path> files) throws IOException {
		listFilesService.setFiles(files);
	}

	private void updateOutputView() {
		initPreviewService();
		progressBar.progressProperty().bind(previewService.progressProperty());
		progressBar.visibleProperty().bind(previewService.runningProperty());
		startService(previewService);
	}

	private void initPreviewService() {
		previewService.setFiles(entries);
		var strat = initAndGetStrategy();
		if(strat != null)
		previewService.setRenamingStrategy(strat);
	}

	private void addToContent(final RenamingBean b) {

		final PairSame<Control> entry = RenamingBeanControlBuilder.buildRenameEntryNode(b);
		content1.getItems().add(entry.getFirst());
		content2.getItems().add(entry.getSecond());
	}

	/**
	 * Starts a {@link Service}. Call in UI thread.
	 * @param service Service to start
	 */
	private void startService(Service<?> service) {
		if ((service.getState() == Worker.State.RUNNING) || (service.getState() == Worker.State.SCHEDULED)) {
			if (log.isDebugEnabled()) {
				log.debug("Still busy, waiting for cancel " + service);
			}
			service.setOnCancelled(e -> {
				if (log.isDebugEnabled()) {
					log.debug("Cancelled " + service);
				}
				startService(service);
			});
			service.cancel();
			return;
		}
		service.reset();
		service.setOnRunning(e -> {
			if (log.isDebugEnabled()) {
				log.debug("Service running " + service);
			}
		});
		service.setOnFailed(e -> {
			if (log.isDebugEnabled()) {
				log.debug("Service {} failed with exception {}", service, service.getException());
			}
		});

		/* This is only *one* callback. configure this separately. */
		/*service.setOnSucceeded(e -> {
			if (log.isDebugEnabled()) {
				log.debug("Service succeeded {} ", service);
			}
		});*/
		service.start();
	}

	private void clearView() {
		content1.getItems().clear();
		content2.getItems().clear();
	}

	private void cancelCurrentOperation() {
		previewService.cancel();
		listFilesService.cancel();

	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
//		log.debug("Event received: {}", event);
		if(event instanceof FileEntryEvent){
			entries.add((RenamingBean) event.getSource());
			Platform.runLater(()->addToContent((RenamingBean) event.getSource()));
		}
//		entries.forEach(b -> b.onApplicationEvent(event));
	}

	private RenamingStrategy initAndGetStrategy() {

		final RenamingStrategy strategy = comboBoxRenamingStrategy.getSelectionModel().getSelectedItem();
		if(strategy == null)
			return null;
		strategy.setReplacementStringFrom(textFieldReplacementStringFrom.getText());
		strategy.setReplacementStringTo(textFieldReplacementStringTo.getText());
		return strategy;
	}

	public void handleButtonActionGo(ActionEvent actionEvent) {

			final RenamingStrategy s = initAndGetStrategy();
			if(s != null) {
				renamingService.cancel();
				renamingService.reset();
				renamingService.setEvents(entries);
				renamingService.setStrategy(s);
				renamingService.start();
			} else {
				log.info("No renaming strategy selected");
		}
	}
}
