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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.linguafranca.pwdb.Entry;

/**
 *
 * @author SpecOp0
 */
public class KeePassTableModel implements TableModel, SelectionChangedListener {

    private final EventListenerList listeners = new EventListenerList();
    private final List<DatabaseObject> data = new ArrayList<>();

    private enum DataTableColumns {

        TITLE, USERNAME, URL
    };

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return DataTableColumns.values().length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        try {
            switch (DataTableColumns.values()[columnIndex]) {
                case TITLE:
                    return "Title";
                case USERNAME:
                    return "Username";
                case URL:
                    return "URL";
                default:
                    throw new AssertionError(DataTableColumns.values()[columnIndex].name());
            }
        } catch (ArrayIndexOutOfBoundsException ex) {

        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public DatabaseObject getDatabaseObject(int rowIndex) {
        DatabaseObject object = null;
        try {
            object = data.get(rowIndex);
        } catch (ArrayIndexOutOfBoundsException ex) {

        }
        return object;
    }

    @Override
    public void showData(DatabaseObject object) {
        data.clear();
        if (null != object) {
            if (object.isGroup()) {
                for (Entry entry : object.getGroup().getEntries()) {
                    data.add(new DatabaseObject(entry));
                }
            } else if (object.isEntry()) {
                data.add(object);
            }
        }
        tableChanged();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String returnValue = "";
        try {
            DatabaseObject object = data.get(rowIndex);
            switch (DataTableColumns.values()[columnIndex]) {
                case TITLE:
                    if (object.isGroup()) {
                        return object.getGroup().getName();
                    }
                    if (object.isEntry()) {
                        return object.getEntry().getTitle();
                    }
                    break;
                case USERNAME:
                    if (object.isGroup()) {
                        return object.getGroup().getEntriesCount();
                    }
                    if (object.isEntry()) {
                        return object.getEntry().getUsername();
                    }
                    break;
                case URL:
                    if (object.isEntry()) {
                        return object.getEntry().getUrl();
                    }
                    break;
                default:
                    throw new AssertionError(DataTableColumns.values()[columnIndex].name());

            }
        } catch (ArrayIndexOutOfBoundsException ex) {

        }
        return returnValue;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        getListeners().add(TableModelListener.class, l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        getListeners().remove(TableModelListener.class, l);
    }

    private void tableChanged() {
        TableModelEvent event = new TableModelEvent(this);
        for (TableModelListener listener : getListeners().getListeners(TableModelListener.class)) {
            listener.tableChanged(event);
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void clear() {
        data.clear();
    }

    public void add(DatabaseObject object) {
        data.add(object);
    }

    public <T extends EventListener> void addListener(Class<T> className, T listener) {
        getListeners().add(className, listener);
    }

    public <T extends EventListener> void removeListener(Class<T> className, T listener) {
        getListeners().remove(className, listener);
    }

    public EventListenerList getListeners() {
        return listeners;
    }

}
