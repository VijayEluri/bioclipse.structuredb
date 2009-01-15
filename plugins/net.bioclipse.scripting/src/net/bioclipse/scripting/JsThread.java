/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.scripting;

import java.util.LinkedList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class JsThread extends ScriptingThread {

    public static JsEnvironment js;
    private LinkedList<JsAction> actions;
    private static boolean busy;
    private volatile IProgressMonitor monitor;
    
    public void run() {
        js = new JsEnvironment();
        actions = new LinkedList<JsAction>();

        synchronized (actions) {
            while (true) {
                try {
                    while ( actions.isEmpty() )
                        actions.wait();
                } catch ( InterruptedException e ) {
                    break;
                }

                final JsAction nextAction = actions.removeFirst();
                final String[] result = new String[1];
                busy = true;
                final Boolean[] wait = { new Boolean(true) };
                final Boolean[] monitorIsSet = { new Boolean(false) };
                Job job = new Job("JS-script") {
                    @Override
                    protected IStatus run( IProgressMonitor m ) {

                        m.beginTask( "Running JavaScript", 
                                     IProgressMonitor.UNKNOWN );
                        monitor = m;
                        synchronized ( monitorIsSet ) {
                            monitorIsSet[0] = true;
                            monitorIsSet.notifyAll();
                        }
                        synchronized ( wait ) {
                            while(wait[0]) {
                                try {
                                    wait.wait();
                                } 
                                catch ( InterruptedException e ) {
                                    break;
                                }
                            }
                        }
                        return Status.OK_STATUS;
                    }
                };
                job.setUser( true );
                job.schedule();
                synchronized ( monitorIsSet ) {
                    while ( !monitorIsSet[0] ) {
                        try {
                            monitorIsSet.wait();
                        }
                        catch ( InterruptedException e ) {
                            break;
                        }
                    }
                }
                result[0] = js.eval( nextAction.getCommand() );
                
                synchronized ( wait ) {
                    wait[0] = false;
                    wait.notifyAll();
                }
                try {
                    job.join();
                } 
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                busy = false;
                
                nextAction.runPostCommandHook(result[0]);
            }
        }
    }
    
    public synchronized void enqueue(JsAction action) {
        while (actions == null) { // BIG UGLY HACK!
            try {
                Thread.sleep( 500 );
            } catch ( InterruptedException e ) {
            }
        }
        
        synchronized (actions) {
            actions.addLast( action );
            actions.notifyAll();
        }
    }
    
    public static synchronized boolean isBusy() {
        return busy;
    }
    
    public void enqueue(String command) {
        enqueue( new JsAction( command,
                               new Hook() { public void run(String s) {} } ) );
    }

    public synchronized IProgressMonitor getMonitor() {
        return monitor;
    }
}
