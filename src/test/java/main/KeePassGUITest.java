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
import java.awt.AWTException;
import java.awt.Point;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author SpecOp0
 */
public class KeePassGUITest {

    public KeePassGUITest() {
    }

    public static KeePassGUI createKeePassGUI() {
        // empty path (or popup at start)
        DatabasePathTest.createPathFile("");
        KeePassTree tree = new KeePassTree("root");
        KeePassTableModel tableModel = new KeePassTableModel();
        KeePassGUI gui = new KeePassGUI("KeePass 2", tree, tableModel);
        tableModel.addTableModelListener(gui);
        return gui;
    }

    /**
     * Test of setEnabledAllButtons method, of class KeePassGUI.
     */
    @Test
    public void testSetEnabledAllButtons() {
        System.out.println("setEnabledAllButtons");
        boolean enabled = false;
        KeePassGUI instance = createKeePassGUI();
        instance.setEnabledAllButtons(enabled);
        for (ActionButton button : instance.getTopPanelButtons()) {
            if (ActionTypeHelper.isAlwaysActive(button)) {
                assertEquals(true, button.isEnabled());
            } else {
                assertEquals(enabled, button.isEnabled());
            }
        }
        enabled = true;
        instance.setEnabledAllButtons(enabled);
        for (ActionButton button : instance.getTopPanelButtons()) {
            assertEquals(true, button.isEnabled());
        }
    }

    /**
     * Test of chooseFile method, of class KeePassGUI.
     */
    @Test
    public void testChooseFile_0args() throws AWTException, InterruptedException {
        System.out.println("chooseFile");
        KeePassGUI instance = createKeePassGUI();
        String path = this.getClass().getClassLoader().getResource("test.kdbx").getFile();
        File expResult = new File(path);
        // start gui
        IThread worker = new IThread(instance) {

            @Override
            public Object runGUITest() {
                return instance.chooseFile();
            }
        };
        worker.start();
        // wait for GUI window to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(KeePassGUITest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // get offset to chooser window
        Point location = instance.getLocation();
        IRobot robot = new IRobot();
        int xOffset = (int) location.getX() + 142;
        int yOffset = (int) location.getY() + 67;
        // source folder
        robot.mouseMove(xOffset + 40, yOffset + 110);
        robot.doubleClick();
        // test folder
        robot.mouseMove(xOffset + 40, yOffset + 110);
        robot.doubleClick();
        // resources folder
        robot.mouseMove(xOffset + 40, yOffset + 110);
        robot.doubleClick();
        // test.kdbx file
        robot.mouseMove(xOffset + 40, yOffset + 110);
        robot.click();
        // ok button
        robot.mouseMove(xOffset + 350, yOffset + 330);
        robot.click();
        worker.join();
        // test.kdbx copied to /target/test-classes -> compare length of files
        assertEquals(expResult.length(), ((File) worker.result).length());
    }

    /**
     * Test of chooseFile method, of class KeePassGUI.
     */
    @Test
    public void testChooseFile_String() throws AWTException, InterruptedException {
        System.out.println("chooseFile");
        KeePassGUI instance = createKeePassGUI();
        String path = this.getClass().getClassLoader().getResource("test.kdbx").getFile();
        DatabasePathTest.createPathFile(path);
        File expResult = new File(path);
        // start gui
        IThread worker = new IThread(instance) {

            @Override
            public Object runGUITest() {
                return instance.chooseFile(path);
            }
        };
        worker.start();
        // wait for GUI window to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(KeePassGUITest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // get offset to chooser window
        Point location = instance.getLocation();
        IRobot robot = new IRobot();
        int xOffset = (int) location.getX() + 142;
        int yOffset = (int) location.getY() + 67;
        // test.kdbx file
        robot.mouseMove(xOffset + 40, yOffset + 160);
        robot.click();
        // ok button
        robot.mouseMove(xOffset + 350, yOffset + 330);
        robot.click();
        worker.join();
        // test.kdbx copied to /target/test-classes -> compare length of files
        assertEquals(expResult, worker.result);
    }

    /**
     * Test of enterPassword method, of class KeePassGUI.
     */
    @Test
    public void testEnterPassword() throws InterruptedException, AWTException {
        System.out.println("enterPassword");
        KeePassGUI instance = createKeePassGUI();
        String password = "test1234";
        byte[] expResult = password.getBytes();
        // start gui
        IThread worker = new IThread(instance, 1000000000) {

            @Override
            public Object runGUITest() {
                return instance.enterPassword();
            }
        };
        worker.start();
        // wait for GUI window to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(KeePassGUITest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // get offset to password window
        Point location = instance.getLocation();
        IRobot robot = new IRobot();
        int xOffset = (int) location.getX() + 259;
        int yOffset = (int) location.getY() + 186;
        // select password field
        robot.mouseMove(xOffset + 140, yOffset + 55);
        robot.click();
        // enter password
        robot.type(password);
        // click ok button
        robot.mouseMove(xOffset + 100, yOffset + 100);
        robot.click();
        worker.join();
        byte[] result = (byte[]) worker.result;
        assertArrayEquals(expResult, result);
    }
//
//    /**
//     * Test of tableChanged method, of class KeePassGUI.
//     */
//    @Test
//    public void testTableChanged() {
//        System.out.println("tableChanged");
//        TableModelEvent e = null;
//        KeePassGUI instance = null;
//        instance.tableChanged(e);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of copy method, of class KeePassGUI.
//     */
//    @Test
//    public void testCopy() {
//        System.out.println("copy");
//        boolean isUsername = false;
//        KeePassGUI instance = null;
//        instance.copy(isUsername);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//

    public abstract class IThread extends Thread {

        public Object result = null;
        private long maximumExecutionTime = 0;
        private final KeePassGUI instance;
        private final long sleepInterval = 1000;

        public IThread(KeePassGUI instance) {
            super();
            this.instance = instance;
            maximumExecutionTime = 5 * sleepInterval;
        }

        public IThread(KeePassGUI instance, long maximumExecutionTime) {
            super();
            this.instance = instance;
            this.maximumExecutionTime = maximumExecutionTime;
        }

        @Override
        public void run() {
            Thread worker = new Thread() {

                @Override
                public void run() {
                    result = runGUITest();
                }
            };
            worker.start();
            for (long sleepTime = 0; sleepTime <= maximumExecutionTime; sleepTime += sleepInterval) {
                try {
                    worker.join(sleepInterval);
                } catch (InterruptedException ex) {
                }
            }
            instance.dispose();
        }

        public abstract Object runGUITest();

    }
}
