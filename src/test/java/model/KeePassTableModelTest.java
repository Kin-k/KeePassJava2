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
package model;

import model.KeePassTableModel;
import java.io.IOException;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import main.DatabaseObject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;
import org.linguafranca.pwdb.kdb.KdbEntry;
import org.linguafranca.pwdb.kdbx.dom.DomDatabaseWrapper;

/**
 *
 * @author SpecOp0
 */
public class KeePassTableModelTest {

    public KeePassTableModelTest() {
    }

    /**
     * Test of getRowCount method, of class KeePassTableModel.
     */
    @Test
    public void testGetRowCount() {
        System.out.println("getRowCount");
        KeePassTableModel instance = new KeePassTableModel();
        // empty row
        int expResult = 0;
        int result = instance.getRowCount();
        assertEquals(expResult, result);
        // add null object -> expect one row
        instance.add(null);
        expResult = 1;
        result = instance.getRowCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getColumnCount method, of class KeePassTableModel.
     */
    @Test
    public void testGetColumnCount() {
        System.out.println("getColumnCount");
        KeePassTableModel instance = new KeePassTableModel();
        int expResult = 3;
        int result = instance.getColumnCount();
        assertEquals(expResult, result);
    }

    /**
     * Test of getColumnName method, of class KeePassTableModel.
     */
    @Test
    public void testGetColumnName() {
        System.out.println("getColumnName");
        KeePassTableModel instance = new KeePassTableModel();
        // test existing columns
        String[] columnNames = {"Title", "Username", "URL"};
        for (int index = 0; index < columnNames.length; index++) {
            String expResult = columnNames[index];
            assertEquals(expResult, instance.getColumnName(index));
        }
        // test out of bound column
        int[] outOfBoundIndices = {-1, columnNames.length};
        String expResult = "";
        for (int index : outOfBoundIndices) {
            try {
                assertEquals(expResult, instance.getColumnName(index));
            } catch (ArrayIndexOutOfBoundsException ex) {

            }
        }
    }

    /**
     * Test of getColumnClass method, of class KeePassTableModel.
     */
    @Test
    public void testGetColumnClass() {
        System.out.println("getColumnClass");
        KeePassTableModel instance = new KeePassTableModel();
        Class expResult = String.class;
        for (int columnIndex = -1; columnIndex < 10; columnIndex++) {
            assertEquals(expResult, instance.getColumnClass(columnIndex));
        }
    }

    /**
     * Test of isCellEditable method, of class KeePassTableModel.
     */
    @Test
    public void testIsCellEditable() {
        System.out.println("isCellEditable");
        KeePassTableModel instance = new KeePassTableModel();
        boolean expResult = false;
        for (int rowIndex = -1; rowIndex < 10; rowIndex++) {
            for (int columnIndex = -1; columnIndex < 10; columnIndex++) {
                assertEquals(expResult, instance.isCellEditable(rowIndex, columnIndex));
            }
        }
    }

    /**
     * Test of getDatabaseObject method, of class KeePassTableModel.
     */
    @Test
    public void testGetDatabaseObject() {
        System.out.println("getDatabaseObject");
        KeePassTableModel instance = new KeePassTableModel();
        // empty data
        int rowIndex = 0;
        DatabaseObject expResult = null;
        DatabaseObject result = instance.getDatabaseObject(rowIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of showData method, of class KeePassTableModel.
     */
    @Test
    public void testShowData() {
        System.out.println("showData");
        KeePassTableModel instance = new KeePassTableModel();
        // add null object -> nothing happens
        DatabaseObject object = null;
        instance.showData(object);
        assertEquals(true, instance.isEmpty());
        // add one object -> gets displayed
        Entry entry = new KdbEntry();
        object = new DatabaseObject(entry);
        instance.showData(object);
        assertSame(object, instance.get(0));
        // show database (two entries)
        Database database = createNewDatabase();
        Group group = database.getRootGroup();
        List<Entry> entryList = group.getEntries();
        object = new DatabaseObject(group);
        instance.showData(object);
        // expect entries of group and entries of TableModel to match
        for (int index = 0; index < instance.size(); index++) {
            assertEquals(entryList.get(index), instance.get(index).getEntry());
        }
        assertEquals(entryList.size(), instance.size());
    }

    /**
     * Test of getValueAt method, of class KeePassTableModel.
     */
    @Test
    public void testGetValueAt() {
        System.out.println("getValueAt");
        KeePassTableModel instance = new KeePassTableModel();
        // default behavious is empty String
        Object expResult = "";
        Object result = instance.getValueAt(0, 0);
        assertEquals(expResult, result);
        // add database entries
        Database database = createNewDatabase();
        String[] entryNames = {"entry1", "entry2"};
        Group group = database.getRootGroup();
        List<Entry> entryList = group.getEntries();
        instance.showData(new DatabaseObject(group));
        for (int index = 0; index < entryList.size(); index++) {
            String entryName = entryNames[index];
            for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
                assertEquals(entryName, instance.getValueAt(index, columnIndex));
            }
        }
    }

    /**
     * Test of addTableModelListener method, of class KeePassTableModel.
     */
    @Test
    public void testAddTableModelListener() {
        System.out.println("addTableModelListener");
        KeePassTableModel instance = new KeePassTableModel();
        // empty listener
        int expResult = 0;
        assertEquals(expResult, instance.getListeners().getListenerCount());
        // add null listener
        instance.addTableModelListener(null);
        assertEquals(expResult, instance.getListeners().getListenerCount());
        // add listener
        TableModelListener listener = new ITableModelListener();
        instance.addTableModelListener(listener);
        // listener is one item
        expResult = 1;
        assertEquals(expResult, instance.getListeners().getListenerCount());
        // added listener equals argument
        TableModelListener[] listenerList = instance.getListeners().getListeners(TableModelListener.class);
        assertSame(listener, listenerList[0]);
    }

    /**
     * Test of removeTableModelListener method, of class KeePassTableModel.
     */
    @Test
    public void testRemoveTableModelListener() {
        System.out.println("removeTableModelListener");
        KeePassTableModel instance = new KeePassTableModel();
        // add listener
        TableModelListener listener = new ITableModelListener();
        instance.addTableModelListener(listener);
        // listener is one item
        int expResult = 1;
        assertEquals(expResult, instance.getListeners().getListenerCount());

        // remove null object (expect same result)
        instance.removeTableModelListener(null);
        assertEquals(expResult, instance.getListeners().getListenerCount());

        // remove listener
        instance.removeTableModelListener(listener);
        // empty listener
        expResult = 0;
        assertEquals(expResult, instance.getListeners().getListenerCount());
    }

    /**
     * Test of isEmpty method, of class KeePassTableModel.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        KeePassTableModel instance = new KeePassTableModel();
        assertEquals(true, instance.isEmpty());
    }

    /**
     * Test of clear method, of class KeePassTableModel.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        KeePassTableModel instance = new KeePassTableModel();
        // add null object
        instance.add(null);
        assertEquals(false, instance.isEmpty());

        // clear object
        instance.clear();
        assertEquals(true, instance.isEmpty());
    }

    /**
     * Test of add method, of class KeePassTableModel.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        KeePassTableModel instance = new KeePassTableModel();
        // is empty
        assertEquals(true, instance.isEmpty());

        // add object
        DatabaseObject object = null;
        instance.add(object);
        assertEquals(false, instance.isEmpty());

        // add object and test if really in list
        DatabaseObject kdbEntry = new DatabaseObject(new KdbEntry());
        instance.add(kdbEntry);
        assertSame(kdbEntry, instance.get(1));
    }

    /**
     * Test of addListener method, of class KeePassTableModel.
     */
    @Test
    public void testAddListener() {
        System.out.println("addListener");
        KeePassTableModel instance = new KeePassTableModel();
        // empty listener
        int expResult = 0;
        assertEquals(expResult, instance.getListeners().getListenerCount());
        // add null listener
        instance.addListener(TableModelListener.class, null);
        assertEquals(expResult, instance.getListeners().getListenerCount());
        // add listener
        TableModelListener listener = new ITableModelListener();
        instance.addListener(TableModelListener.class, listener);
        // listener is one item
        expResult = 1;
        assertEquals(expResult, instance.getListeners().getListenerCount());
        // added listener equals argument
        TableModelListener[] listenerList = instance.getListeners().getListeners(TableModelListener.class);
        assertSame(listener, listenerList[0]);
    }

    /**
     * Test of removeListener method, of class KeePassTableModel.
     */
    @Test
    public void testRemoveListener() {
        System.out.println("removeListener");
        KeePassTableModel instance = new KeePassTableModel();
        // add listener
        TableModelListener listener = new ITableModelListener();
        instance.addTableModelListener(listener);
        // listener is one item
        int expResult = 1;
        assertEquals(expResult, instance.getListeners().getListenerCount());

        // remove null object (expect same result)
        instance.removeListener(TableModelListener.class, null);
        assertEquals(expResult, instance.getListeners().getListenerCount());

        // remove listener
        instance.removeListener(TableModelListener.class, listener);
        // empty listener
        expResult = 0;
        assertEquals(expResult, instance.getListeners().getListenerCount());
    }

    private class ITableModelListener implements TableModelListener {

        public ITableModelListener() {
        }

        @Override
        public void tableChanged(TableModelEvent e) {
        }

    }

    private Database createNewDatabase() {
        DomDatabaseWrapper database = null;
        try {
            database = new DomDatabaseWrapper();
            Group rootGroup = database.getRootGroup();
            String[] entryNames = {"entry1", "entry2"};
            for (String entryName : entryNames) {
                rootGroup.addEntry(entryFactory(database, entryName));
            }
        } catch (IOException ex) {
            fail("Could not create new Database");
            System.out.println(ex.getMessage());
        }
        return database;
    }

    private Entry entryFactory(Database database, String name) {
        Entry result = database.newEntry();
        result.setTitle(name);
        result.setUsername(name);
        result.setPassword(name);
        result.setUrl(name);
        result.setNotes(name);
        return result;
    }

}
