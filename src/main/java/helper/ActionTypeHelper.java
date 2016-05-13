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
 *
 * @author SpecOp0
 */
public class ActionTypeHelper {

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

    public static boolean isAlwaysActive(ActionButton button) {
        switch (button.getType()) {
            case COPY_USER:
            case COPY_PW:
            case EXIT:
                return true;
        }
        return false;
    }

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
