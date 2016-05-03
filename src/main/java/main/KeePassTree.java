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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.linguafranca.pwdb.Entry;
import org.linguafranca.pwdb.Group;

/**
 *
 * @author SpecOp0
 */
public class KeePassTree extends JTree implements DatabaseChangedListener {

    private static final long serialVersionUID = 1L;

    public KeePassTree(TreeNode root) {
        super(root);
    }

    public KeePassTree(String rootName) {
        super(new DefaultMutableTreeNode("root"));
    }

    @Override
    public void databaseChanged(DatabaseChangedEvent event) {
        // get root and change it
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        if (null != event.getDatabase()) {
            root.setUserObject(new DatabaseObject(event.getDatabase().getRootGroup()));
            // search groups
            for (Group group : event.getDatabase().getRootGroup().getGroups()) {
                DefaultMutableTreeNode branch = new DefaultMutableTreeNode(new DatabaseObject(group));
                // and add entries
                for (Entry entry : group.getEntries()) {
                    DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(new DatabaseObject(entry));
                    branch.insert(leaf, branch.getChildCount());
                }
                root.add(branch);
            }
            // add direct entries for root
            for (Entry entry : event.getDatabase().getRootGroup().getEntries()) {
                DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(new DatabaseObject(entry));
                root.insert(leaf, root.getChildCount());
            }
        } else {
            root.setUserObject("root");
        }
        // reload (closes tree if update only)
        model.reload(root);
    }

    public static DatabaseObject getSelectedObject(JTree tree) {
        DatabaseObject object = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (null != node && null != node.getUserObject() && node.getUserObject().getClass().equals(DatabaseObject.class)) {
            object = (DatabaseObject) node.getUserObject();
        }
        return object;
    }

}
