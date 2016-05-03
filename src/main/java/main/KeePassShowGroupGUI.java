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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.linguafranca.pwdb.Group;

/**
 *
 * @author SpecOp0
 */
public class KeePassShowGroupGUI extends KeePassShowObjectGUI {

    private static final long serialVersionUID = 1L;

    private final TextField nameField;

    public KeePassShowGroupGUI(JFrame parent) {
        super(parent, "New Group");

        // ==================
        // center information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        int index = 0;

        // title
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        nameField = new TextField("");
        nameField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(nameLabel);
        centerPanel.add(nameField);
        index++;

        // icon
        final int iconChooserWidth = 20;
        JLabel iconLabel = new JLabel("Icon");
        iconLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        JButton iconChooserButton = new JButton(IconHelper.getImageIcon(1));
        iconChooserButton.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                iconChooserWidth,
                LABEL_HEIGHT);
        iconChooserButton.addActionListener((ActionEvent e) -> chooseIcon());
        centerPanel.add(iconLabel);
        centerPanel.add(iconChooserButton);
        index++;

        centerPanel.setPreferredSize(new Dimension(PADDING_LEFT + LABEL_WIDTH + FIELD_MAX_WIDTH, getYoffset(index)));
        this.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }

    public KeePassShowGroupGUI(JFrame parent, Group group) {
        super(parent, group.getName());

        // ==================
        // center information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        int index = 0;

        // title
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        nameField = new TextField(group.getName());
        nameField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(nameLabel);
        centerPanel.add(nameField);
        index++;

        // icon
        final int iconChooserWidth = 20;
        JLabel iconLabel = new JLabel("Icon");
        iconLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        JButton iconChooserButton = new JButton(IconHelper.getImageIcon(1));
        iconChooserButton.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                iconChooserWidth,
                LABEL_HEIGHT);
        iconChooserButton.addActionListener((ActionEvent e) -> chooseIcon());
        setIconIndex(group.getIcon().getIndex());
        centerPanel.add(iconLabel);
        centerPanel.add(iconChooserButton);
        index++;

        centerPanel.setPreferredSize(new Dimension(PADDING_LEFT + LABEL_WIDTH + FIELD_MAX_WIDTH, getYoffset(index)));
        this.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }

    @Override
    protected void saveInputToObject(DatabaseObject object) {
        if (null != object && object.isGroup()) {
            Group group = object.getGroup();
            // update if values change (avoid setDirty = true)
            setSaveObject(true);
            if (!group.getName().equals(getNameField().getText())) {
                group.setName(getNameField().getText());
            }
            if (group.getIcon().getIndex() != getIconIndex()) {
                group.getIcon().setIndex(getIconIndex());
            }
        }
    }

    // getter and setter
    public TextField getNameField() {
        return nameField;
    }

}
