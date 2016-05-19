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

import enums.ActionType;
import java.net.URL;
import javax.swing.ImageIcon;
import view.ActionButton;

/**
 * Useeful functions for ActionType.
 *
 * @author SpecOp0
 */
public class ActionTypeHelper {

    /**
     * Creates J(Action)Button for given ActionType. Mainly a Text and Icon (if
     * exists) will be added.
     *
     * @param type type of J(Action)Button
     * @return new JActionButton
     */
    public static ActionButton getButton(ActionType type) {
        ActionButton button = new ActionButton(type);
        ImageIcon icon = ActionTypeHelper.getIcon(type);
        if (null != icon) {
            button.setIcon(icon);
            button.setToolTipText(icon.getDescription());
        } else {
            button.setText(type.toString());
        }
        return button;
    }

    /**
     * Tests if after given ActionType a seperator should be used.
     *
     * @param type ActionType to test
     * @return true if a seperator should be placed after given type
     */
    public static boolean isSeperatorAfterwards(ActionType type) {
        switch (type) {
            case SAVE:
            case DELETE:
            case COPY_PW:
            case EXIT:
                return true;
        }
        return false;
    }

    /**
     * Tests if given ActionType should always be usable (be active). Most of
     * the functions can only be used if a Database is loaded and might want to
     * be not active / clickable if no Database is present.
     *
     * @param type ActionType to test
     * @return true if ActionType should always be usable
     */
    public static boolean isAlwaysActive(ActionType type) {
        switch (type) {
            case COPY_USER:
            case COPY_PW:
            case EXIT:
                return true;
        }
        return false;
    }

    /**
     * Loads ImageIcon for given ActionType from resources folder. Returns null
     * if not found.
     *
     * @param type ActionType to get Icon for
     * @return ImageIcon of given ActionType or null if Icon not found
     */
    public static ImageIcon getIcon(ActionType type) {
        URL resource = null;
        switch (type) {
            case OPEN:
                resource = ActionTypeHelper.class.getClassLoader().getResource("document-open.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Open Database");
                }
                break;
            case SAVE:
                resource = ActionTypeHelper.class.getClassLoader().getResource("document-save.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Save Database");
                }
                break;
            case ADD:
                resource = ActionTypeHelper.class.getClassLoader().getResource("entry-new.png");
                if (null != resource) {
                    return new ImageIcon(resource, "New Entry");
                }
                break;
            case SHOW:
                resource = ActionTypeHelper.class.getClassLoader().getResource("entry-edit.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Show/Edit Entry");
                }
                break;
            case DELETE:
                resource = ActionTypeHelper.class.getClassLoader().getResource("entry-delete.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Delete Entry");
                }
                break;
            case COPY_USER:
                resource = ActionTypeHelper.class.getClassLoader().getResource("username-copy.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Copy Username");
                }
                break;
            case COPY_PW:
                resource = ActionTypeHelper.class.getClassLoader().getResource("password-copy.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Copy Password");
                }
                break;
            case LOCK:
                resource = ActionTypeHelper.class.getClassLoader().getResource("document-encrypt.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Lock Session");
                }
                break;
            case EXIT:
                resource = ActionTypeHelper.class.getClassLoader().getResource("dialog-error.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Exit");
                }
                break;
            case SEARCH:
                resource = ActionTypeHelper.class.getClassLoader().getResource("system-search.png");
                if (null != resource) {
                    return new ImageIcon(resource, "Exit");
                }
                break;
            default:
                throw new AssertionError(type.name());

        }
        return null;
    }
}
