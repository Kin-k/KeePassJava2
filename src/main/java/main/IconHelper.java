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

import java.net.URL;
import javax.swing.ImageIcon;
import org.linguafranca.pwdb.Icon;

/**
 *
 * @author SpecOp0
 */
public class IconHelper {

    public static final int NUMBER_OF_ICON = 69;
    private static final ImageIcon[] imageIconList = new ImageIcon[NUMBER_OF_ICON];
    private static int imagesLoaded = 0;

    public static ImageIcon getImageIcon(Icon icon) {
        // load image (manual lazy load)
        ImageIcon imageIcon = null;
        if (null != icon && icon.getIndex() >= 0 && icon.getIndex() < NUMBER_OF_ICON) {
            imageIcon = getImageIcon(icon.getIndex());
        }
        return imageIcon;
    }

    public static ImageIcon getImageIcon(int index) {
        ImageIcon imageIcon = getImageIconList()[index];
        if (null == imageIcon) {
            String imagePath = String.format("icons/database/%02d.png", index);
            URL ressource = ActionTypeHelper.class.getClassLoader().getResource(imagePath);
            if (null != ressource) {
                imageIcon = new ImageIcon(ressource, imagePath);
                imageIcon.setDescription(String.format("%02d", index));
                getImageIconList()[index] = imageIcon;
                setImagesLoaded(getImagesLoaded() + 1);
            }
        }
        return imageIcon;
    }

    public static void loadAllImages() {
        if (getImagesLoaded() < NUMBER_OF_ICON) {
            for (int i = 0; i < NUMBER_OF_ICON; i++) {
                getImageIcon(i);
            }
        }
    }

    public static int indexOf(ImageIcon image) {
        int index = -1;
        for (int i = 0; i < NUMBER_OF_ICON; i++) {
            if (null != getImageIconList()[i] && image == getImageIconList()[i]) {
                return i;
            }
        }
        return index;
    }

    public static ImageIcon[] getImageIconList() {
        return imageIconList;
    }

    public static int getImagesLoaded() {
        return imagesLoaded;
    }

    public synchronized static void setImagesLoaded(int aImagesLoaded) {
        imagesLoaded = aImagesLoaded;
    }

}
