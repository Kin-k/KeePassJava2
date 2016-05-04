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

enum MenuType {

    FILE, EDIT, VIEW, TOOLS, HELP
}

enum MenuItemType {

    // File menu
    NEW, OPEN, OPEN_RECENT, CLOSE, SAVE, SAVE_AS, DATABASE_SETTINGS, CHANGE_MASTER_KEY, PRINT, IMPORT, EXPORT, SYNCHRONIZE, LOCK_WORKSPACE, EXIT,
    // Edit menu
    ADD_GROUP, EDIT_GROUP, DELETE_GROUP, ADD_ENTRY, EDIT_ENTRY, DUPLICATE_ENTRY, DELETE_ENTRY, SELECT_ALL, SHOW_ALL_ENTRIES, SHOW_ALL_EXPIRED_ENTIRES, SHOW_ENTRIES_BY_TAG, FIND,
    // View menu
    // Tools menu
    // Help Menu
    HELP_CONTENTS, HELP_SOURCE, KEEPASS_WEBSITE, DONATE, CHECK_FOR_UPDATES, ABOUT_KEEPASS
}

/**
 *
 * @author SpecOp0
 */
public class MenuTypeHelper {

    public static boolean isSeperatorAfterward(MenuItemType type) {
        switch (type) {
            case CLOSE:
            case SAVE_AS:
            case CHANGE_MASTER_KEY:
            case PRINT:
            case SYNCHRONIZE:
            case DELETE_GROUP:
            case DELETE_ENTRY:
            case SELECT_ALL:
            case SHOW_ENTRIES_BY_TAG:
            case HELP_SOURCE:
            case DONATE:
            case CHECK_FOR_UPDATES:
                return true;
        }
        return false;
    }

    public static boolean isImplemented(MenuItemType type) {
        switch (type) {
            case NEW:
            case OPEN:
            case CLOSE:
            case SAVE:
            case SAVE_AS:
            case LOCK_WORKSPACE:
            case EXIT:
            case ADD_GROUP:
            case EDIT_GROUP:
            case DELETE_GROUP:
            case ADD_ENTRY:
            case EDIT_ENTRY:
            case DUPLICATE_ENTRY:
            case DELETE_ENTRY:
            case SHOW_ALL_ENTRIES:
            case KEEPASS_WEBSITE:
            case ABOUT_KEEPASS:
                return true;
        }
        return false;
    }

    public static MenuType getMenu(MenuItemType type) {
        switch (type) {
            case NEW:
            case OPEN:
            case OPEN_RECENT:
            case CLOSE:
            case SAVE:
            case SAVE_AS:
            case DATABASE_SETTINGS:
            case CHANGE_MASTER_KEY:
            case PRINT:
            case IMPORT:
            case EXPORT:
            case SYNCHRONIZE:
            case LOCK_WORKSPACE:
            case EXIT:
                return MenuType.FILE;
            case ADD_GROUP:
            case EDIT_GROUP:
            case DELETE_GROUP:
            case ADD_ENTRY:
            case EDIT_ENTRY:
            case DUPLICATE_ENTRY:
            case DELETE_ENTRY:
            case SELECT_ALL:
            case SHOW_ALL_ENTRIES:
            case SHOW_ALL_EXPIRED_ENTIRES:
            case SHOW_ENTRIES_BY_TAG:
            case FIND:
                return MenuType.EDIT;
            case HELP_CONTENTS:
            case HELP_SOURCE:
            case KEEPASS_WEBSITE:
            case DONATE:
            case CHECK_FOR_UPDATES:
            case ABOUT_KEEPASS:
                return MenuType.HELP;
            default:
                throw new AssertionError(type.name());
        }
    }

    public static boolean isAlwaysActive(MenuItemType type) {
        return false;
    }
}
