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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author SpecOp0
 */
public class KeePassTree extends JTree {

    private static final long serialVersionUID = 1L;

    public KeePassTree(TreeNode root) {
        super(root);
    }

    @Override
    public String convertValueToText(Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value.getClass().equals(DefaultMutableTreeNode.class)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object object = node.getUserObject();
//            if (object.getClass().equals(BookInfo.class)) {
//                BookInfo book = (BookInfo) object;
//                return book.title;

//                } else if (object.getClass().equals(DatabaseObject.class)) {
//                    DatabaseObject dbobject = (DatabaseObject) object;
//                    if (dbobject.isGroup()) {
//                        return dbobject.getGroup().getName();
//                    }
//                    if (dbobject.isEntry()) {
//                        return dbobject.getEntry().getTitle();
//                    }
//            }
        }
        return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
    }

}
