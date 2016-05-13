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
package view;

import helper.IRobot;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.DatabaseObject;

/**
 *
 * @author SpecOp0
 */
public abstract class KeePassShowObjectGUI extends JFrame {

    protected final JButton saveButton;
    protected final JButton exitButton;
    protected boolean saveObject = false;
    protected int iconIndex = 0;

    // final int values to create GUI
    protected static final int LABEL_WIDTH = 85;
    protected static final int LABEL_HEIGHT = 20;

    protected static final int FIELD_MAX_WIDTH = 380;

    protected static final int PADDING_TOP = 10;
    protected static final int PADDING_ELEMENT_TOP = 10;
    protected static final int PADDING_LEFT = 5;

    protected int getYoffset(int index) {
        return PADDING_TOP + index * (LABEL_HEIGHT + PADDING_ELEMENT_TOP);
    }

    public KeePassShowObjectGUI(Component parent, String title) {
        super(title);
        this.setLayout(new BorderLayout());

        // ==============
        // bottom buttons
        saveButton = new JButton("Save");
        saveButton.addActionListener((ActionEvent e) -> setSaveObjectFlag());
        exitButton = new JButton("Cancel");
        exitButton.addActionListener((ActionEvent e) -> dispose());

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(exitButton);

        this.add(bottomButtonPanel, BorderLayout.PAGE_END);

        // frame settings
        this.setSize(500, 450);
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bottomButtonPanel.registerKeyboardAction((ActionEvent e) -> dispose(), IRobot.STROKE_ESCAPE, JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.setVisible(true);
    }

    protected void setSaveObjectFlag() {
        setSaveObject(true);
        dispose();
    }

    public abstract void saveInputToObject(DatabaseObject object);

    protected void chooseIcon() {
        int selectedIndex = ChooseIconDialog.showChooseIconDialog(this, "Choose Icon", getIconIndex());
        if (selectedIndex != -1) {
            setIconIndex(selectedIndex);
        }
    }

    // getter and setter
    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public boolean isSaveObject() {
        return saveObject;
    }

    public void setSaveObject(boolean saveObject) {
        this.saveObject = saveObject;
    }

    public int getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }

}
