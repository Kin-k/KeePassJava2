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
package enums;

/**
 *
 * @author SpecOp0
 */

public enum MenuItemType {

    // File menu
    NEW, OPEN, OPEN_RECENT, CLOSE, SAVE, SAVE_AS, DATABASE_SETTINGS, CHANGE_MASTER_KEY, PRINT, IMPORT, EXPORT, SYNCHRONIZE, LOCK_WORKSPACE, EXIT,
    // Edit menu
    ADD_GROUP, EDIT_GROUP, DELETE_GROUP, ADD_ENTRY, EDIT_ENTRY, DUPLICATE_ENTRY, DELETE_ENTRY, SELECT_ALL, SHOW_ALL_ENTRIES, SHOW_ALL_EXPIRED_ENTIRES, SHOW_ENTRIES_BY_TAG, FIND,
    // View menu
    // Tools menu
    // Help Menu
    HELP_CONTENTS, HELP_SOURCE, KEEPASS_WEBSITE, DONATE, CHECK_FOR_UPDATES, ABOUT_KEEPASS
}