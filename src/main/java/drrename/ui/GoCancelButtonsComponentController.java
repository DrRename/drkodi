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

import drrename.event.JavaFXActionEventFactory;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
@Scope("prototype")
@FxmlView("/fxml/GoCancelButtonsComponent.fxml")
public class GoCancelButtonsComponentController implements Initializable, ApplicationListener<ApplicationEvent> {

    private final ConfigurableApplicationContext applicationContext;

    public Button buttonGo;
    public Button buttonCancel;

    private JavaFXActionEventFactory buttonGoActionEventFactory;

    private JavaFXActionEventFactory buttonCancelActionEventFactory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonGo.setDefaultButton(true);
        buttonGo.setDisable(true);

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    public void setButtonCancelActionEventFactory(JavaFXActionEventFactory buttonCancelActionEventFactory) {
        this.buttonCancelActionEventFactory = buttonCancelActionEventFactory;
    }

    public void setButtonGoActionEventFactory(JavaFXActionEventFactory buttonGoActionEventFactory) {
        this.buttonGoActionEventFactory = buttonGoActionEventFactory;
    }

    public void handleDummyFileCreatorButtonGo(ActionEvent actionEvent) {
        applicationContext.publishEvent(buttonGoActionEventFactory.buildEvent(actionEvent));
    }

    public void handleDummyFileCreatorButtonCancel(ActionEvent actionEvent) {
        applicationContext.publishEvent(buttonCancelActionEventFactory.buildEvent(actionEvent));
    }
}
