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
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;
import org.linguafranca.pwdb.Visitor;
import org.linguafranca.pwdb.kdbx.KdbxCredentials;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.linguafranca.security.Credentials;
import view.JMenuItemType;
import view.KeePassPopupMenu;

/**
 * Controller of KeePass 2.
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
    private final Object showObjectGuiLock = new Object();
    private final Object popupMenuLock = new Object();
    // current database
    private DomDatabaseWrapper database = null;
    private File databaseFile = null;
    private byte[] password = null;

    /**
     * Controller of KeePass 2.
     *
     * @param gui view which displays the whole gui
     * @param tree tree structure (part of the gui) which manages the basic
     * database structure
     */
    public KeePassController(KeePassGUI gui, KeePassTree tree) {
        this.gui = gui;
        this.tree = tree;
    }

    /**
     * Creates a new Database (safety dialog).
     */
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

    /**
     * Locks the current Database (currently: deletes all changes and Database
     * has to be reopened).
     */
    public void lock() {
        // clear database (tree)
        setDatabase(null);
        setPassword(null);
        notifyDatabaseChanged();
        // clear shown entries (data model)
        updateTableData(new ArrayList<>());
    }

    /**
     * Opens Database from a File which the User has to choose.
     */
    public void open() {
        // open file chooser to determine file
        open(KeePassGUI.chooseFile(getGui(), getDefaultDatabasePath(), true));
    }

    /**
     * Opens Database from given File.
     *
     * @param path Path to Database file
     */
    public void open(String path) {
        open(new File(path));
    }

    /**
     * Opens Database from given File. Prompts the User for a password.
     *
     * @param file Database file
     */
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

    /**
     * Opens Database from given File and with given Password.
     *
     * @param File Database file
     * @param password Password of Database
     */
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

    /**
     * Looks for a configuration file and sets the default Path when a user is
     * promted for a Database file (if no file is found current directory is
     * choosen).
     */
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

    /**
     * Searches the Group of the selected DatabaseObject in the Tree. If
     * selected DatabaseObject is an Entry, its parent group will be returned.
     *
     * @return Group of selected DatabaseObject
     */
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
                    getGui().showWarning("Group not found", "Error while preparing to add an Entry/Group. The selected group could not be found.");
                }
            } else {
                group = getDatabase().getRootGroup();
            }
        }
        return group;
    }

    /**
     * Adds an Entry to the currently selected Group (creates new GUI element).
     */
    public void addEntry() {
        Group group = getGroupOfSelectedObject();
        if (null != group) {
            addEntryToGroup(group);
        }
    }

    /**
     * Adds an Entry to the given group (creates new GUI element).
     *
     * @param group Group of new Entry
     */
    public void addEntryToGroup(Group group) {
        if (null != getDatabase() && null == getShowObjectGui() && null != group) {
            synchronized (showObjectGuiLock) {
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
            }
        } else {
            getGui().showWarning("Entry/Group already shown", "Another Entry or Group window is open. You need to close that window before showing or editing another entry.");
        }
    }

    /**
     * Adds a Group to the currently selected Group (creates new GUI element).
     */
    public void addGroup() {
        Group group = getGroupOfSelectedObject();
        if (null != group) {
            addGroupToGroup(group);
        }
    }

    /**
     * Adds a Group to the given group (creates new GUI element).
     *
     * @param group Group of new Entry
     */
    public void addGroupToGroup(Group group) {
        if (null != getDatabase() && null == getShowObjectGui()) {
            synchronized (showObjectGuiLock) {
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
            }
        } else {
            getGui().showWarning("Entry/Group already shown", "Another Entry or Group window is open. You need to close that window before showing or editing another group.");
        }
    }

    /**
     * Save currently open Database to a file. If Database was loaded from a
     * file (a Database file exists and is known), then this file is used, else
     * user input is requested.
     */
    public void save() {
        if (isDatabaseLoadedOrGUIError()) {
            if (null != getDatabaseFile()) {
                save(getDatabaseFile());
            } else {
                saveAs();
            }
        }
    }

    /**
     * Save currently open Database to a file. Requests user input for a file.
     */
    public void saveAs() {
        if (isDatabaseLoadedOrGUIError()) {
            File selectedFile = KeePassGUI.chooseFile(getGui(), getDefaultDatabasePath(), false);
            if (selectedFile != null) {
                save(selectedFile);
            }
        }
    }

    /**
     * Save currently open Database to given file.
     *
     * @param databaseFile file to save Database into
     */
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

    /**
     * Tests if a Database is loaded and shows an Error to the user otherwise.
     *
     * @return true of database is loaded
     */
    private boolean isDatabaseLoadedOrGUIError() {
        boolean isDatabaseLoaded = null != getDatabase();
        if (!isDatabaseLoaded) {
            getGui().showWarning("No Database loaded", "Cannot save Database, because no Database is loaded.");
        }
        return isDatabaseLoaded;
    }

    /**
     * Shows the selected Element (Entry) in the Table (creates new GUI
     * element).
     */
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

    /**
     * Shows the selected Element (Group or Entry) of the Tree (creates new GUI
     * element).
     */
    public void showTreeSelect() {
        showTreeSelect(getTree());
    }

    /**
     * Shows the selected Element (Group or Entry) of a Tree given by the
     * MouseEvent (creates new GUI element).
     *
     * @param e MouseEvent in which a Tree is selected
     */
    public void showTreeSelect(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        showTreeSelect(tree);
    }

    /**
     * Shows the selected Element (Group or Entry) of a Tree given by the
     * ActionEvent (creates new GUI element).
     *
     * @param e ActionEvent in which a Tree is selected
     */
    public void showTreeSelect(ActionEvent e) {
        showTreeSelect(getTree());
    }

    /**
     * Shows the selected Element (Group or Entry) of the given Tree (creates
     * new GUI element).
     *
     * @param tree Tree of selected Element
     */
    private void showTreeSelect(JTree tree) {
        DatabaseObject object = KeePassTree.getSelectedObject(tree);
        if (null != object && (object.isEntry() || object.isGroup())) {
            show(object);
        }
    }

    /**
     * Shows a given DatabaseObject (Group or Entry) (creates new GUI element).
     *
     * @param tree Tree of selected Element
     */
    private void show(DatabaseObject object) {
        if (null == getShowObjectGui() && null != object) {
            synchronized (showObjectGuiLock) {
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
            }
        } else {
            getGui().showWarning("Entry already shown", "Another Entry window is open. You need to close that window before showing or editing another entry.");
        }
    }

    /**
     * Copies the Username of selected Entry (Tree) to the Clipboard.
     */
    public void copyUsername() {
        copy(true);
    }

    /**
     * Copies the Passowrd of selected Entry (Tree) to the Clipboard. After a
     * given time the Clipboard is cleared.
     */
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

    /**
     * Copies a Username or password of selected Entry (Tree) to the Clipboard.
     *
     * @param isUsername true if username, false if password should be copied
     */
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

    /**
     * Copies a given message to the Clipboard.
     *
     * @param message message to copy to Clipboard
     */
    public static void copyToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * Deletes currently selected Entry (Table) (safety dialog included).
     */
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

    /**
     * Deletes currently selected Group (Tree) (safety dialog included).
     */
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

    /**
     * Deletes currently selected Entry or Group (Tree) (safety dialog
     * included).
     */
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

    /**
     * Deletes given DatabaseObject (safety dialog included).
     */
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

    /**
     * Searches the Database for a search string given by a GUI element.
     */
    public void search() {
        String searchQuery = getGui().getSearchField().getText();
        search(searchQuery);
    }

    /**
     * Searches the Database for given Query.
     *
     * @param searchQuery Query to search the Database fore
     */
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

    /**
     * Exits KeePass 2 (safety prompt if Database changed and not safed).
     */
    public void exit() {
        if (null != getDatabase() && getDatabase().isDirty()) {
            // abort exit if database changed and user cancels action
            if (KeePassGUI.showCancelDialog("Exit without Saving", "There are unsaved changes in the database. If you exit all unsaved data will be lost." + System.lineSeparator() + "Do you really want to exit?", getGui())) {
                return;
            }
        }
        System.exit(0);
    }

    /**
     * Shows a dialog containing Information about the User distributing to this
     * version of KeePass 2.
     */
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

    /**
     * Opens the KeePass Webpage in default Browser.
     */
    public static void openKeePassWebpage() {
        try {
            openWebpage(new URI("http://keepass.info/"));
        } catch (URISyntaxException ex) {
        }
    }

    /**
     * Opens given URL in default Browser.
     *
     * @params url URL to webpage which should be opened
     */
    private static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());

        } catch (URISyntaxException ex) {
            Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opens given URI in default Browser.
     *
     * @params uri URI to webpage which should be opened
     */
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

    /**
     * Updates the Table with the given DatabaseObject. A group will show all
     * first generation Children which are an Entry. An Entry will show its
     * Entry itself.
     *
     * @param object DatabaseObject to display in the Table
     */
    private void updateTableData(DatabaseObject object) {
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(object);
        }

    }

    /**
     * Updates the Table with the given List of DatabaseObject.
     *
     * @param entries List of DatabaseObjects to display in the Table
     */
    private void updateTableData(List<Entry> entries) {
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(entries);
        }
    }

    /**
     * Enables and Disables the Buttons of the GUI. Should be used if a new
     * Database is loaded (heavy computing), but parts of the GUI shall remain
     * responsive.
     *
     * @param enabled true if all Buttons should be enabled
     */
    private void setEnabled(boolean enabled) {
        getGui().setEnabledAllButtons(enabled);
    }

    /**
     * Notifies that the Database has been changed and informs the listener
     * which DatabaseObject is selected in the Tree (should be shown after
     * repainting).
     */
    public void notifyDatabaseChanged() {
        notifyDatabaseChanged(getTree().getSelectedObject());
    }

    /**
     * Notifies that the Database has been changed and informs the listener that
     * the given DatabaseObject should be shown after repainting.
     *
     * @param objectToShow DatabaseObject which is selected/should be shown afte
     * repainting
     */
    public void notifyDatabaseChanged(DatabaseObject objectToShow) {
        DatabaseChangedEvent event = new DatabaseChangedEvent(this, getDatabase(), objectToShow);
        for (DatabaseChangedListener listener : getListeners().getListeners(DatabaseChangedListener.class)) {
            listener.databaseChanged(event);
        }
    }

    /**
     * Adds Listener of given Class to the Listener of the Controller.
     *
     * @param <T> Type of Listener
     * @param className ClassName of Listener
     * @param listener object which implements given listener
     */
    public <T extends EventListener> void addListener(Class<T> className, T listener) {
        getListeners().add(className, listener);
    }

    /**
     * Removes Listener of given Class from the Listener of the Controller.
     *
     * @param <T> Type of Listener
     * @param className ClassName of Listener
     * @param listener object which implements given listener
     */
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
                    synchronized (popupMenuLock) {
                        setPopupMenu(new KeePassPopupMenu(object, mouseEvent.getLocationOnScreen()));
                        addPopupMenuActionListener(object);

                    }
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
        synchronized (popupMenuLock) {
            if (null != getPopupMenu()) {
                setPopupMenu(getPopupMenu().dispose());
            }
        }
    }

    // getter and setter
    /**
     * Sets the File of currently loaded Database. If a Database File is given,
     * it will be saved in a configuration File.
     *
     * @param databaseFile File of currently loaded Database
     */
    public void setDatabaseFile(File databaseFile) {
        // save new database file to ini file
        if (null != databaseFile) {
            DatabasePath databasePath = new DatabasePath();
            databasePath.setPath(databaseFile.getAbsolutePath());
            databasePath.save();
        }
        this.databaseFile = databaseFile;
    }

    /**
     * Gets the current Database.
     *
     * @return current Database
     */
    public DomDatabaseWrapper getDatabase() {
        return database;
    }

    /**
     * Sets the current Database
     *
     * @param database current Database
     */
    public void setDatabase(DomDatabaseWrapper database) {
        this.database = database;
    }

    /**
     * Gets all Listener of the Controller.
     *
     * @return Listener of the Controller
     */
    public EventListenerList getListeners() {
        return listeners;
    }

    /**
     * Gets the Thread which is used to initiate the reset of the Password
     * copied to the Clipboard.
     *
     * @return Thread Thread which initiates the reset of the Password copied to
     * the Clipboard
     */
    public static Thread getPasswordReset() {
        return passwordReset;
    }

    /**
     * Sets the Thread which is used to initiate the reset of the Password
     * copied to the Clipboard.
     *
     * @param aPasswordReset Thread which initiates the reset of the Password
     * copied to the Clipboard
     */
    public static void setPasswordReset(Thread aPasswordReset) {
        passwordReset = aPasswordReset;
    }

    /**
     * Gets the File of currently loaded Database.
     *
     * @return File of currently loaded Database
     */
    public File getDatabaseFile() {
        return databaseFile;
    }

    /**
     * Gets the Password of currently loaded Database.
     *
     * @return Password of currently loaded Database
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * Sets the Password of currently loaded Database.
     *
     * @param password Password of currently loaded Database
     */
    public void setPassword(byte[] password) {
        this.password = password;
    }

    /**
     * Gets the view/GUI.
     *
     * @return view/GUI
     */
    public KeePassGUI getGui() {
        return gui;
    }

    /**
     * Gets the GUI which shows a DatabaseObject. Only one should be present at
     * a time, but is not as strict as a JDialog (rest of GUI can be used).
     *
     * @return GUI which shows a DatabaseObject
     */
    public KeePassShowObjectGUI getShowObjectGui() {
        return showObjectGui;
    }

    /**
     * Sets the GUI which shows a DatabaseObject. Only one should be present at
     * a time, but is not as strict as a JDialog (rest of GUI can be used).
     *
     * @param showObjectGui GUI which shows a DatabaseObject
     */
    public void setShowObjectGui(KeePassShowObjectGUI showObjectGui) {
        this.showObjectGui = showObjectGui;
    }

    /**
     * Gets the Tree (part of the gui) which manages the basic database
     * structure
     *
     * @return Tree structure of Database
     */
    public KeePassTree getTree() {
        return tree;
    }

    /**
     * Gets the GUI which shows a PopupMenu in the main GUI, if the user
     * performs a right-click. Only one should be present at a time.
     *
     * @return GUI which shows a PopupMenu
     */
    public KeePassPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Sets the GUI which shows a PopupMenu in the main GUI, if the user
     * performs a right-click. Only one should be present at a time.
     *
     * @param popupMenu GUI which shows a PopupMenu
     */
    public void setPopupMenu(KeePassPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

}
