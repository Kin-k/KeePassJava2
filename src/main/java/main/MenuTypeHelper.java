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

import java.net.URL;
import javax.swing.ImageIcon;

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

    public static String getName(MenuType type) {
        String name = type.toString();
        switch (type) {
            case FILE:
                name = "File";
                break;
            case EDIT:
                name = "Edit";
                break;
            case VIEW:
                name = "View";
                break;
            case TOOLS:
                name = "Tools";
                break;
            case HELP:
                name = "Help";
                break;
            default:
                throw new AssertionError(type.name());
        }
        return name;
    }

    public static ImageIcon getIcon(MenuItemType type) {
        URL ressource;
        switch (type) {
            case NEW:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-new.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "New Database");
                }
                break;
            case OPEN:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-open.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Open Database");
                }
                break;
            case OPEN_RECENT:
                break;
            case CLOSE:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-close.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Close Database");
                }
                break;
            case SAVE:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-save.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Save");
                }
                break;
            case SAVE_AS:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-save-as.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Save As");
                }
                break;
            case DATABASE_SETTINGS:
                break;
            case CHANGE_MASTER_KEY:
                break;
            case PRINT:
                break;
            case IMPORT:
                break;
            case EXPORT:
                break;
            case SYNCHRONIZE:
                break;
            case LOCK_WORKSPACE:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-encrypt.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Lock Workspace");
                }
                break;
            case EXIT:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/application-exit.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Exit Program");
                }
                break;
            case ADD_GROUP:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/group-new.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Add Group");
                }
                break;
            case EDIT_GROUP:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/group-edit.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Edit Group");
                }
                break;
            case DELETE_GROUP:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/group-delete.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Delete Group");
                }
                break;
            case ADD_ENTRY:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-new.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Add Entry");
                }
                break;
            case EDIT_ENTRY:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-edit.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Edit Entry");
                }
                break;
            case DUPLICATE_ENTRY:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-clone.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Duplicate Entry");
                }
                break;
            case DELETE_ENTRY:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-delete.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Delete Entry");
                }
                break;
            case SELECT_ALL:
                break;
            case SHOW_ALL_ENTRIES:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/system-search.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Show all Entries");
                }
                break;
            case SHOW_ALL_EXPIRED_ENTIRES:
                break;
            case SHOW_ENTRIES_BY_TAG:
                break;
            case FIND:
                break;
            case HELP_CONTENTS:
                break;
            case HELP_SOURCE:
                break;
            case KEEPASS_WEBSITE:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/about-website.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "KeePass Website");
                }
                break;
            case DONATE:
                break;
            case CHECK_FOR_UPDATES:
                break;
            case ABOUT_KEEPASS:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/help-about.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "About KeePass");
                }
                break;
            default:
                throw new AssertionError(type.name());
        }
        return null;
    }
}
