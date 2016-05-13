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

import model.DatabaseObject;
import model.KeePassTree;
import view.KeePassShowEntryGUI;
import view.KeePassShowObjectGUI;
import view.KeePassGUI;
import view.KeePassShowGroupGUI;
import listener.SelectionChangedListener;
import listener.DatabaseChangedListener;
import events.DatabaseChangedEvent;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;
import org.linguafranca.pwdb.Visitor;
import org.linguafranca.pwdb.kdbx.KdbxCredentials;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.linguafranca.security.Credentials;
import view.JMenuItemType;
import view.KeePassPopupMenu;

/**
 *
 * @author SpecOp0
 */
public class KeePassController implements TreeSelectionListener, MouseListener {

    private final EventListenerList listeners = new EventListenerList();
    private final KeePassGUI gui;
    private final KeePassTree tree;
    private static Thread passwordReset = null;
    private KeePassShowObjectGUI showObjectGui = null;
    private KeePassPopupMenu popupMenu = null;
    // current database
    private DomDatabaseWrapper database = null;
    private File databaseFile = null;
    private byte[] password = null;

    public KeePassController(KeePassGUI gui, KeePassTree tree) {
        this.gui = gui;
        this.tree = tree;
    }

    public void newDatabase() {
        boolean createNewDatabase = true;
        if (null != getDatabase() && getDatabase().isDirty()) {
            createNewDatabase = !KeePassGUI.showCancelDialog("Create new Database without Saving", "There are unsaved changes in the database. If you create a new Database all unsaved data will be lost." + System.lineSeparator() + "Do you really want to create a new Database?", getGui());
        }
        if (createNewDatabase) {
            SwingUtilities.invokeLater(() -> {
                try {
                    setDatabase(new DomDatabaseWrapper());
                    setPassword(null);
                    setDatabaseFile(null);

                    notifyDatabaseChanged();
                    // clear shown entries (data model)
                    updateTableData(new ArrayList<>());
                } catch (IOException ex) {
                    Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
                    KeePassGUI.showWarning("Error creating Database", "An error occured while creating a new Database.", getGui());
                }
            });
        }
    }

    public void lock() {
        // clear database (tree)
        setDatabase(null);
        setPassword(null);
        notifyDatabaseChanged();
        // clear shown entries (data model)
        updateTableData(new ArrayList<>());
    }

    public void open() {
        // open file chooser to determine file
        open(KeePassGUI.chooseFile(getGui(), getDefaultDatabasePath(), true));
    }

    public void open(String path) {
        open(new File(path));
    }

    private void open(final File file) {
        if (null != file) {
            // enter password
            setPassword(KeePassGUI.enterPassword(getGui()));
            if (null != getPassword()) {
                setEnabled(false);
                // start worker
                Thread worker = new Thread() {

                    @Override
                    public void run() {
                        // heavy computing (decrypt database)
                        open(file, getPassword());
                        SwingUtilities.invokeLater(() -> {
                            setEnabled(true);
                            if (null != getDatabase()) {
                                notifyDatabaseChanged();
                                // save path to file
                                setDatabaseFile(file);
                            }
                        });
                    }
                };
                worker.start();
            }
        }
    }

    private void open(File file, byte[] password) {
        InputStream decryptedInputStream = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            Credentials credentials = new KdbxCredentials.Password(password);
            setDatabase(DomDatabaseWrapper.load(credentials, inputStream));
        } catch (IllegalStateException | IOException ex) {
            Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
            String title = "Read error";
            String message = String.format("File (%s) corrupt or wrong format.", file.getName());
            switch (ex.getMessage()) {
                case "File version did not match":
                    message = String.format("File version (%s) did not match.", file.getName());
                    break;
                case "Inconsistent stream bytes":
                    message = String.format("Inconsistent stream bytes while reading File (%s)." + System.lineSeparator() + "Wrong password?", file.getName());
                    break;
            }
            getGui().showWarning(title, message);
        } finally {
            try {
                if (null != decryptedInputStream) {
                    decryptedInputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getDefaultDatabasePath() {
        DatabasePath databasePath = new DatabasePath();
        databasePath.load();
        String path = ".";
        if (databasePath.isDatabase()) {
            String directPath = databasePath.getPath();
            int lastSlash = directPath.lastIndexOf('\\');
            if (lastSlash == -1) {
                lastSlash = directPath.lastIndexOf('/');
            }
            if (lastSlash != -1) {
                path = directPath.substring(0, lastSlash);
            }
        }
        return path;
    }

    private Group getGroupOfSelectedObject() {
        Group group = null;
        if (null != getDatabase()) {
            DatabaseObject object = getTree().getSelectedObject();
            if (null != object) {
                if (object.isEntry()) {
                    group = object.getEntry().getParent();
                } else if (object.isGroup()) {
                    group = object.getGroup();
                } else {
                    getGui().showWarning("Adding Failed", "Error while preparing to add an object. The selected group could not be found.");
                }
            } else {
                group = getDatabase().getRootGroup();
            }
        }
        return group;
    }

    public void addEntry() {
        Group group = getGroupOfSelectedObject();
        if (null != group) {
            addEntryToGroup(group);
        }
    }

    public void addEntryToGroup(Group group) {
        if (null != getDatabase() && null == getShowObjectGui() && null != group) {
            setShowObjectGui(new KeePassShowEntryGUI(getGui()));
            getShowObjectGui().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent event) {
                    if (getShowObjectGui().isSaveObject()) {
                        DatabaseObject newEntry = new DatabaseObject(group.addEntry(getDatabase().newEntry()));
                        getShowObjectGui().saveInputToObject(newEntry);
                        notifyDatabaseChanged(newEntry);
                        updateTableData(new DatabaseObject(group));
                    }
                    setShowObjectGui(null);
                }
            });
        } else {
            getGui().showWarning("Entry/Group already shown", "Another Entry or Group window is open. You need to close that window before showing or editing another entry.");
        }
    }

    public void addGroup() {
        Group group = getGroupOfSelectedObject();
        if (null != group) {
            addGroupToGroup(group);
        }
    }

    public void addGroupToGroup(Group group) {
        if (null != getDatabase() && null == getShowObjectGui()) {
            setShowObjectGui(new KeePassShowGroupGUI(getGui()));
            getShowObjectGui().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent event) {
                    if (getShowObjectGui().isSaveObject()) {
                        DatabaseObject newEntry = new DatabaseObject(group.addGroup(getDatabase().newGroup()));
                        getShowObjectGui().saveInputToObject(newEntry);
                        notifyDatabaseChanged(newEntry);
                        updateTableData(newEntry);
                    }
                    setShowObjectGui(null);
                }
            });
        } else {
            getGui().showWarning("Entry/Group already shown", "Another Entry or Group window is open. You need to close that window before showing or editing another group.");
        }
    }

    public void save() {
        if (isDatabaseLoadedOrGUIError()) {
            if (null != getDatabaseFile()) {
                save(getDatabaseFile());
            } else {
                saveAs();
            }
        }
    }

    public void saveAs() {
        if (isDatabaseLoadedOrGUIError()) {
            File selectedFile = KeePassGUI.chooseFile(getGui(), getDefaultDatabasePath(), false);
            if (selectedFile != null) {
                System.out.println("selected+" + selectedFile.getAbsolutePath());
                save(selectedFile);
            } else {
                System.out.println("errörrr");
            }
        }
    }

    private void save(File databaseFile) {
        if (null != databaseFile) {
            try {
                // auto add extension
                String validExtension = ".kdbx";
                if (!databaseFile.getName().toLowerCase().endsWith(validExtension)) {
                    databaseFile = new File(databaseFile.getAbsolutePath() + validExtension);
                }
                // if file exists ask for overwrite
                boolean overwrite = true;
                if (databaseFile.exists()) {
                    overwrite = !KeePassGUI.showCancelDialog("Overwrite file?", "A file '" + databaseFile.getName() + "' already exists. Overwrite saved Database?", getGui());
                }
                if (overwrite) {
                    if (!databaseFile.exists()) {
                        databaseFile.createNewFile();
                    }
                    FileOutputStream outputStream = new FileOutputStream(databaseFile);
                    boolean validPassword = null != getPassword();
                    if (!validPassword) {
                        setPassword(KeePassGUI.enterPassword(getGui()));
                        validPassword = null != getPassword();
                    }
                    if (validPassword) {
                        Credentials credentials = new KdbxCredentials.Password(getPassword());
                        getDatabase().save(credentials, outputStream);
                        setDatabaseFile(databaseFile);
                        getGui().showWarning("Database saved", "Database was saved sucessfully to " + System.lineSeparator() + databaseFile.getAbsolutePath());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
                getGui().showWarning("Error while saving Database", "An Error occured while saving Database to " + System.lineSeparator() + databaseFile.getAbsolutePath() + System.lineSeparator() + "Try saving again or all changes will be lost!");
            }
        } else {
            getGui().showWarning("Database save error", "Cannot save Database, because no valid file is selected.");
            saveAs();
        }
    }

    private boolean isDatabaseLoadedOrGUIError() {
        boolean isDatabaseLoaded = null != getDatabase();
        if (!isDatabaseLoaded) {
            getGui().showWarning("No Database loaded", "Cannot save Database, because no Database is loaded.");
        }
        return isDatabaseLoaded;
    }

    public void showTableSelect() {
        if (null != getDatabase()) {
            DatabaseObject object = getGui().getSelectedObject();
            if (null != object && object.isEntry()) {
                show(object);
            } else {
                getGui().showWarning("Show / Edit Entry error", "No Entry in table selected (right-hand side).");
            }
        }
    }

    public void showTreeSelect() {
        showTreeSelect(getTree());
    }

    public void showTreeSelect(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        showTreeSelect(tree);
    }

    public void showTreeSelect(ActionEvent e) {
        showTreeSelect(getTree());
    }

    private void showTreeSelect(JTree tree) {
        DatabaseObject object = KeePassTree.getSelectedObject(tree);
        if (null != object && (object.isEntry() || object.isGroup())) {
            show(object);
        }
    }

    private void show(DatabaseObject object) {
        if (null == getShowObjectGui() && null != object) {
            if (object.isEntry()) {
                setShowObjectGui(new KeePassShowEntryGUI(getGui(), object.getEntry()));
            } else if (object.isGroup()) {
                setShowObjectGui(new KeePassShowGroupGUI(getGui(), object.getGroup()));
            } else {
                getGui().showWarning("Error while showing Data", "Try to show Entry or Group, but could not find either.");
                throw new IllegalArgumentException("Error while showing Data: Try to show Entry or Group, but could not find either.");
            }
            getShowObjectGui().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent event) {
                    if (getShowObjectGui().isSaveObject()) {
                        getShowObjectGui().saveInputToObject(object);
                        notifyDatabaseChanged();
                    }
                    setShowObjectGui(null);
                }
            });
        } else {
            getGui().showWarning("Entry already shown", "Another Entry window is open. You need to close that window before showing or editing another entry.");
        }
    }

    public void copyUsername() {
        copy(true);
    }

    public synchronized void copyPassword() {
        setPasswordReset(new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    if (null == getPasswordReset() || getPasswordReset() == this) {
                        copyToClipboard("");
                        setPasswordReset(null);

                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        copy(false);
        getPasswordReset().start();
    }

    private void copy(boolean isUsername) {
        DatabaseObject object = getGui().getSelectedObject();
        if (null != object && object.isEntry()) {
            String message;
            if (isUsername) {
                message = object.getEntry().getUsername();
            } else {
                message = object.getEntry().getPassword();
            }
            KeePassController.copyToClipboard(message);
        } else {
            getGui().showWarning("Copy to Clipboard Error", "No Entry in table selected (right-hand side).");
        }
    }

    public static void copyToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void deleteEntry() {
        if (null != getDatabase()) {
            DatabaseObject object = getGui().getSelectedObject();
            if (null != object && object.isEntry()) {
                delete(object);
            } else {
                getGui().showWarning("Delete Failed", "No Entry in table selected (right-hand side).");
            }
        }
    }

    public void deleteGroup() {
        if (null != getDatabase()) {
            DatabaseObject object = getTree().getSelectedObject();
            if (null != object && object.isGroup()) {
                delete(object);
            } else {
                getGui().showWarning("Delete Failed", "No Group in tree selected (left-hand side).");
            }
        }
    }

    public void deleteGroupOrEntry() {
        if (null != getDatabase()) {
            DatabaseObject object = getTree().getSelectedObject();
            if (null != object) {
                delete(object);
            } else {
                getGui().showWarning("Delete Failed", "No Entry/Group in tree selected (left-hand side).");
            }
        }
    }

    private void delete(DatabaseObject object) {
        if (null != object) {
            Group parent = null;
            boolean cancelDelete = false;
            if (object.isEntry()) {
                cancelDelete = KeePassGUI.showCancelDialog("Delete Entry " + object.getEntry().getTitle() + "?", "Do you really want to delete the Entry '" + object.getEntry().getTitle() + "'?", getGui());
                if (!cancelDelete) {
                    parent = object.getEntry().getParent();
                    parent.removeEntry(object.getEntry());
                }
            } else if (object.isGroup()) {
                if (!object.getGroup().isRootGroup()) {
                    cancelDelete = KeePassGUI.showCancelDialog("Delete Group " + object.getGroup().getName() + "?", "Do you really want to delete the Group '" + object.getGroup().getName() + "' and all its Entries?", getGui());
                    if (!cancelDelete) {
                        parent = object.getGroup().getParent();
                        parent.removeGroup(object.getGroup());
                    }
                } else {
                    cancelDelete = true;
                    KeePassGUI.showWarning("Cannot delete Root", "The Root group cannot be deleted. You can create a new Database or have to delete the Database file manually.", getGui());
                }
            }
            if (!cancelDelete) {
                if (null != parent) {
                    // no group, because only tree model (right-hand side) is used
                    DatabaseObject parentObject = new DatabaseObject(parent);
                    notifyDatabaseChanged(parentObject);
                    updateTableData(parentObject);
                } else {
                    getGui().showWarning("Error while deleting", "Try to show delete Entry or Group, but could not find either.");
                    throw new IllegalArgumentException("Error while deleting: Given Object is neither Entry or Group");
                }
            }
        }
    }

    public void search() {
        String searchQuery = getGui().getSearchField().getText();
        search(searchQuery);
    }

    public void search(String searchQuery) {
        if (null != getDatabase()) {
            SwingUtilities.invokeLater(() -> {
                final List<Entry> matchingEntries = new ArrayList<>();
                getDatabase().visit(new Visitor() {

                    @Override
                    public void startVisit(Group group) {
                    }

                    @Override
                    public void endVisit(Group group) {
                    }

                    @Override
                    public void visit(Entry entry) {
                        if (entry.match(searchQuery)) {
                            matchingEntries.add(entry);
                        }
                    }

                    @Override
                    public boolean isEntriesFirst() {
                        return false;
                    }
                });
                Collections.sort(matchingEntries, (Entry entry1, Entry entry2) -> entry1.getTitle().toLowerCase().compareTo(entry2.getTitle().toLowerCase()));
                updateTableData(matchingEntries);
            });
        }
    }

    public void exit() {
        if (null != getDatabase() && getDatabase().isDirty()) {
            // abort exit if database changed and user cancels action
            if (KeePassGUI.showCancelDialog("Exit without Saving", "There are unsaved changes in the database. If you exit all unsaved data will be lost." + System.lineSeparator() + "Do you really want to exit?", getGui())) {
                return;
            }
        }
        System.exit(0);
    }

    public void showAboutDialog() {
        // https://stackoverflow.com/questions/8348063/clickable-links-in-joptionpane

        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        // create some css from the label's font
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:").append(font.isBold() ? "bold" : "normal").append(";");
        style.append("font-size:").append(font.getSize()).append("pt;");

        // html content
        String lineSeperator = "<br></br>";
        String message = "KeePassJava2 v 0.0" + lineSeperator + lineSeperator
                + "Database API by jorabin" + lineSeperator
                + "<a href='https://github.com/jorabin/KeePassJava2'>https://github.com/jorabin/KeePassJava2</a>" + lineSeperator
                + "GUI by SpecOp0 (inspired by KeePass2)" + lineSeperator
                + "<a href='https://github.com/specop0/KeePassJava2'>https://github.com/specop0/KeePassJava2</a>" + lineSeperator + lineSeperator
                + "LICENSE: Apache License, Version 2.0" + lineSeperator
                + "<a href='http://www.apache.org/licenses/LICENSE-2.0'>http://www.apache.org/licenses/LICENSE-2.0</a>";
        JEditorPane editorPane = new JEditorPane("text/html", "<html><body style=\"" + style + "\">"
                + message
                + "</body></html>");

        // handle link events
        editorPane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                KeePassController.openWebpage(e.getURL());
            }
        });
        editorPane.setEditable(false);
        editorPane.setBackground(label.getBackground());

        JOptionPane.showMessageDialog(getGui(), editorPane, "About", JOptionPane.PLAIN_MESSAGE, null);
    }

    public static void openKeePassWebpage() {
        try {
            openWebpage(new URI("http://keepass.info/"));
        } catch (URISyntaxException ex) {
        }
    }

    private static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());

        } catch (URISyntaxException ex) {
            Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception ex) {
                Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // ===========================
    // initiate or listen to events
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        DatabaseObject object = KeePassTree.getSelectedObject(tree);
        if (null != getDatabase() && null != object) {
            if (object.isGroup()) {
                updateTableData(object);
            } else {
                updateTableData(new DatabaseObject(object.getEntry().getParent()));
            }
        }
    }

    private void updateTableData(DatabaseObject object) {
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(object);
        }

    }

    private void updateTableData(List<Entry> entries) {
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(entries);
        }
    }

    private void setEnabled(boolean enabled) {
        getGui().setEnabledAllButtons(enabled);
    }

    public void notifyDatabaseChanged() {
        notifyDatabaseChanged(getTree().getSelectedObject());
    }

    public void notifyDatabaseChanged(DatabaseObject objectToShow) {
        DatabaseChangedEvent event = new DatabaseChangedEvent(this, getDatabase(), objectToShow);
        for (DatabaseChangedListener listener : getListeners().getListeners(DatabaseChangedListener.class)) {
            listener.databaseChanged(event);
        }
    }

    public <T extends EventListener> void addListener(Class<T> className, T listener) {
        getListeners().add(className, listener);
    }

    public <T extends EventListener> void removeListener(Class<T> className, T listener) {
        getListeners().remove(className, listener);
    }

    // PopupMenu for Tree and Table
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        disposePopupMenu();
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            if (null != getDatabase() && null != mouseEvent.getComponent()) {
                // check wether tree or table is selected
                DatabaseObject object;
                if (mouseEvent.getComponent() instanceof JTable) {
                    object = getGui().getAndSelectObjectAt(mouseEvent.getPoint());
                } else {
                    object = getTree().getAndSelectObjectAt(mouseEvent.getX(), mouseEvent.getY());
                    // if nothing in tree selected use root group
                    if (null == object) {
                        object = new DatabaseObject(getDatabase().getRootGroup());
                    }
                }
                if (null != object) {
                    setPopupMenu(new KeePassPopupMenu(object, mouseEvent.getLocationOnScreen()));
                    addPopupMenuActionListener(object);
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void addPopupMenuActionListener(DatabaseObject object) {
        for (JMenuItemType menuItem : getPopupMenu().getMenuItems()) {
            switch (menuItem.getType()) {
                case ADD_GROUP:
                    menuItem.addActionListener((ActionEvent e) -> {
                        disposePopupMenu();
                        addGroupToGroup(object.getGroup());
                    });
                    break;
                case EDIT_GROUP:
                    menuItem.addActionListener((ActionEvent e) -> {
                        disposePopupMenu();
                        show(object);
                    });
                    break;
                case DELETE_GROUP:
                    menuItem.addActionListener((ActionEvent e) -> {
                        disposePopupMenu();
                        delete(object);
                    });
                    break;
                case ADD_ENTRY:
                    if (object.isGroup()) {
                        menuItem.addActionListener((ActionEvent e) -> {
                            disposePopupMenu();
                            addEntryToGroup(object.getGroup());
                        });
                    } else {
                        menuItem.addActionListener((ActionEvent e) -> {
                            disposePopupMenu();
                            addEntryToGroup(object.getEntry().getParent());
                        });
                    }
                    break;
                case EDIT_ENTRY:
                    menuItem.addActionListener((ActionEvent e) -> {
                        disposePopupMenu();
                        show(object);
                    });
                    break;
                case DUPLICATE_ENTRY:
                    menuItem.addActionListener((ActionEvent e) -> {
                        disposePopupMenu();
                    });
                    break;
                case DELETE_ENTRY:
                    menuItem.addActionListener((ActionEvent e) -> {
                        disposePopupMenu();
                        delete(object);
                    });
                    break;
            }
        }
    }

    private void disposePopupMenu() {
        if (null != getPopupMenu()) {
            setPopupMenu(getPopupMenu().dispose());
        }
    }

    // getter and setter
    public void setDatabaseFile(File databaseFile) {
        // save new database file to ini file
        if (null != databaseFile) {
            DatabasePath databasePath = new DatabasePath();
            databasePath.setPath(databaseFile.getAbsolutePath());
            databasePath.save();
        }
        this.databaseFile = databaseFile;
    }

    public DomDatabaseWrapper getDatabase() {
        return database;
    }

    public void setDatabase(DomDatabaseWrapper database) {
        this.database = database;
    }

    public EventListenerList getListeners() {
        return listeners;
    }

    public static Thread getPasswordReset() {
        return passwordReset;
    }

    public static void setPasswordReset(Thread aPasswordReset) {
        passwordReset = aPasswordReset;
    }

    public File getDatabaseFile() {
        return databaseFile;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public KeePassGUI getGui() {
        return gui;
    }

    public KeePassShowObjectGUI getShowObjectGui() {
        return showObjectGui;
    }

    public void setShowObjectGui(KeePassShowObjectGUI showObjectGui) {
        this.showObjectGui = showObjectGui;
    }

    public KeePassTree getTree() {
        return tree;
    }

    public KeePassPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(KeePassPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

}
