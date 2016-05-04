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

import helper.IRobot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author SpecOp0
 */
public class Main {

    private static final KeePassTree tree = new KeePassTree("root");
    private static final KeePassTableModel tableModel = new KeePassTableModel();
    private static final KeePassGUI gui = new KeePassGUI("KeePass 2", tree, tableModel);
    private static final KeePassController controller = new KeePassController(gui, tree);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // gui actions initiated from controller (disable buttons, copy selected data)
        // [directly with saved gui object]
        // update tree if new database loaded
        getController().addListener(DatabaseChangedListener.class, getTree());
        // update table if group/entry in tree is selected
        getTree().addTreeSelectionListener(getController());
        getController().addListener(SelectionChangedListener.class, getTableModel());
        // update gui if table is changed
        getTableModel().addTableModelListener(getGui());
        // double click event for tree and table
        getGui().getDataTable().addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if (e.getClickCount() == 2) {
                            e.consume();
                            getController().showTableSelect();
                        }
                        break;
                    case MouseEvent.BUTTON3:
                        //todo menu for table entries
                        break;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        getTree().addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if (e.getClickCount() == 2) {
                            e.consume();
                            getController().showTreeSelect(e);
                        }
                        break;
                    case MouseEvent.BUTTON3:
                        //todo menu for groups
                        break;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        for (ActionButton button : getGui().getTopPanelButtons()) {
            switch (button.getType()) {
                case OPEN:
                    button.addActionListener((ActionEvent e) -> getController().open());
                    break;
                case SAVE:
                    button.addActionListener((ActionEvent e) -> getController().save());
                    break;
                case ADD:
                    button.addActionListener((ActionEvent e) -> getController().add());
                    break;
                case SHOW:
                    button.addActionListener((ActionEvent e) -> getController().showTableSelect());
                    break;
                case DELETE:
                    button.addActionListener((ActionEvent e) -> getController().deleteEntry());
                    break;
                case COPY_USER:
                    button.addActionListener((ActionEvent e) -> getController().copyUsername());
                    break;
                case COPY_PW:
                    button.addActionListener((ActionEvent e) -> getController().copyPassword());
                    break;
                case LOCK:
                    button.addActionListener((ActionEvent e) -> getController().lock());
                    break;
                case SEARCH:
                    button.addActionListener((ActionEvent e) -> getController().search());
                    break;
                case EXIT:
                    button.addActionListener((ActionEvent e) -> getController().exit());
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
        // add key actions for tree on the left and table on the right
        getGui().getTreePane().registerKeyboardAction((ActionEvent e) -> getController().showTreeSelect(e), IRobot.STROKE_ENTER, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        getGui().getDataTable().registerKeyboardAction((ActionEvent e) -> getController().copyPassword(), IRobot.STROKE_CTRL_AND_COPY, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        getGui().getDataTable().registerKeyboardAction((ActionEvent e) -> getController().showTableSelect(), IRobot.STROKE_ENTER, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // add actions to menu
        for (JMenuItemType menuItem : getGui().getMenuItems()) {
            switch (menuItem.getType()) {
                case NEW:
                    break;
                case OPEN:
                    menuItem.addActionListener((ActionEvent e) -> getController().open());
                    break;
                case OPEN_RECENT:
                    break;
                case CLOSE:
                    menuItem.addActionListener((ActionEvent e) -> getController().lock());
                    break;
                case SAVE:
                    menuItem.addActionListener((ActionEvent e) -> getController().save());
                    break;
                case SAVE_AS:
                    break;
                case DATABASE_SETTINGS:
                    break;
                case CHANGE_MASTER_KEY:
                    break;
                case PRINT:
                    break;
                case IMPORT:
                    break;
                case EXPORT:
                    break;
                case SYNCHRONIZE:
                    break;
                case LOCK_WORKSPACE:
                    menuItem.addActionListener((ActionEvent e) -> getController().lock());
                    break;
                case EXIT:
                    menuItem.addActionListener((ActionEvent e) -> getController().exit());
                    break;
                case ADD_GROUP:
                    break;
                case EDIT_GROUP:
                    menuItem.addActionListener((ActionEvent e) -> getController().showTreeSelect());
                    break;
                case DELETE_GROUP:
                    break;
                case ADD_ENTRY:
                    menuItem.addActionListener((ActionEvent e) -> getController().add());
                    break;
                case EDIT_ENTRY:
                    menuItem.addActionListener((ActionEvent e) -> getController().showTreeSelect());
                    break;
                case DUPLICATE_ENTRY:
                    break;
                case DELETE_ENTRY:
                    break;
                case SELECT_ALL:
                    break;
                case SHOW_ALL_ENTRIES:
                    menuItem.addActionListener((ActionEvent e) -> getController().search(""));
                    break;
                case SHOW_ALL_EXPIRED_ENTIRES:
                    break;
                case SHOW_ENTRIES_BY_TAG:
                    break;
                case FIND:
                    break;
                case HELP_CONTENTS:
                    break;
                case HELP_SOURCE:
                    break;
                case KEEPASS_WEBSITE:
                    menuItem.addActionListener((ActionEvent e) -> KeePassController.openKeePassWebpage());
                    break;
                case DONATE:
                    break;
                case CHECK_FOR_UPDATES:
                    break;
                case ABOUT_KEEPASS:
                     menuItem.addActionListener((ActionEvent e) -> getController().showAboutDialog());
                    break;
                default:
                    throw new AssertionError(menuItem.getType().name());
            }
        }

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
