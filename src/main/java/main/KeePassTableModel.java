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
import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author SpecOp0
 */
public class KeePassTableModel implements TableModel {

    private final String[] dataTableColumns = {"Title", "Username", "URL"};
    private final List<DatabaseObject> data = new ArrayList<>();

    private enum dataTableColumnss {

        TITLE, USERNAME, URL
    };

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return dataTableColumns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < dataTableColumns.length) {
            return dataTableColumns[columnIndex];
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
    public Object getValueAt(int rowIndex, int columnIndex) {
        String returnValue = "";
        try {
            DatabaseObject object = data.get(rowIndex);
            switch (dataTableColumnss.values()[columnIndex]) {
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
                    throw new AssertionError(dataTableColumnss.values()[columnIndex].name());

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
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
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

}
