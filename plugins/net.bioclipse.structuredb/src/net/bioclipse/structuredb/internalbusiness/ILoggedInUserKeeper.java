/*******************************************************************************
 * Copyright (c) 2007 - 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.structuredb.internalbusiness;

import net.bioclipse.structuredb.domain.User;
import net.bioclipse.usermanager.IUserManagerListener;

/**
 * @author jonalv
 *
 */
public interface ILoggedInUserKeeper extends IUserManagerListener {

    public User getLoggedInUser();

    void setLoggedInUser(User root);
}
