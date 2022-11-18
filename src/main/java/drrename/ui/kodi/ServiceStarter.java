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

package drrename.ui.kodi;


import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
            if(service.getState().equals(Worker.State.READY)){
                doStartService();
            }
            else if(service.getState().equals(Worker.State.SCHEDULED) || service.getState().equals(Worker.State.RUNNING)){
                log.debug("Scheduled or running, cancelling service {}", service);
                cancelResetAndStart();
            }
            else {
                resetAndStart();
            }
        } else {
            log.warn("Cannot start, pre conditions failed");
        }
    }

    private void cancelResetAndStart() {
        service.setOnCancelled(event -> resetAndStart());
        service.cancel();
    }

    private void resetAndStart() {
        log.debug("Resetting service {}", service);
        service.setOnReady(event -> doStartService());
        service.reset();
    }

    private void doStartService() {
        log.debug("Init service {}", service);
        initService(service);
        log.debug("Starting service {}", service);
        service.start();
    }

    protected final void initService(S service){
        service.setOnFailed(this::handleFailed);
        service.setOnSucceeded(this::onSucceeded);
        doInitService(service);
    }

    protected abstract void onSucceeded(WorkerStateEvent workerStateEvent);

    private void handleFailed(WorkerStateEvent e) {
            log.error("Service {} failed with exception {}", service, service.getException());
    }

    protected abstract void doInitService(S service);

    protected abstract void prepareUi();

    protected abstract boolean checkPreConditions();
}
