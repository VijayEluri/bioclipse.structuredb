package net.bioclipse.jseditor;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.IConsoleFactory;

/*
 * This file is part of the Bioclipse Javascript Editor Plug-in.
 * 
 * Copyright (c) 2008 Johannes Wagener.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Wagener - initial API and implementation
 */
public class RhinoConsole implements IConsoleFactory {
        private static MessageConsole messageConsole = null;
        private static String consoleName = "Rhino Javascript Console";
        private static MessageConsoleStream out = null,
                                                                                out_blue = null,
                                                                                out_red = null;
        public void openConsole() {
                show();
        }
        public static void show() {
                IWorkbench wb = PlatformUI.getWorkbench();
                IWorkbenchPage wbPage = wb.getActiveWorkbenchWindow().getActivePage(); 
        if (wbPage != null) {
        	try {
        		IConsoleView conView = (IConsoleView) wbPage.showView(
                                                IConsoleConstants.ID_CONSOLE_VIEW);
        		conView.display(getRhinoConsole());
        	} catch (PartInitException e) {
        		PluginLogger.log("Console.show() - PartInitException: " + e.getMessage());
        	}
        }
        }
        public static MessageConsole getRhinoConsole() {
                if (messageConsole == null) {
                        messageConsole = findConsole(consoleName);
                }
                return messageConsole;
        }
        private static MessageConsole findConsole(String name) {
                ConsolePlugin conPlugin = ConsolePlugin.getDefault();
                IConsoleManager conManager = conPlugin.getConsoleManager();
                IConsole[] consAll = conManager.getConsoles();
                for (int i = 0; i < consAll.length; i++)
                        if (name.equals(consAll[i].getName()))
                                return (MessageConsole) consAll[i];
                //no console found, so we create a new one
                MessageConsole rhinoConsole = new MessageConsole(name, null);
                conManager.addConsoles(new IConsole[]{rhinoConsole});
                return rhinoConsole;
        }
        public static void writeToConsole(final String message) {
                Runnable r = new Runnable() {
                        public void run() {
                                getConsoleStream().println(message);
                        }
                };
                Display.getDefault().asyncExec(r);
        }
        public static void writeToConsoleBlue(final String message) {
                Runnable r = new Runnable() {
                        public void run() {
                                getConsoleStreamBlue().println(message);
                        }
                };
                Display.getDefault().asyncExec(r);
        }
        public static void writeToConsoleRed(final String message) {
                Runnable r = new Runnable() {
                        public void run() {
                                getConsoleStreamRed().println(message);
                        }
                };
                Display.getDefault().asyncExec(r);
        }
        // with time-stamp
        public static void writeToConsoleBlueT(String message) {
                writeToConsoleBlue(getCurrentTime() + " " + message);
        }
        // with time-stamp
        public static void writeToConsoleT(String message) {
                writeToConsole(getCurrentTime() + " " + message);
        }
        // with time-stamp
        public static void writeToConsoleRedT(String message) {
                writeToConsoleRed(getCurrentTime() + " " + message);
        }
        private static MessageConsoleStream getConsoleStream() {
                if (out == null)
                        out = getRhinoConsole().newMessageStream();
                return out;
        }
        private static MessageConsoleStream getConsoleStreamBlue() {
                if (out_blue == null) {
                        Color color_blue = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
                        out_blue = getRhinoConsole().newMessageStream();
                        out_blue.setColor(color_blue);
                }
                return out_blue;
        }
        private static MessageConsoleStream getConsoleStreamRed() {
                if (out_red == null) {
                        Color color_red = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED);
                        out_red = getRhinoConsole().newMessageStream();
                        out_red.setColor(color_red);
                }
                return out_red;
        }
        private static String getCurrentTime() {
                SimpleDateFormat simpleDateForm = new SimpleDateFormat("hh:mm:ss");
                Date current = new Date();
                current.setTime(System.currentTimeMillis());
                return simpleDateForm.format(current);
        }
}