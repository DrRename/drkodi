/*
 *     Dr.Rename - A Minimalistic Batch Renamer
 *
 *     Copyright (C) 2022
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package drrename.kodi;

import drrename.model.RenamingPath;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class MediaFileNameTreeItemValue extends KodiTreeItemValue<MediaFileNameCheckResult> {

    private final MediaFileNameIssue delegate;

    public MediaFileNameTreeItemValue(RenamingPath path, Executor executor) {
        super(path, executor);
        this.delegate = new MediaFileNameIssue(path);
        triggerStatusCheck();
    }

    @Override
    protected String buildNewMessage(Boolean newValue) {
        if (delegate.getCheckResult().getType() == null) {
            return "unknown";
        }
        return (delegate.getCheckResult().getType().toString()) + (delegate.getCheckResult().getMediaFiles().isEmpty() ? "" : ":\n" + delegate.getCheckResult().getMediaFiles().stream().map(Object::toString).collect(Collectors.joining("\n")));
    }

    private boolean calculateWarning() {
        return delegate.getCheckResult().getType().isWarning();
    }

    @Override
    public void updateStatus(MediaFileNameCheckResult result) {
        delegate.updateStatus(result);
        setWarning(calculateWarning());
    }

    // Delegate //

    @Override
    public String getHelpText() {
        return delegate.getHelpText();
    }

    @Override
    public String getIdentifier() {
        return delegate.getIdentifier();
    }


    @Override
    public MediaFileNameCheckResult checkStatus() {
        return delegate.checkStatus();
    }

    @Override
    public void fix(MediaFileNameCheckResult result) throws FixFailedException {
        delegate.fix(result);
    }

}
