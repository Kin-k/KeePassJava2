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

enum ActionType {

    OPEN, SAVE, ADD, SHOW, DELETE, COPY_USER, COPY_PW, LOCK, EXIT, SEARCH

}

/**
 *
 * @author SpecOp0
 */
public class ActionTypeHelper {

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

    public static ImageIcon getIcon(ActionType type) {
        URL ressource = null;
        switch (type) {
            case OPEN:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("document-open.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Open Database");
                }
                break;
            case SAVE:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("document-save.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Save Database");
                }
                break;
            case ADD:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("entry-new.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "New Entry");
                }
                break;
            case SHOW:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("entry-edit.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Show/Edit Entry");
                }
                break;
            case DELETE:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("entry-delete.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Delete Entry");
                }
                break;
            case COPY_USER:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("username-copy.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Copy Username");
                }
                break;
            case COPY_PW:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("password-copy.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Copy Password");
                }
                break;
            case LOCK:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("document-encrypt.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Lock Session");
                }
                break;
            case EXIT:
                ressource = ActionTypeHelper.class.getClassLoader().getResource("dialog-error.png");
                if (null != ressource) {
                    return new ImageIcon(ressource, "Exit");
                }
                break;
            case SEARCH:
                break;
            default:
                throw new AssertionError(type.name());

        }
        return null;
    }
}
