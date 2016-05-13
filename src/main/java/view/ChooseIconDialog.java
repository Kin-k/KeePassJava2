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

import java.awt.Component;
import java.awt.Frame;
import javax.swing.JOptionPane;

/**
 *
 * @author SpecOp0
 */
public class ChooseIconDialog extends JOptionPane {

    private static final long serialVersionUID = 1L;

    public static int showChooseIconDialog(Component parentComponent, String title, int iconIndex) {
        int selectedIndex = -1;
        // let user select icon
        IconDialog iconDialog = new IconDialog((Frame) parentComponent, title, iconIndex);
        iconDialog.setVisible(true);
        if (iconDialog.getClosingAction() == JOptionPane.OK_OPTION) {
            selectedIndex = iconDialog.getSelectedButton();
        }
        return selectedIndex;
    }

}
