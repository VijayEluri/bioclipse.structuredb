/* *****************************************************************************
 * Copyright (c) 2009  Jonathan Alvarsson <jonalv@users.sourceforge.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * www.eclipse.org�epl-v10.html <http://www.eclipse.org/legal/epl-v10.html>
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.filestore.tests;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.util.Scanner;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import net.bioclipse.filestore.FileStore;

/**
 * @author jonalv
 *
 */
public class FileStoreTest {

    private FileStore fs;
    private UUID key;
    
    public static final String EXAMPLE_STRING = "example String";
    public static final String UPDATED_EXAMPLE_STRING = "updated String";
    
    @BeforeClass
    public static void cleanAwayFiles() throws URISyntaxException {
        File file = new File( FileStoreTest.class
                                           .getClassLoader()
                                           .getResource( "./testFolder" )
                                           .toURI() );
        for ( File child : file.listFiles() ) {
            if ( child.isDirectory() ) {
                deleteDirectory( child );
            }
        }
    }
    
    public static boolean deleteDirectory(File path) {
        if ( path.exists() ) {
            for ( File file : path.listFiles() ) {
                if ( file.isDirectory() ) {
                    deleteDirectory(file);
                }
                else {
                    file.delete();
                }
            }
        }
        return ( path.delete() );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void dontInitializeWithFile() throws URISyntaxException {
        new FileStore( 
            new File( this.getClass()
                          .getClassLoader()
                          .getResource( "./testFolder/testfile.txt" )
                          .toURI() ) );
    }
    
    @Test
    public void doInializeWithDirectory() throws URISyntaxException {
        fs = new FileStore( 
                 new File( this.getClass()
                               .getClassLoader()
                               .getResource( "./testFolder" )
                               .toURI() ) );
        assertNotNull(fs);
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void dontStoreNull() throws URISyntaxException {
        doInializeWithDirectory();
        fs.store( null );
    }
    
    @Test
    public void doStoreExampleString() throws URISyntaxException {
        doInializeWithDirectory();
        key = fs.store( EXAMPLE_STRING );
        assertNotNull( key );
    }
    
    @Test
    public void doRetrieveStored() throws URISyntaxException, IOException {
        doStoreExampleString();
        assertEquals( EXAMPLE_STRING, readInputStream( fs.retrieve( key ) ) );
    }
    
    private String readInputStream(InputStream in) throws IOException {

        StringBuilder result = new StringBuilder();
        BufferedInputStream bis = new BufferedInputStream(in);
        int read = bis.read();
        while ( read != -1 ) {
          result.append( (char)read );
          read = bis.read();
        }        
        return result.toString();
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void dontRetrieveNotStored() throws URISyntaxException {
        doStoreExampleString();
        UUID unstoredKey = UUID.randomUUID();
        fs.retrieve( unstoredKey );
    }
    
    @Test
    public void doStoreAFileAtCorrectPlace() throws URISyntaxException {
        doStoreExampleString();

        File f = locateFile(key); 
        assertTrue( f.exists() );
    }
    
    /**
     * @param key
     * @return
     */
    private File locateFile( UUID key ) {

        File root = fs.getRootFolder();
        
        String keyString = key.toString();

        StringBuffer directoryString = new StringBuffer();
        directoryString.append( root.getAbsolutePath() );
        for ( int i = 0 ; i < FileStore.RECURSION_DEPTH ; i++ ) {
            directoryString.append( File.separatorChar  );
            directoryString.append( keyString.charAt(i) );
        }
        return new File( directoryString.toString() + File.separatorChar
                         + keyString + ".txt" );
    }

    @Test
    public void doStoreAndDeleteFile() throws URISyntaxException {
        doStoreAFileAtCorrectPlace();
        fs.delete(key);
        File f = locateFile( key );
        assertFalse( f.exists() );
    }
    
    @Test
    public void doStoreAndUpdateFile() throws URISyntaxException {
        doStoreAFileAtCorrectPlace();
        assertEquals( EXAMPLE_STRING, 
                      new Scanner( fs.retrieve( key ) ).nextLine() );
        fs.update( key, UPDATED_EXAMPLE_STRING );
        assertEquals( UPDATED_EXAMPLE_STRING, 
                      new Scanner( fs.retrieve( key ) ).nextLine() );
    }
}
