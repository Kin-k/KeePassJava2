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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author SpecOp0
 */
public class DatabasePath extends Properties {

    private static final long serialVersionUID = 1L;

    public DatabasePath() {
        setPath(".");
    }

    public DatabasePath(Properties defaults) {
        super(defaults);
    }

    public void load() {
        try {
            FileInputStream fis = new FileInputStream("path.ini");
            load(fis);
            fis.close();
        } catch (NullPointerException ex) {
            System.out.println("fis null");
        } catch (FileNotFoundException ex) {
            System.out.println("path.ini not found");
        } catch (IOException ex) {
            System.out.println("I/O exception");
        }
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream("path.ini");
            store(fos, null);
            fos.close();
        } catch (NullPointerException ex) {
            System.out.println("fos null");
        } catch (FileNotFoundException ex) {
            System.out.println("path.ini not found");
        } catch (IOException ex) {
            System.out.println("I/O exception");
        }
    }
    
    public boolean isDatabase(){
        return isDatabase(getPath());
    }
    
    public static boolean isDatabase(String path){
        return (path.endsWith(".kdb") || path.endsWith(".kdbx"));
    }

    public String getPath() {
        return getProperty("path");
    }

    public void setPath(String path) {
        setProperty("path", path);
    }
}
