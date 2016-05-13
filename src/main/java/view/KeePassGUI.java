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
package view;

import enums.ActionType;
import enums.MenuItemType;
import enums.MenuType;
import helper.ActionTypeHelper;
import helper.MenuTypeHelper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.TextField;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import model.DatabaseObject;
import model.KeePassTableModel;

/**
 *
 * @author SpecOp0
 */
public class KeePassGUI extends JFrame implements TableModelListener {

    private static final long serialVersionUID = 1L;

    private final List<ActionButton> topPanelButtons = new ArrayList<>();
    private final TextField searchField;
    private final JScrollPane treePane;
    private final JScrollPane dataPane;

    private final List<JMenuType> menuIndex = new ArrayList<>();
    private final List<JMenuItemType> menuItems = new ArrayList<>();

    private final JTable dataTable;

    public KeePassGUI(String title, JTree tree, TableModel tableModel) throws HeadlessException {
        super(title);
        this.setLayout(new BorderLayout());

        // =========================
        // top menu bar
        JMenuBar menuBar = new JMenuBar();
        for (MenuType type : MenuType.values()) {
            JMenuType menu = new JMenuType(type);
            menuIndex.add(menu);
            menuBar.add(menu);
        }
        for (MenuItemType itemType : MenuItemType.values()) {
            MenuType menuType = MenuTypeHelper.getMenu(itemType);
            if (MenuTypeHelper.isImplemented(itemType)) {
                for (JMenuType menu : menuIndex) {
                    if (menu.getType() == menuType) {
                        JMenuItemType menuItem = MenuTypeHelper.getMenuItem(itemType);
                        menu.add(menuItem);
                        menuItems.add(menuItem);
                        if (MenuTypeHelper.isSeperatorAfterward(itemType)) {
                            menu.addSeparator();
                        }
                        break;
                    }
                }
            }
        }

        // =========================
        // top tool bar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        Dimension preferredDimension = new Dimension(32, 32);
        searchField = new TextField();
        for (ActionType type : ActionType.values()) {
            ActionButton button = ActionTypeHelper.getButton(type);
            button.setPreferredSize(preferredDimension);
            if (type == ActionType.SEARCH) {
                toolbar.add(searchField);
            }
            toolbar.add(button);
            topPanelButtons.add(button);
            if (ActionTypeHelper.isSeperatorAfterwards(type)) {
                toolbar.addSeparator();
            }
        }
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.PAGE_START);
        topPanel.add(toolbar, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.PAGE_START);

        // =========================
        // main window
        // main window entry tree
        DefaultTreeCellRenderer renderer = new KeePassTreeRenderer();
        tree.setCellRenderer(renderer);
        treePane = new JScrollPane(tree);

        // main window data table
        dataTable = new JTable(tableModel);
        dataTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setAutoCreateRowSorter(true);
        dataPane = new JScrollPane(dataTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                treePane, dataPane);
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerLocation(300);
        this.add(splitPane, BorderLayout.CENTER);

        // frame settings
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    public List<JMenuType> getMenuIndex() {
        return menuIndex;
    }

    public List<JMenuItemType> getMenuItems() {
        return menuItems;
    }

    public void setEnabledAllButtons(boolean enabled) {
        getTopPanelButtons().stream().filter((ActionButton button) -> !ActionTypeHelper.isAlwaysActive(button)).forEach((ActionButton button) -> {
            button.setEnabled(enabled);
        });
    }

    public static File chooseFile(Component parentComponent, boolean isOpen) {
        return chooseFile(parentComponent, ".", isOpen);
    }

    public static File chooseFile(Component parentComponent, String path, boolean isOpen) {
        JFileChooser chooser = new JFileChooser(path);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("KeePass Database", "kdb", "kdbx");
        chooser.setFileFilter(filter);
        int returnValueFile;
        if (isOpen) {
            returnValueFile = chooser.showOpenDialog(parentComponent);
        } else {
            returnValueFile = chooser.showSaveDialog(parentComponent);
        }
        if (returnValueFile == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsoluteFile();
        }
        return null;
    }

    public static byte[] enterPassword(Component parentComponent) {
        JPasswordField passwordField = new JPasswordField();
        int returnValuePassword = JOptionPane.showConfirmDialog(parentComponent, passwordField, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (returnValuePassword == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword()).getBytes();
        }
        return null;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (getDataTable().getModel().getRowCount() > 0) {
            SwingUtilities.invokeLater(() -> {
                getDataTable().setRowSelectionInterval(0, 0);
            });
        }
        getDataTable().getParent().revalidate();
        getDataTable().getParent().repaint();
    }

    public void showWarning(String title, String message) {
        JOptionPane.showConfirmDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void showWarning(String title, String message, Component parentComponent) {
        JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static boolean showCancelDialog(String title, String message, Component parentComponent) {
        int returnValue = JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
        return returnValue != JOptionPane.YES_OPTION;
    }

    public DatabaseObject getSelectedObject() {
        // only use of KeePass model variants
        KeePassTableModel model = (KeePassTableModel) getDataTable().getModel();
        DatabaseObject object = model.getDatabaseObject(getDataTable().getSelectedRow());
        return object;
    }

    public DatabaseObject getAndSelectObjectAt(Point point) {
        // only use of KeePass model variants
        int rowIndex = getDataTable().rowAtPoint(point);
        SwingUtilities.invokeLater(() -> {
            getDataTable().getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        });
        KeePassTableModel model = (KeePassTableModel) getDataTable().getModel();
        return model.get(rowIndex);
    }

    public List<ActionButton> getTopPanelButtons() {
        return topPanelButtons;
    }

    public JTable getDataTable() {
        return dataTable;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public JScrollPane getTreePane() {
        return treePane;
    }

    public JScrollPane getDataPane() {
        return dataPane;
    }

}
