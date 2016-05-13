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
package model;

import listener.DatabaseChangedListener;
import events.DatabaseChangedEvent;
import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
                DefaultMutableTreeNode branch = transformToNode(group);
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
        if (null != event.getDisplayedObject()) {
            displayObject(event.getDisplayedObject());
        }
    }

    private DefaultMutableTreeNode transformToNode(DatabaseObject object) {
        DefaultMutableTreeNode branch;
        if (object.isGroup()) {
            branch = new DefaultMutableTreeNode(object);
            for (Group group : object.getGroup().getGroups()) {
                branch.insert(transformToNode(group), branch.getChildCount());
            }
            for (Entry entry : object.getGroup().getEntries()) {
                branch.insert(transformToNode(entry), branch.getChildCount());
            }
        } else {
            branch = new DefaultMutableTreeNode(object);
        }
        return branch;
    }

    public void displayObject(DatabaseObject object) {
        if (null != object) {
            // search for expanded object
            DefaultTreeModel model = (DefaultTreeModel) getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            TreePath pathToDisplay = find(root, object);
            // display node
            if (null != pathToDisplay) {
                expandPath(pathToDisplay);
                getSelectionModel().setSelectionPath(pathToDisplay);
                if (object.isEntry()) {
                    setExpandedState(pathToDisplay.getParentPath(), true);
                } else {
                    setExpandedState(pathToDisplay, true);
                }
            }
        }
    }

    private TreePath find(DefaultMutableTreeNode root, DatabaseObject object) {
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            DatabaseObject nodeObject = (DatabaseObject) node.getUserObject();
            if (nodeObject.getObject().equals(object.getObject())) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    private DefaultMutableTreeNode transformToNode(Group group) {
        return transformToNode(new DatabaseObject(group));
    }

    private DefaultMutableTreeNode transformToNode(Entry entry) {
        return transformToNode(new DatabaseObject(entry));
    }

    public static DatabaseObject getSelectedObject(JTree tree) {
        DatabaseObject object = null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (null != node && null != node.getUserObject() && node.getUserObject().getClass().equals(DatabaseObject.class)) {
            object = (DatabaseObject) node.getUserObject();
        }
        return object;
    }

    public DatabaseObject getSelectedObject() {
        return KeePassTree.getSelectedObject(this);
    }

    public DatabaseObject getAndSelectObjectAt(int Xlocation, int Ylocation) {
        TreePath selPath = getPathForLocation(Xlocation, Ylocation);
        getSelectionModel().setSelectionPath(selPath);
        return getSelectedObject();
    }

}
