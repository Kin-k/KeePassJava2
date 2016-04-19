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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author SpecOp0
 */
public class Main {

    private static final KeePassGUI gui = new KeePassGUI("KeePass 2");
    private static final KeePassController controller = new KeePassController();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        getController().addListener(ControllerListener.class, getGui());
        for (ActionButton button : getGui().getTopPanelButtons()) {
            switch (button.getType()) {
                case OPEN:
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getController().open();
                        }
                    });
                    break;
                case SAVE:
                    break;
                case ADD:
                    break;
                case SHOW:
                    break;
                case DELETE:
                    break;
                case COPY_USER:
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getController().copyUsername();
                        }
                    });
                    break;
                case COPY_PW:
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getController().copyPassword();
                        }
                    });
                    break;
                case LOCK:
                    break;
                case SEARCH:
                    break;
                case EXIT:
                    button.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            getController().exit();
                        }
                    });
                    break;
                default:
                    throw new AssertionError(button.getType().name());

            }
        }
        getGui().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                getController().exit();
            }
        });
        getGui().getTree().addTreeSelectionListener(getController());

        DatabasePath databasePath = new DatabasePath();
        databasePath.load();
        String path = databasePath.getPath();
        if (databasePath.isDatabase()) {
            getController().open(path);
            getController().notifyDatabaseChanged();
        }
    }

    public static KeePassGUI getGui() {
        return gui;
    }

    public static KeePassController getController() {
        return controller;
    }

}
