package drrename.kodi;

import drrename.ui.FilterableTreeItem;
import drrename.ui.mainview.GoCancelButtonsComponentController;
import drrename.ui.mainview.StartDirectoryComponentController;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
@FxmlView("/fxml/KodiTools.fxml")
public class KodiToolsController implements Initializable {

    public BorderPane root;

    public ProgressBar progressBar;

    public TreeView<Object> warningsTree;

    public Button buttonExpandAll;

    public Button buttonCollapseAll;

    public CheckBox checkBoxIgnoreMissingNfo;

    public CheckBox checkBoxHideEmpty;

    private Stage stage;

    public StartDirectoryComponentController startDirectoryComponentController;

    public GoCancelButtonsComponentController goCancelButtonsComponentController;

    private final KodiService service;

    private FilterableTreeItem<Object> treeRoot;

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
        treeRoot = new FilterableTreeItem<>("Movies"){
            @Override
            protected Observable[] getCallback() {
                return super.getCallback();
            }
        };
        warningsTree.setRoot(treeRoot);
        buttonExpandAll.setDisable(true);
        buttonCollapseAll.setDisable(true);
        treeRoot.getChildren().addListener((ListChangeListener<? super TreeItem<Object>>) e -> {
                buttonExpandAll.setDisable(e.getList().isEmpty());
                buttonCollapseAll.setDisable(e.getList().isEmpty());
        });
        checkBoxHideEmpty.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateTreeRootPredicate();
            }
        });

    }

    private void updateTreeRootPredicate() {
        treeRoot.setPredicate(buildHideEmptyPredicate());

    }

    private static Predicate<Object> buildHideEmptyPredicate(){
        return new Predicate<Object>() {
            @Override
            public boolean test(Object item) {
                if(item instanceof TreeItem<?>){
                    var children = ((TreeItem<?>) item).getChildren();
                } else if(item instanceof KodiCheckResultElementSubDirs){
                    var subdirs = ((KodiCheckResultElementSubDirs) item).getSuggestion().getSubdirs();
                    if(subdirs.isEmpty()){
                        return false;
                    } else {
                        var wait = 0;
                    }

                }
                return true;
            }
        };
    }

    public void show() {
        stage.show();
    }

    @EventListener
    public void onButtonGoEvent(KodiToolsButtonGoEvent event){
        startService();
    }

    @EventListener
    public void onButtonCancelEvent(KodiToolsButtonCancelEvent event){
        cancelService();
    }

    private void startService() {
        log.debug("Starting service {}" ,service);
        service.reset();
        service.setDirectory(startDirectoryComponentController.getInputPath());
        progressBar.progressProperty().bind(service.progressProperty());
        service.setOnSucceeded(this::handleResult);
        service.setOnFailed(this::handleFailed);
        service.setOnCancelled(this::handleResult);
        treeRoot.getSourceChildren().clear();
        service.start();
    }

    private void handleResult(WorkerStateEvent workerStateEvent) {
        if(service.getValue() != null){
            log.info("Result: {}", service.getValue().getElements().values().stream().map(Object::toString).collect(Collectors.joining("\n")));
            treeRoot.getSourceChildren().addAll(transform(service.getValue()));
        }
      else {
          log.info("Got no result. Cancelled?");
        }
    }

    private List<FilterableTreeItem<Object>> transform(KodiCheckResult taskResult) {
        List<FilterableTreeItem<Object>> result2 = new ArrayList<>();
        for(Map.Entry<KodiCheckResult.Type, Map<String, KodiCheckResultElement>> e : taskResult.getElements().entrySet()){
            FilterableTreeItem<Object> root = new FilterableTreeItem<>(e.getKey());
            root.getSourceChildren().addAll(transformChildren(e.getValue()));
            result2.add(root);
        }
        return result2;
    }

    private List<FilterableTreeItem<Object>> transformChildren(Map<String, KodiCheckResultElement> value) {
        List<FilterableTreeItem<Object>> result = new ArrayList<>();
        for(Map.Entry<String, KodiCheckResultElement> e : value.entrySet()){
            FilterableTreeItem<Object> root = new FilterableTreeItem<>(e.getKey());
            root.getSourceChildren().addAll(transformChildren2(e.getValue()));
            result.add(root);
        }
        return result;
    }

    private List<FilterableTreeItem<Object>> transformChildren2(KodiCheckResultElement value) {
        List<FilterableTreeItem<Object>> result = new ArrayList<>();
        result.add(new FilterableTreeItem<>(value.getSuggestion()));
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
