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

import java.io.FileOutputStream;
import java.net.URL;
import static main.DatabasePath.getFilename;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author SpecOp0
 */
public class DatabasePathTest {

    private static String expectedPath = "";

    public DatabasePathTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // create path.ini
        FileOutputStream fos = new FileOutputStream(getFilename());
        URL defaultPath = ActionTypeHelper.class.getClassLoader().getResource("test.kdbx");
        expectedPath = defaultPath.getFile();
        String pathProperty = "path=" + expectedPath;
        System.out.println(pathProperty);
        fos.write(pathProperty.getBytes());
        fos.close();
    }

    /**
     * Test of load method, of class DatabasePath.
     */
    @Test
    public void testLoad() {
        System.out.println("load");
        DatabasePath instance = new DatabasePath();
        instance.load();
        assertEquals(expectedPath, instance.getPath());
    }

    /**
     * Test of save method, of class DatabasePath.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        DatabasePath instance = new DatabasePath();
        instance.setPath(expectedPath);
        instance.save();
        DatabasePath loadInstance = new DatabasePath();
        loadInstance.load();
        assertEquals(expectedPath, loadInstance.getPath());
    }

    /**
     * Test of isDatabase method, of class DatabasePath.
     */
    @Test
    public void testIsDatabase_0args() {
        System.out.println("isDatabase");
        DatabasePath instance = new DatabasePath();
        // default path "."
        assertEquals(false, instance.isDatabase());
        // database files
        String fileExtensionKDB = "test.kdb";
        String fileExtensionKDBX = "test.kdbx";
        String[] databaseFiles = {fileExtensionKDB, fileExtensionKDBX};
        for (String filename : databaseFiles) {
            instance.setPath(filename);
            assertEquals(true, instance.isDatabase());
        }
        // not database files
        String fileNameKDBnoExtension = "kdb";
        String fileNameKDBXnoExtension = "kdbx";
        String fileExtensionTXT = ".txt";
        String[] notDatabaseFiles = {fileNameKDBnoExtension, fileNameKDBXnoExtension, fileExtensionTXT};
        for (String filename : notDatabaseFiles) {
            instance.setPath(filename);
            assertEquals(false, instance.isDatabase());
        }
    }

    /**
     * Test of isDatabase method, of class DatabasePath.
     */
    @Test
    public void testIsDatabase_String() {
        System.out.println("isDatabase");
        DatabasePath instance = new DatabasePath();
        // database files
        String fileExtensionKDB = "test.kdb";
        String fileExtensionKDBX = "test.kdbx";
        String[] databaseFiles = {fileExtensionKDB, fileExtensionKDBX};
        for (String filename : databaseFiles) {
            instance.setPath(filename);
            assertEquals(true, instance.isDatabase());
        }
        // not database files
        String fileNameKDBnoExtension = "kdb";
        String fileNameKDBXnoExtension = "kdbx";
        String fileExtensionTXT = ".txt";
        String defaultPath = ".";
        String[] notDatabaseFiles = {fileNameKDBnoExtension, fileNameKDBXnoExtension, fileExtensionTXT, defaultPath};
        for (String filename : notDatabaseFiles) {
            instance.setPath(filename);
            assertEquals(false, instance.isDatabase());
        }
    }

    /**
     * Test of getPath method, of class DatabasePath.
     */
    @Test
    public void testGetPath() {
        System.out.println("getPath");
        DatabasePath instance = new DatabasePath();
        // default path
        assertEquals(".", instance.getPath());
        // set path
        instance.setPath(expectedPath);
        assertEquals(expectedPath, instance.getPath());
    }

    /**
     * Test of setPath method, of class DatabasePath.
     */
    @Test
    public void testSetPath() {
        System.out.println("setPath");
        DatabasePath instance = new DatabasePath();
        instance.setPath(expectedPath);
        assertEquals(expectedPath, instance.getPath());
    }

    /**
     * Test of getFilename method, of class DatabasePath.
     */
    @Test
    public void testGetFilename() {
        String filename = "path.ini";
        String result = DatabasePath.getFilename();
        assertEquals(filename, result);
    }

}
