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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author SpecOp0
 */
public class Main {

    private static final KeePassTree tree = new KeePassTree("root");
    private static final KeePassTableModel tableModel = new KeePassTableModel();
    private static final KeePassController controller = new KeePassController();
    private static final KeePassGUI gui = new KeePassGUI("KeePass 2", tree, tableModel);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // gui actions initiated from controller (disable buttons, copy selected data)
        getController().addListener(ControllerListener.class, getGui());
        // update tree if new database loaded
        getController().addListener(DatabaseChangedListener.class, getTree());
        // update table if group/entry in tree is selected
        getTree().addTreeSelectionListener(getController());
        getController().addListener(SelectionChangedListener.class, getTableModel());
        // update gui if table is changed
        getTableModel().addTableModelListener(getGui());
        
        for (ActionButton button : getGui().getTopPanelButtons()) {
            switch (button.getType()) {
                case OPEN:
                    button.addActionListener((ActionEvent e) -> {
                        getController().open();
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
                    button.addActionListener((ActionEvent e) -> {
                        getController().copyUsername();
                    });
                    break;
                case COPY_PW:
                    button.addActionListener((ActionEvent e) -> {
                        getController().copyPassword();
                    });
                    break;
                case LOCK:
                    button.addActionListener((ActionEvent e) -> {
                        getController().lock();
                    });
                    break;
                case SEARCH:
                    break;
                case EXIT:
                    button.addActionListener((ActionEvent e) -> {
                        getController().exit();
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

    public static KeePassTree getTree() {
        return tree;
    }

    public static KeePassTableModel getTableModel() {
        return tableModel;
    }
}
