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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.kdbx.KdbxCredentials;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;
import org.linguafranca.security.Credentials;

/**
 *
 * @author SpecOp0
 */
public class KeePassController implements TreeSelectionListener {

    private final EventListenerList listeners = new EventListenerList();
    private static Thread passwordReset = null;
    private Database database = null;

    public KeePassController() {
    }

    public void lock() {
        // clear database (tree)
        setDatabase(null);
        notifyDatabaseChanged();
        // clear shown entries (data model)
        for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
            listener.showData(null);
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
        final File file = new File(path);
        open(file);
    }

    private void open(final File file) {
        if (null != file) {
            // enter password
            final byte[] password = KeePassGUI.enterPassword();
            if (null != password) {
                setEnabled(false);
                // start worker
                Thread worker = new Thread() {

                    @Override
                    public void run() {
                        // heavy computing (decrypt database)
                        open(file, password);
                        SwingUtilities.invokeLater(() -> {
                            setEnabled(true);
                            if (null != getDatabase()) {
                                notifyDatabaseChanged();
                                // save path to file
                                DatabasePath databasePath = new DatabasePath();
                                databasePath.setPath(file.getAbsolutePath());
                                databasePath.save();
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
                    message = String.format("Inconsistent stream bytes while reading File (%s). Wrong password?", file.getName());
                    break;
            }
            KeePassGUI.showWarning(title, message);
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

    void copyUsername() {
        for (ControllerListener listener : getListeners().getListeners(ControllerListener.class)) {
            listener.copyUsername();
        }
    }

    synchronized void copyPassword() {
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

        for (ControllerListener listener : getListeners().getListeners(ControllerListener.class)) {
            listener.copyPassword();
        }
        getPasswordReset().start();
    }

    public static void copyToClipboard(String message) {
        StringSelection stringSelection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree treea = (JTree) e.getSource();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treea.getLastSelectedPathComponent();
        if (null != node && node.getUserObject().getClass().equals(DatabaseObject.class)) {
            DatabaseObject object = (DatabaseObject) node.getUserObject();
            if (null != getDatabase()) {
                for (SelectionChangedListener listener : getListeners().getListeners(SelectionChangedListener.class)) {
                    listener.showData(object);
                }
            }
        }
    }

    private void setEnabled(boolean enabled) {
        for (ControllerListener listener : getListeners().getListeners(ControllerListener.class)) {
            listener.setEnabledAllButtons(enabled);
        }
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

    void exit() {
        if (null != getDatabase() && getDatabase().isDirty()) {
            // abort exit if database changed and user cancels action
            if (KeePassGUI.showCancelDialog("Exit without Saving", "There are unsaved changes in the database. If you exit all unsaved data will be lost. Do you really want to exit?")) {
                return;
            }
        }
        System.exit(0);
    }

    // getter and setter
    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
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

}
