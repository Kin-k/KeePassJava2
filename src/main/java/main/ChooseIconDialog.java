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
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author SpecOp0
 */
public class ChooseIconDialog extends JOptionPane {

    private static final long serialVersionUID = 1L;

    public static int showChooseIconDialog(Component parentComponent, String title, int iconIndex) {
        int selectedIndex = -1;
        // load all images
        IconHelper.loadAllImages();
        // create a combobox with only non null images and search for current selected image
        int currentIndex = 0;
        int originalArrayIndex = 0;
        int currentSelectedIndex = 0;
        DefaultComboBoxModel<ImageIcon> imageIconsComboBoxModel = new DefaultComboBoxModel<>();
        JComboBox<ImageIcon> imageIconsComboBox = new JComboBox<>(imageIconsComboBoxModel);
        for (ImageIcon image : IconHelper.getImageIconList()) {
            if (null != image) {
                imageIconsComboBoxModel.addElement(image);
                if (originalArrayIndex == iconIndex) {
                    currentSelectedIndex = currentIndex;
                }
                currentIndex++;
            }
            originalArrayIndex++;
        }
        imageIconsComboBox.setSelectedIndex(currentSelectedIndex);
        imageIconsComboBox.setRenderer(new ImageIconCellRenderer());
        // let user select icon
        int result = showConfirmDialog(parentComponent, imageIconsComboBox, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            ImageIcon selectedImage = (ImageIcon) imageIconsComboBox.getSelectedItem();
            selectedIndex = IconHelper.indexOf(selectedImage);
        }
        return selectedIndex;
    }

}
