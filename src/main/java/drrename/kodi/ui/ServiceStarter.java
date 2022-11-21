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


import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class ServiceStarter<S extends Service<?>> {

    private final S service;

    public void startService(){
        if(checkPreConditions()){
            log.debug("Preparing UI");
            prepareUi();
            log.debug("Init Service {}", service);
            initService(service);
            log.debug("Restarting Service {}", service);
            service.restart();
        } else {
            log.warn("Cannot start, pre conditions failed");
        }
    }

    protected final void initService(S service){
        service.setOnFailed(this::handleFailed);
        service.setOnSucceeded(this::onSucceeded);
        service.setOnCancelled(this::onCancelled);
        doInitService(service);
    }

    protected abstract  void onCancelled(WorkerStateEvent workerStateEvent);

    protected abstract void onSucceeded(WorkerStateEvent workerStateEvent);

    private void handleFailed(WorkerStateEvent e) {
        log.error("{} failed with reason {}", e.getSource(), e.getSource().getException());
    }

    protected abstract void doInitService(S service);

    protected abstract void prepareUi();

    protected abstract boolean checkPreConditions();
}
