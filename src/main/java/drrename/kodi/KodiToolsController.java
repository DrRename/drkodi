package drrename.kodi;

import com.kerner1000.drrename.GoCancelButtonsComponentController;
import com.kerner1000.drrename.StartDirectoryComponentController;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/KodiTools.fxml")
public class KodiToolsController implements Initializable, ApplicationListener<ApplicationEvent> {

    public BorderPane root;

    public ProgressBar progressBar;

    public TreeView<Object> warningsTree;
    public Button buttonExpandAll;
    public Button buttonCollapseAll;
    public CheckBox checkBoxIgnoreMissingNfo;

    private Stage stage;
    public StartDirectoryComponentController startDirectoryComponentController;
    public GoCancelButtonsComponentController goCancelButtonsComponentController;
    private final KodiService service;

    private TreeItem<Object> treeRoot;

    private FilteredList<TreeItem<Object>> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Kodi Tools");
        goCancelButtonsComponentController.buttonGo.disableProperty().bind(service.runningProperty().or(startDirectoryComponentController.readyProperty().not()));
        goCancelButtonsComponentController.buttonCancel.disableProperty().bind(service.runningProperty().not());
        goCancelButtonsComponentController.setButtonCancelActionEventFactory( KodiToolsButtonCancelEvent::new);
        goCancelButtonsComponentController.setButtonGoActionEventFactory(KodiToolsButtonGoEvent::new);
        progressBar.visibleProperty().bind(service.runningProperty());
        treeRoot = new TreeItem<>();
        warningsTree.setRoot(treeRoot);
        buttonExpandAll.setDisable(true);
        treeRoot.getChildren().addListener((ListChangeListener<? super TreeItem<Object>>) e -> {
                buttonExpandAll.setDisable(e.getList().isEmpty());
                buttonCollapseAll.setDisable(e.getList().isEmpty());
        });
        this.filteredList = new FilteredList<>(FXCollections.observableArrayList());
        filteredList.predicateProperty().bind(Bindings.createObjectBinding(this::func, checkBoxIgnoreMissingNfo.selectedProperty()));
    }

    private Predicate<? super Object> func() {
        return e -> {
            if(e instanceof KodiCheckResultElementNfoFile){
                return !KodiCheckResultElementNfoFile.NfoFile.NO_FILE.equals(((KodiCheckResultElementNfoFile) e).getNfoFile());
            }
            return true;
        };
    }

    public void show() {
        stage.show();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.debug("Event received: {}", event);
        if(event instanceof KodiToolsButtonGoEvent){
            startService();
        } else if(event instanceof KodiToolsButtonCancelEvent){
            cancelService();
        }
    }

    private void startService() {
        log.debug("Starting service {}" ,service);
        service.reset();
        service.setDirectory(startDirectoryComponentController.getInputPath());
        progressBar.progressProperty().bind(service.progressProperty());
        service.setOnSucceeded(this::handleResult);
        service.setOnFailed(this::handleFailed);
        service.setOnCancelled(this::handleResult);
        treeRoot.getChildren().clear();
        service.start();
    }

    private void handleResult(WorkerStateEvent workerStateEvent) {
        if(service.getValue() != null){
            log.info("Result: {}", service.getValue().getElements().values().stream().filter(this::filterResults).map(Object::toString).collect(Collectors.joining("\n")));
//            this.filteredList.addAll(transform(service.getValue()));
            this.filteredList = new FilteredList<>(FXCollections.observableList(transform(service.getValue())));
            filteredList.predicateProperty().bind(Bindings.createObjectBinding(this::func, checkBoxIgnoreMissingNfo.selectedProperty()));
            treeRoot.getChildren().addAll(filteredList);
        }
      else {
          log.info("Got no result. Cancelled?");
        }
    }

    private boolean filterResults(KodiCheckResultElement kodiCheckResultElement) {
        if(kodiCheckResultElement instanceof KodiCheckResultElementNfoFile){
            return KodiCheckResultElementNfoFile.NfoFile.NO_FILE.equals(((KodiCheckResultElementNfoFile) kodiCheckResultElement).getNfoFile());
        }
        return true;
    }

    private List<TreeItem<Object>> transform(KodiCheckResult taskResult) {
        List<TreeItem<Object>> result2 = new ArrayList<>();
        for(Map.Entry<String, KodiCheckResultElement> e : taskResult.getElements().entrySet()){
            TreeItem<Object> item = new TreeItem<>(e.getKey());
            item.getChildren().addAll(transformChildren(e.getValue()));
            result2.add(item);
        }
        return result2;
    }

    private List<TreeItem<Object>> transformChildren(KodiCheckResultElement value) {
        List<TreeItem<Object>> result = new ArrayList<>();
        if(value instanceof KodiCheckResultElementNfoFile){
            TreeItem rootItem = new TreeItem("NFO file");
            rootItem.getChildren().add(new TreeItem<>(value));
            result.add(rootItem);
        } else if(value instanceof KodiCheckResultElementSubDirs){
            TreeItem rootItem = new TreeItem("Sub directories");
            for(Path subdirResult : ((KodiCheckResultElementSubDirs) value).getSubdirs().getSubdirs()){
                rootItem.getChildren().add(new TreeItem<>(subdirResult));
            }
            result.add(rootItem);
        }
        return result;
    }

    private void handleFailed(WorkerStateEvent e) {
        log.error("Service failed: {}", service.getException(), service.getException());
    }

    private void cancelService(){
        service.cancel();
    }

    public void handleButtonExpandAll(ActionEvent actionEvent) {
        expandTreeView(warningsTree.getRoot(), true);
    }

    private void expandTreeView(TreeItem<?> item, boolean expand){
        if(item != null && !item.isLeaf()){
            item.setExpanded(expand);
            for(TreeItem<?> child:item.getChildren()){
                expandTreeView(child, expand);
            }
        }
    }

    public void handleButtonCollapseAll(ActionEvent actionEvent) {
        expandTreeView(warningsTree.getRoot(), false);
    }
}
