/*
 * Copyright 2015 SpecOp0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package main;

import java.util.EventListener;
import javax.swing.event.TreeSelectionEvent;

/**
 *
 * @author SpecOp0
 */
public interface ControllerListener extends EventListener {
    
    void setEnabledAllButtons(boolean enabled);
    
    void databaseChanged(DatabaseChangedEvent event);
    
    void showData(DatabaseChangedEvent event1, TreeSelectionEvent event2);
    
    void copyUsername();
    
    void copyPassword();
    
}
