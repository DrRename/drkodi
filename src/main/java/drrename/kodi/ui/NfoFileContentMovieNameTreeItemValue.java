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

package drrename.kodi.ui;

import drrename.kodi.FixFailedException;
import drrename.kodi.NfoFileNameCheckResult;
import drrename.kodi.WarningsConfig;
import drrename.kodi.nfo.NfoContentTitleChecker;
import drrename.kodi.nfo.NfoFileParser;
import drrename.RenamingPath;
import drrename.kodi.nfo.NfoFileTitleExtractor;
import drrename.util.DrRenameUtil;
import javafx.scene.Node;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executor;

@Setter
@Getter
@Slf4j
public class NfoFileContentMovieNameTreeItemValue extends KodiTreeItemValue<NfoFileNameCheckResult> {

    private final NfoFileParser nfoFileParser;

    public NfoFileContentMovieNameTreeItemValue(RenamingPath moviePath, Executor executor, WarningsConfig warningsConfig) {
        super(moviePath, executor, warningsConfig);
        nfoFileParser = new NfoFileParser();
    }

    @Override
    public NfoFileNameCheckResult checkStatus() {
        return new NfoContentTitleChecker().checkDir(getRenamingPath().getOldPath());
    }

    protected void updateButtonText(boolean canFix, boolean isWarning) {
        if (isWarning) {
            if(SystemUtils.IS_OS_MAC) {
               buttonText.set("Open in finder");
            } else
                buttonText.set("Fix manually");
        } else {
            buttonText.set("OK");
        }
    }

    protected Node buildGraphic() {
        Button button = (Button) super.buildGraphic();
        if(isWarning() && SystemUtils.IS_OS_MAC) {
        button.disableProperty().unbind();
        button.setDisable(false);
        }
        return button;
    }

    @Override
    public void fix(NfoFileNameCheckResult result) throws FixFailedException {
        DrRenameUtil.runOpenFolderCommandMacOs(result.getNfoFiles().get(0));
    }

    @Override
    public void updateStatus(NfoFileNameCheckResult result) {
        if(result == null){
            return;
        }
        // 'fixable' to 'true', on order for the button to show up
//        setFixable(SystemUtils.IS_OS_MAC);
        setCheckResult(result);
        setWarning(calculateWarning());
    }

    private boolean calculateWarning() {
        if(getCheckResult() == null){
            return false;
        }
//        log.debug("Calculating warning");
        return getCheckResult().getType().isWarning(getWarningsConfig());
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        String additionalInfo = getAdditionalMessageInfo();
        if(additionalInfo != null)
            return getCheckResult().getType() + ": " + getAdditionalMessageInfo();
        return getCheckResult().getType() + ".";
    }

    private String getAdditionalMessageInfo() {
        if(getCheckResult().getType() == null || getCheckResult().getNfoFiles().isEmpty())
            return null;
        var validNfoFiles = getCheckResult().getNfoFiles().stream().map(this::getTitleFromNfoFile).filter(Objects::nonNull).toList();
        if(validNfoFiles.isEmpty()){
            return null;
        }
        return String.join(", ", validNfoFiles);
    }

    String getTitleFromNfoFile(Path nfoFile){
        return new NfoFileTitleExtractor(nfoFileParser).getTitleFromNfoFile(nfoFile);
    }

    @Override
    public String getHelpText() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return "NFO File Content Movie Name";
    }
}
