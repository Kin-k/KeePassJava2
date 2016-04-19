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

import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;

/**
 *
 * @author SpecOp0
 */
public class DatabaseObject {

    private final Group group;
    private final Entry entry;

    public DatabaseObject(Group group) {
        this.group = group;
        this.entry = null;
    }

    public DatabaseObject(Entry entry) {
        this.group = null;
        this.entry = entry;
    }

    public DatabaseObject(Group group, Entry entry) {
        this.group = group;
        this.entry = entry;
        if (null == group && null == entry) {
            System.out.println("Error: both objects null");
        }
    }

    @Override
    public String toString() {
        if (isGroup()) {
            return getGroup().getName();
        }
        if (isEntry()) {
            return getEntry().getTitle();
        }
        return super.toString();
    }

    public boolean isGroup() {
        return null != getGroup();
    }

    public boolean isEntry() {
        return null != getEntry();
    }

    public Object getObject() {
        if (isGroup()) {
            return getGroup();
        }
        if (isEntry()) {
            return getEntry();
        }
        return null;
    }

    public Group getGroup() {
        return group;
    }

    public Entry getEntry() {
        return entry;
    }

}
