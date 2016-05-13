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
package helper;

import enums.MenuItemType;
import enums.MenuType;
import java.net.URL;
import javax.swing.ImageIcon;
import view.JMenuItemType;

/**
 *
 * @author SpecOp0
 */
public class MenuTypeHelper {

    public static JMenuItemType getMenuItem(MenuItemType type) {
        JMenuItemType menuItem = new JMenuItemType(type);
        ImageIcon icon = MenuTypeHelper.getIcon(type);
        if (null != icon) {
            menuItem.setIcon(icon);
            menuItem.setText(icon.getDescription());
        }
        return menuItem;
    }

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
        URL resource;
        switch (type) {
            case NEW:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-new.png");
                if (null != resource) {
                    return new ImageIcon(resource, "New Database");
                }
                break;
            case OPEN:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-open.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Open Database");
                }
                break;
            case OPEN_RECENT:
                break;
            case CLOSE:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-close.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Close Database");
                }
                break;
            case SAVE:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-save.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Save");
                }
                break;
            case SAVE_AS:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-save-as.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Save As");
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
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/document-encrypt.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Lock Workspace");
                }
                break;
            case EXIT:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/application-exit.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Exit Program");
                }
                break;
            case ADD_GROUP:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/group-new.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Add Group");
                }
                break;
            case EDIT_GROUP:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/group-edit.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Edit Group");
                }
                break;
            case DELETE_GROUP:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/group-delete.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Delete Group");
                }
                break;
            case ADD_ENTRY:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-new.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Add Entry");
                }
                break;
            case EDIT_ENTRY:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-edit.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Edit Entry");
                }
                break;
            case DUPLICATE_ENTRY:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-clone.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Duplicate Entry");
                }
                break;
            case DELETE_ENTRY:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/entry-delete.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Delete Entry");
                }
                break;
            case SELECT_ALL:
                break;
            case SHOW_ALL_ENTRIES:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/system-search.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Show all Entries");
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
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/about-website.png");
                if (null != resource) {
                    return new ImageIcon(resource, "KeePass Website");
                }
                break;
            case DONATE:
                break;
            case CHECK_FOR_UPDATES:
                break;
            case ABOUT_KEEPASS:
                resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/help-about.png");
                if (null != resource) {
                    return new ImageIcon(resource, "About KeePass");
                }
                break;
            default:
                throw new AssertionError(type.name());
        }
        return null;
    }
}
