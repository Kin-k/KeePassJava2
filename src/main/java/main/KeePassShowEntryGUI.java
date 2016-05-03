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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.TextEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static main.KeePassShowObjectGUI.PADDING_LEFT;
import org.bouncycastle.util.Arrays;
import org.linguafranca.pwdb.Entry;

/**
 *
 * @author SpecOp0
 */
public class KeePassShowEntryGUI extends KeePassShowObjectGUI {

    private static final long serialVersionUID = 1L;

    private final TextField titleField;
    private final TextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField passwordFieldRepeat;
    private final TextField passwordFieldClear;
    private final TextField passwordQuality;
    private final TextField urlField;
    private final JTextArea notesField;

    public KeePassShowEntryGUI(JFrame parent) {
        super(parent, "New Entry");

        // ==================
        // center information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        int index = 0;

        // title
        final int iconChooserWidth = 20;
        final int iconChooserPadding = 5;
        final int titleSize = FIELD_MAX_WIDTH - iconChooserWidth - iconChooserPadding;
        JLabel titleLabel = new JLabel("Title");
        titleLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        titleField = new TextField("");
        titleField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                titleSize,
                LABEL_HEIGHT);
        JButton iconChooserButton = new JButton(IconHelper.getImageIcon(1));
        iconChooserButton.setBounds(LABEL_WIDTH + PADDING_LEFT + titleSize + iconChooserPadding,
                getYoffset(index),
                iconChooserWidth,
                LABEL_HEIGHT);
        iconChooserButton.addActionListener((ActionEvent e) -> chooseIcon());
        centerPanel.add(titleLabel);
        centerPanel.add(titleField);
        centerPanel.add(iconChooserButton);
        index++;

        // username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        usernameField = new TextField("");
        usernameField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(usernameLabel);
        centerPanel.add(usernameField);
        index++;

        // password
        final int clearPasswordSwitcherWidth = 20;
        final int clearPasswordSwitcherPadding = 5;
        final int passwordFieldSize = FIELD_MAX_WIDTH - clearPasswordSwitcherWidth - clearPasswordSwitcherPadding;
        JLabel passwordLabel = new JLabel("Password");
        usernameLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        passwordField = new JPasswordField("");
        passwordField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                passwordFieldSize,
                LABEL_HEIGHT);
        passwordFieldClear = new TextField("");
        passwordFieldClear.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                passwordFieldSize,
                LABEL_HEIGHT);
        passwordFieldClear.setVisible(false);
        JButton clearPasswordSwitcherButton = new JButton();
        clearPasswordSwitcherButton.setBounds(LABEL_WIDTH + PADDING_LEFT + passwordFieldSize + clearPasswordSwitcherPadding,
                getYoffset(index),
                clearPasswordSwitcherWidth,
                LABEL_HEIGHT);
        clearPasswordSwitcherButton.addActionListener((ActionEvent e) -> switchPasswordVisibility());
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(passwordFieldClear);
        centerPanel.add(clearPasswordSwitcherButton);
        index++;

        // repeat field
        JLabel passwordRepeatLabel = new JLabel("Repeat");
        passwordRepeatLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        passwordFieldRepeat = new JPasswordField("");
        passwordFieldRepeat.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                passwordFieldSize,
                LABEL_HEIGHT);
        centerPanel.add(passwordRepeatLabel);
        centerPanel.add(passwordFieldRepeat);
        index++;

        passwordField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
        passwordFieldRepeat.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
        passwordFieldClear.addTextListener((TextEvent e) -> verifyClearPassword());

        // quality
        JLabel passwordQualityLabel = new JLabel("Quality");
        passwordQualityLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        passwordQuality = new TextField(computerPasswordQuality(passwordField.getPassword()));
        passwordQuality.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(passwordQualityLabel);
        centerPanel.add(passwordQuality);
        index++;

        // url
        JLabel urlLabel = new JLabel("URL");
        urlLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        urlField = new TextField("");
        urlField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(urlLabel);
        centerPanel.add(urlField);
        index++;

        // notes
        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        notesField = new JTextArea("");
        JScrollPane notesPane = new JScrollPane(notesField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        notesPane.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT * 6 + 5 * PADDING_TOP);
        notesField.setLineWrap(true);
        centerPanel.add(notesLabel);
        centerPanel.add(notesPane);
        index += 6;

        centerPanel.setPreferredSize(new Dimension(PADDING_LEFT + LABEL_WIDTH + FIELD_MAX_WIDTH, getYoffset(index)));
        this.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }

    public KeePassShowEntryGUI(Component parent, Entry entry) {
        super(parent, entry.getTitle());

        // ==================
        // center information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        int index = 0;

        // title
        final int iconChooserWidth = 20;
        final int iconChooserPadding = 5;
        final int titleSize = FIELD_MAX_WIDTH - iconChooserWidth - iconChooserPadding;
        JLabel titleLabel = new JLabel("Title");
        titleLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        titleField = new TextField(entry.getTitle());
        titleField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                titleSize,
                LABEL_HEIGHT);
        JButton iconChooserButton = new JButton(IconHelper.getImageIcon(1));
        iconChooserButton.setBounds(LABEL_WIDTH + PADDING_LEFT + titleSize + iconChooserPadding,
                getYoffset(index),
                iconChooserWidth,
                LABEL_HEIGHT);
        iconChooserButton.addActionListener((ActionEvent e) -> chooseIcon());
        setIconIndex(entry.getIcon().getIndex());
        centerPanel.add(titleLabel);
        centerPanel.add(titleField);
        centerPanel.add(iconChooserButton);
        index++;

        // username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        usernameField = new TextField(entry.getUsername());
        usernameField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(usernameLabel);
        centerPanel.add(usernameField);
        index++;

        // password
        final int clearPasswordSwitcherWidth = 20;
        final int clearPasswordSwitcherPadding = 5;
        final int passwordFieldSize = FIELD_MAX_WIDTH - clearPasswordSwitcherWidth - clearPasswordSwitcherPadding;
        JLabel passwordLabel = new JLabel("Password");
        usernameLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        passwordField = new JPasswordField(entry.getPassword());
        passwordField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                passwordFieldSize,
                LABEL_HEIGHT);
        passwordFieldClear = new TextField(entry.getPassword());
        passwordFieldClear.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                passwordFieldSize,
                LABEL_HEIGHT);
        passwordFieldClear.setVisible(false);
        JButton clearPasswordSwitcherButton = new JButton();
        clearPasswordSwitcherButton.setBounds(LABEL_WIDTH + PADDING_LEFT + passwordFieldSize + clearPasswordSwitcherPadding,
                getYoffset(index),
                clearPasswordSwitcherWidth,
                LABEL_HEIGHT);
        clearPasswordSwitcherButton.addActionListener((ActionEvent e) -> switchPasswordVisibility());
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(passwordFieldClear);
        centerPanel.add(clearPasswordSwitcherButton);
        index++;

        // repeat field
        JLabel passwordRepeatLabel = new JLabel("Repeat");
        passwordRepeatLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        passwordFieldRepeat = new JPasswordField(entry.getPassword());
        passwordFieldRepeat.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                passwordFieldSize,
                LABEL_HEIGHT);
        centerPanel.add(passwordRepeatLabel);
        centerPanel.add(passwordFieldRepeat);
        index++;

        passwordField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
        passwordFieldRepeat.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifyPassword();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

        });
        passwordFieldClear.addTextListener((TextEvent e) -> verifyClearPassword());

        // quality
        JLabel passwordQualityLabel = new JLabel("Quality");
        passwordQualityLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        passwordQuality = new TextField(computerPasswordQuality(passwordField.getPassword()));
        passwordQuality.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(passwordQualityLabel);
        centerPanel.add(passwordQuality);
        index++;

        // url
        JLabel urlLabel = new JLabel("URL");
        urlLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        urlField = new TextField(entry.getUrl());
        urlField.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT);
        centerPanel.add(urlLabel);
        centerPanel.add(urlField);
        index++;

        // notes
        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setBounds(0 + PADDING_LEFT,
                getYoffset(index),
                LABEL_WIDTH,
                LABEL_HEIGHT);
        notesField = new JTextArea(entry.getNotes());
        JScrollPane notesPane = new JScrollPane(notesField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        notesPane.setBounds(LABEL_WIDTH + PADDING_LEFT,
                getYoffset(index),
                FIELD_MAX_WIDTH,
                LABEL_HEIGHT * 6 + 5 * PADDING_TOP);
        notesField.setLineWrap(true);
        centerPanel.add(notesLabel);
        centerPanel.add(notesPane);
        index += 6;

        centerPanel.setPreferredSize(new Dimension(PADDING_LEFT + LABEL_WIDTH + FIELD_MAX_WIDTH, getYoffset(index)));
        this.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }

    @Override
    protected void saveInputToObject(DatabaseObject object) {
        // todo check for matching password!
        if (null != object && object.isEntry()) {
            Entry entry = object.getEntry();
            // update if values change (avoid setDirty = true)
            setSaveObject(true);
            if (!entry.getTitle().equals(getTitleField().getText())) {
                entry.setTitle(getTitleField().getText());
            }
            if (!entry.getUsername().equals(getUsernameField().getText())) {
                entry.setUsername(getUsernameField().getText());
            }
            // check which password is visible
            boolean isClearPasswordVisible = getPasswordFieldClear().isVisible();
            String password;
            if (isClearPasswordVisible) {
                password = getPasswordFieldClear().getText();
            } else {
                password = String.copyValueOf(getPasswordField().getPassword());
            }
            if (!entry.getPassword().equals(password)) {
                entry.setPassword(password);
            }
            if (!entry.getUrl().equals(getUrlField().getText())) {
                entry.setUrl(getUrlField().getText());
            }
            if (entry.getIcon().getIndex() != getIconIndex()) {
                entry.getIcon().setIndex(getIconIndex());
            }
            // test if note has been changed (null and new content or not null and note has changed)
            if ((null == entry.getNotes() && !getNotesField().getText().isEmpty())
                    || (null != entry.getNotes() && !entry.getNotes().equals(getNotesField().getText()))) {
                entry.setNotes(getNotesField().getText());
            }
        }
    }

    private boolean verifyPassword() {
        boolean passwordMatch = false;
        if (Arrays.areEqual(getPasswordField().getPassword(), getPasswordFieldRepeat().getPassword())) {
            getPasswordFieldRepeat().setBackground(Color.WHITE);
            passwordMatch = true;
        } else {
            getPasswordFieldRepeat().setBackground(new Color(255, 192, 192));
        }
        getPasswordQuality().setText(computerPasswordQuality(getPasswordField().getPassword()));
        return passwordMatch;
    }

    private void verifyClearPassword() {
        getPasswordQuality().setText(computerPasswordQuality(getPasswordFieldClear().getText().toCharArray()));
    }

    private void switchPasswordVisibility() {
        boolean isClearPasswordVisible = getPasswordFieldClear().isVisible();
        String password;
        if (isClearPasswordVisible) {
            password = getPasswordFieldClear().getText();
            getPasswordField().setText(password);
            getPasswordFieldRepeat().setText(password);
        } else {
            password = String.copyValueOf(getPasswordField().getPassword());
            getPasswordFieldClear().setText(password);
        }
        getPasswordField().setVisible(isClearPasswordVisible);
        getPasswordFieldRepeat().setVisible(isClearPasswordVisible);
        getPasswordFieldClear().setVisible(!isClearPasswordVisible);
    }

    private String computerPasswordQuality(char[] password) {
        return "123 bit";
//        return String.valueOf(password);
    }

    @Override
    protected void setSaveObjectFlag() {
        if (!getPasswordField().isVisible() || verifyPassword()) {
            setSaveObject(true);
            dispose();
        } else {
            KeePassGUI.showWarning("Passwords do not match", "Can not save the Entry because the passwords do not match.", this);
        }
    }

    // getter and setter
    public TextField getTitleField() {
        return titleField;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JPasswordField getPasswordFieldRepeat() {
        return passwordFieldRepeat;
    }

    public TextField getPasswordFieldClear() {
        return passwordFieldClear;
    }

    public TextField getPasswordQuality() {
        return passwordQuality;
    }

    public TextField getUrlField() {
        return urlField;
    }

    public JTextArea getNotesField() {
        return notesField;
    }
}
