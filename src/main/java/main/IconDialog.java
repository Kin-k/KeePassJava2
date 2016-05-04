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

import helper.IRobot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;

/**
 *
 * @author SpecOp0
 */
public class IconDialog extends JDialog {

    private final ButtonGroup buttons;
    private final List<AbstractButton> buttonList;

    private final Color backgroundDefault;
    private static final Color BACKGORUND_SELECTION = new Color(153, 255, 153);

    private int closingAction = JOptionPane.CANCEL_OPTION;

    public IconDialog(Frame owner, String title, int iconIndex) {
        super(owner, title, true);
        // create icon buttons

        // load all images
        IconHelper.loadAllImages();
        // create a radiobuttons representing the image
        int originalArrayIndex = 0;
        ButtonGroup buttonGroup = new ButtonGroup();
        boolean buttonSelected = false;
        for (ImageIcon image : IconHelper.getImageIconList()) {
            if (null != image) {
                // create radio button
                JRadioButton button;
                // test wehter image is selected
                if (originalArrayIndex == iconIndex) {
                    button = new JRadioButton(image.getDescription(), image, true);
                    buttonSelected = true;
                } else {
                    button = new JRadioButton(image.getDescription(), image);
                }
                button.addFocusListener(new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent e) {
                        button.setSelected(true);
                        highlightSelected((Component) e.getSource());
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        button.setSelected(false);
                    }
                });
                // set information of image and add to group
                button.setActionCommand(button.getText());
                buttonGroup.add(button);
            }
            originalArrayIndex++;
        }
        this.buttons = buttonGroup;
        this.buttonList = Collections.list(buttonGroup.getElements());
        if (!buttonSelected) {
            buttonList.get(0).setSelected(true);
        }

        // create GUI
        getRootPane().setLayout(new BorderLayout());

        // icon panel
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new GridLayout(10, 13));
        backgroundDefault = buttonGroup.getElements().nextElement().getBackground();
        for (AbstractButton button : buttonList) {
            iconPanel.add(button);
            button.addActionListener((ActionEvent e) -> highlightSelected((Component) e.getSource()));
        }
        getRootPane().add(iconPanel, BorderLayout.CENTER);

        // OK and CANCEL button
        JButton okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> saveAndClose());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getRootPane().add(buttonPanel, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(owner);
        // pre select button
        buttonList.stream().filter((button) -> (button.isSelected())).forEach((button) -> {
            button.requestFocusInWindow();
        });
    }

    @Override
    protected JRootPane createRootPane() {
        JRootPane root = super.createRootPane();
        root.registerKeyboardAction((ActionEvent e) -> saveAndClose(), IRobot.STROKE_ENTER, JComponent.WHEN_IN_FOCUSED_WINDOW);
        root.registerKeyboardAction((ActionEvent e) -> dispose(), IRobot.STROKE_ESCAPE, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return root;
    }

    private void saveAndClose() {
        setClosingAction(JOptionPane.OK_OPTION);
        dispose();
    }

    private void highlightSelected(Component component) {
        getButtonList().stream().forEach((button) -> {
            button.setBackground(getBackgroundDefault());
        });
        component.setBackground(BACKGORUND_SELECTION);
    }

    public int getSelectedButton() {
        for (AbstractButton button : Collections.list(buttons.getElements())) {
            if (button.isSelected()) {
                String actionCommand = getButtons().getSelection().getActionCommand();
                try {
                    int index = Integer.parseInt(actionCommand);
                    return index;
                } catch (NumberFormatException ex) {
                    return -1;
                }
            }
        }
        return -1;
    }

    // getter and setter
    public ButtonGroup getButtons() {
        return buttons;
    }

    public List<AbstractButton> getButtonList() {
        return buttonList;
    }

    public Color getBackgroundDefault() {
        return backgroundDefault;
    }

    public int getClosingAction() {
        return closingAction;
    }

    public void setClosingAction(int closingAction) {
        this.closingAction = closingAction;
    }

}
