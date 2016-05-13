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
package events;

import java.util.EventObject;
import model.DatabaseObject;
import org.linguafranca.pwdb.Database;

/**
 *
 * @author SpecOp0
 */
public class DatabaseChangedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private final Database database;
    private final boolean newDatabase;
    private final DatabaseObject displayedObject;

    public DatabaseChangedEvent(Object source, Database database) {
        super(source);
        this.database = database;
        this.newDatabase = false;
        this.displayedObject = null;
    }

    public DatabaseChangedEvent(Object source, Database database, DatabaseObject displayedObject) {
        super(source);
        this.database = database;
        this.displayedObject = displayedObject;
        this.newDatabase = false;
    }

    public DatabaseChangedEvent(Object source, Database database, DatabaseObject displayedObject, boolean newDatabase) {
        super(source);
        this.database = database;
        this.displayedObject = displayedObject;
        this.newDatabase = newDatabase;
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isNewDatabase() {
        return newDatabase;
    }

    public DatabaseObject getDisplayedObject() {
        return displayedObject;
    }

}
