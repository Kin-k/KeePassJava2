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

import helper.ActionTypeHelper;
import java.net.URL;
import javax.swing.ImageIcon;
import org.linguafranca.pwdb.Icon;

/**
 *
 * Static class which loads and saves all Icons of the DatabaseModel (lazy
 * loading) and provides useful functions for loading.
 *
 * @author SpecOp0
 */
public class IconHelper {

    public static final int NUMBER_OF_ICON = 69;
    private static final ImageIcon[] imageIconList = new ImageIcon[NUMBER_OF_ICON];
    private static int imagesLoaded = 0;

    private static ImageIcon passwordShowIcon = null;
    private static ImageIcon passwordHideIcon = null;

    /**
     * Loads ImageIcon for given Icon (Icon is from Database Model and contains
     * Index). Returns null if Image not found or index out of range.
     *
     * @param icon Icon from Database Model (contains index of image)
     * @return ImageIcon of Icon or null if not found / out of range
     */
    public static ImageIcon getImageIcon(Icon icon) {
        // load image (manual lazy load)
        ImageIcon imageIcon = null;
        if (null != icon && icon.getIndex() >= 0 && icon.getIndex() < NUMBER_OF_ICON) {
            imageIcon = getImageIcon(icon.getIndex());
        }
        return imageIcon;
    }

    /**
     * Loads ImageIcon for given index (index represents Icon in Database
     * Model). Returns null if Image not found or index out of range.
     *
     * @param index index of Icon (represents Icon in Database Model)
     * @return ImageIcon of Icon or null if not found / out of range
     */
    public static ImageIcon getImageIcon(int index) {
        ImageIcon imageIcon = getImageIconList()[index];
        if (null == imageIcon) {
            String imagePath = String.format("icons/database/%02d.png", index);
            URL resource = ActionTypeHelper.class.getClassLoader().getResource(imagePath);
            if (null != resource) {
                imageIcon = new ImageIcon(resource, imagePath);
                imageIcon.setDescription(String.format("%02d", index));
                getImageIconList()[index] = imageIcon;
                setImagesLoaded(getImagesLoaded() + 1);
            }
        }
        return imageIcon;
    }

    /**
     * Loads all Images. (No IO if Image already loaded)
     */
    public static void loadAllImages() {
        if (getImagesLoaded() < NUMBER_OF_ICON) {
            for (int i = 0; i < NUMBER_OF_ICON; i++) {
                getImageIcon(i);
            }
        }
    }

    /**
     * Searches the loaded Images for given ImageIcon and returns its index in
     * the Database Model.
     *
     * @param image ImageIcon to get index for
     * @return index of icon in Database Model if image was loaded, -1 if not
     * found
     */
    public static int indexOf(ImageIcon image) {
        int index = -1;
        for (int i = 0; i < NUMBER_OF_ICON; i++) {
            if (null != getImageIconList()[i] && image == getImageIconList()[i]) {
                return i;
            }
        }
        return index;
    }

    /**
     * Loads ImageIcon for displaying the Password in Clear Text or with
     * Password Field. If the Password is in Clear Text the returned Icon will
     * be an Icon for hiding the Password (Password Field).
     *
     * @param isPasswordVisible true if password is visible / in clear text
     * @return ImageIcon to hide Password (Password Field) if password is
     * visible
     */
    public static ImageIcon getPasswordIcon(boolean isPasswordVisible) {
        ImageIcon imageIcon = null;
        if (isPasswordVisible) {
            if (null == passwordHideIcon) {
                URL resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/password-show-off.png");
                if (null != resource) {
                    passwordHideIcon = new ImageIcon(resource, "Hide Password");
                }
            }
            imageIcon = passwordHideIcon;
        } else {
            if (null == passwordShowIcon) {
                URL resource = ActionTypeHelper.class.getClassLoader().getResource("icons/actions/password-show-on.png");
                if (null != resource) {
                    passwordShowIcon = new ImageIcon(resource, "Show Password");
                }
            }
            imageIcon = passwordShowIcon;
        }
        return imageIcon;
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
