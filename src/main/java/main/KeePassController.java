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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;
import org.linguafranca.pwdb.Visitor;
import org.linguafranca.pwdb.kdbx.KdbxCredentials;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.linguafranca.security.Credentials;

/**
 *
 * @author SpecOp0
 */
public class KeePassController implements TreeSelectionListener {

    private final EventListenerList listeners = new EventListenerList();
    private final KeePassGUI gui;
    private final KeePassTree tree;
    private static Thread passwordReset = null;
    private KeePassShowObjectGUI showObjectGui = null;
    // current database
    private DomDatabaseWrapper database = null;
    private File databaseFile = null;
    private byte[] password = null;

    public KeePassController(KeePassGUI gui, KeePassTree tree) {
        this.gui = gui;
        this.tree = tree;
    }

    public void lock() {
        // clear database (tree)
        setDatabase(null);
        setPassword(null);
        notifyDatabaseChanged();
        // clear shown entries (data model)
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(new ArrayList<>());
        }
    }

    public void open() {
        // open file chooser to determine file
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
        open(KeePassGUI.chooseFile(path));
    }

    public void open(String path) {
        open(new File(path));
    }

    private void open(final File file) {
        if (null != file) {
            // enter password
            setPassword(KeePassGUI.enterPassword());
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
                                DatabasePath databasePath = new DatabasePath();
                                databasePath.setPath(file.getAbsolutePath());
                                databasePath.save();
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
        } catch (IOException ex) {
            Logger.getLogger(KeePassController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
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

    public void add() {
        Group group = null;
        DatabaseObject object = KeePassTree.getSelectedObject(getTree());
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
        if (null != group) {
            add(group);
        }
    }

    public void add(Group group) {
        if (null == getShowObjectGui() && null != group) {
            setShowObjectGui(new KeePassShowEntryGUI(getGui()));
            getShowObjectGui().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent event) {
                    if (getShowObjectGui().isSaveObject()) {
                        DatabaseObject newEntry = new DatabaseObject(group.addEntry(getDatabase().newEntry()));
                        getShowObjectGui().saveInputToObject(newEntry);
                        notifyDatabaseChanged();
                        updateTreeData(new DatabaseObject(group));
                    }
                    setShowObjectGui(null);
                }
            });
        } else {
            getGui().showWarning("Entry already shown", "Another Entry window is open. You need to close that window before showing or editing another entry.");
        }
    }

    public void save() {
        if (null != getDatabase()) {
            try {
                FileOutputStream outputStream = new FileOutputStream(getDatabaseFile());
                Credentials credentials = new KdbxCredentials.Password(getPassword());
                getDatabase().save(credentials, outputStream);
                getGui().showWarning("Databse saved", "Database was saved sucessfully to " + System.lineSeparator() + getDatabaseFile().getAbsolutePath());
            } catch (IOException ex) {
                getGui().showWarning("Error while saving Database", "An Error occured while saving Ddatabase to " + System.lineSeparator() + getDatabaseFile().getAbsolutePath() + System.lineSeparator() + "Try saving again or all changes will be lost!");
            }
        } else {
            getGui().showWarning("No Database loaded", "Cannot save Database, because no Database is loaded.");
        }
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

    public void showTreeSelect(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
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
                    Logger.getLogger(KeePassController.class
                            .getName()).log(Level.SEVERE, null, ex);
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
            if (null != object) {
                delete(object);
            } else {
                getGui().showWarning("Delete Failed", "No Entry in table selected (right-hand side).");
            }
        }
    }

    private void delete(DatabaseObject object) {
        if (null != object) {
            Group parent = null;
            if (object.isEntry()) {
                parent = object.getEntry().getParent();
                parent.removeEntry(object.getEntry());
            } else if (object.isGroup()) {
                parent = object.getGroup().getParent();
                parent.removeGroup(object.getGroup());
            }
            if (null != parent) {
                // no group, because only tree model (right-hand side) is used
                notifyDatabaseChanged();
                updateTreeData(new DatabaseObject(parent));
            } else {
                getGui().showWarning("Error while deleting", "Try to show delete Entry or Group, but could not find either.");
                throw new IllegalArgumentException("Error while deleting: Given Object is neither Entry or Group");
            }
        }
    }

    public void search() {
        final String searchQuery = getGui().getSearchField().getText();
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
        updateTreeData(matchingEntries);
    }

    // ===========================
    // initiate or listen to events
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        DatabaseObject object = KeePassTree.getSelectedObject(tree);
        if (null != getDatabase() && null != object && object.isGroup()) {
            updateTreeData(object);
        }
    }

    private void updateTreeData(DatabaseObject object) {
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(object);
        }
    }

    private void updateTreeData(List<Entry> entries) {
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(entries);
        }
    }

    private void setEnabled(boolean enabled) {
        getGui().setEnabledAllButtons(enabled);
    }

    public void notifyDatabaseChanged() {
        DatabaseChangedEvent event = new DatabaseChangedEvent(this, getDatabase());
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

    public void exit() {
        if (null != getDatabase() && getDatabase().isDirty()) {
            // abort exit if database changed and user cancels action
            if (KeePassGUI.showCancelDialog("Exit without Saving", "There are unsaved changes in the database. If you exit all unsaved data will be lost. Do you really want to exit?")) {
                return;
            }
        }
        System.exit(0);
    }

    // getter and setter
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

    public void setDatabaseFile(File databaseFile) {
        this.databaseFile = databaseFile;
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

}
