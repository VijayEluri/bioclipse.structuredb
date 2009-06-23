/*******************************************************************************
 * Copyright (c) 2009  Arvid Berg <goglepox@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.cdk.jchempaint.handlers;

import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;


public class ModuleState extends State implements IExecutableExtension {

    public final static String COMMAND_ID = "net.bioclipse.cdk.ui.editors.jchempaint.command.module";
    public final static String STATE_ID = "net.bioclipse.cdk.jchempaint.moduleState";
    public final static String PARAMETER_ID = "jcp.controller.module";

    public ModuleState() {

    }

    public void setInitializationData( IConfigurationElement config,
                                       String propertyName, Object data )
                                                                         throws CoreException {

        if(data instanceof String ) {
            setValue(data);
        }

    }

    @Override
    public void setValue( Object value ) {

        if( ! (value instanceof String))
            return;// we set only String values

        super.setValue( value );
    }
}
