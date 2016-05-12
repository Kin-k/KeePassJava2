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
import org.junit.Test;
import static org.junit.Assert.*;
import org.linguafranca.pwdb.Icon;

/**
 *
 * @author SpecOp0
 */
public class IconHelperTest {

    public IconHelperTest() {
    }

    /**
     * Test of getImageIcon method, of class IconHelper.
     */
    @Test
    public void testGetImageIcon() {
        System.out.println("getImageIcon");
        Icon icon = null;
        ImageIcon expResult = null;
        // null test
        ImageIcon result = IconHelper.getImageIcon(icon);
        assertEquals(expResult, result);
        // under and overflow test
        int[] iconIndices = {-1, 100};
        for (int index : iconIndices) {
            icon = new IIcon(index);
            result = IconHelper.getImageIcon(icon);
            assertEquals(expResult, result);
        }
        // load entry image (00.png)
        int index = 0;
        icon = new IIcon(index);
        result = IconHelper.getImageIcon(icon);
        String imageName = String.format("icons/database/%02d.png", index);
        URL resource = ActionTypeHelper.class.getClassLoader().getResource(imageName);
        expResult = new ImageIcon(resource, imageName);
        assertEquals(expResult.getImage(), result.getImage());
        // test caching
        expResult = IconHelper.getImageIcon(icon);
        assertSame(expResult, result);
    }

    private class IIcon implements Icon {

        private int index;

        public IIcon(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public void setIndex(int index) {
            this.index = index;
        }

    }

}
