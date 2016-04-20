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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author SpecOp0
 */
public class KeePassTreeRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject().getClass().equals(DatabaseObject.class)) {
            DatabaseObject databaseObject = (DatabaseObject) node.getUserObject();
            if (databaseObject.isGroup()) {
                ImageIcon icon = IconHelper.getImageIcon(databaseObject.getGroup().getIcon());
                if (null != icon) {
                    setIcon(icon);
                } else {
                    if (expanded) {
                        setIcon(openIcon);
                    } else {
                        setIcon(closedIcon);
                    }
                }
            } else if (databaseObject.isEntry()) {
                ImageIcon icon = IconHelper.getImageIcon(databaseObject.getEntry().getIcon());
                if (null != icon) {
                    setIcon(icon);
                } else {
                    setIcon(leafIcon);
                }
            }
        }
        return this;
    }

}
