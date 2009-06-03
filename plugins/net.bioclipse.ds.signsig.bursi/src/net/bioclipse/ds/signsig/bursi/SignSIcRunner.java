/*******************************************************************************
 * Copyright (c) 2009 Ola Spjuth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ola Spjuth - initial API and implementation
 ******************************************************************************/
package net.bioclipse.ds.signsig.bursi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import net.bioclipse.cdk.business.Activator;
import net.bioclipse.cdk.business.CDKManager;
import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.core.util.LogUtils;
import net.bioclipse.ds.model.AbstractWarningTest;
import net.bioclipse.ds.model.IDSTest;
import net.bioclipse.ds.model.ITestResult;
import net.bioclipse.ds.model.SubStructureMatch;
import net.bioclipse.ds.model.impl.DSException;


public class SignSIcRunner extends AbstractWarningTest implements IDSTest{

    //The logger of the class
    private static final Logger logger = Logger.getLogger(CDKManager.class);

    //The model file
    private static final String MODEL_FILE="/data/bursiSignsXYZ_1.txt.model";

    //Path to the platform-specific signatures file
    private String signaturesPath;
    
    // Block of member variables. Can they reside here?
    // Most of the member variables below are hardcoded for the specific example
    //with 198 descriptors.

    // This is an array of the signatures used in the model. 
    //All model specific files resides in the directory ModelSpecificFiles.
    //TODO: read this info from file
    public static final String[] signatureList = {"[C]([C][Cl][Cl])","[P]([C][O][O][O])","[C]([O][O])","[S]([C][O][O][O])","[C]([C][F])","[N]([C][P])","[N]([C][O][O])","[N]([C][S][S])","[C]([C][C][C][C])","[C]([Cl][Cl][Cl][S])","[Cl]([C])","[C]([Br][Cl][Cl][Cl])","[P]([Cl][N][N][O])","[C]([Br][Br][Br][N])","[C]([C][C][Cl][F])","[N]([C][H][N])","[C]([C][C][P])","[C]([Br][C][Cl])","[P]([O][O][O][O])","[C]([Cl][N][N])","[P]([N][O][O][S])","[C]([I][I])","[P]([N][N][N][S])","[S]([C])","[C]([C][C][C][S])","[S]([C][Cl][O][O])","[N]([C][C][C][N])","[C]([C][I])","[P]([C][Cl][O][O])","[N]([Br][C][C])","[N]([C][N])","[N]([P][P])","[O]([C][C])","[O]([N][P])","[S]([C][N])","[S]([C][C][N][O])","[C]([C][F][F][F])","[N]([C][N][O])","[Br]([N])","[C]([C][C][C][Cl])","[C]([C][Cl][Cl][N])","[C]([Cl][Cl][Cl][N])","[N]([C][C][N])","[N]([C])","[N]([N][O][O])","[P]([N][N][N][N])","[P]([C][C][C])","[N]([C][S])","[C]([O][O][S])","[C]([C][Cl][F][F])","[O]([C][S])","[Cl]([N])","[C]([C][Cl])","[C]([H][N][N])","[C]([N][N][S])","[N]([C][C][C])","[C]([C][C][Cl])","[C]([O][P])","[H]([C])","[C]([C][C][O][O])","[C]([N][O][S])","[C]([C][O][P][P])","[C]([C][C][O])","[S]([C][S])","[C]([C][Cl][Cl][Cl])","[N]([C][C][Cl])","[O]([P])","[C]([C][C][N][O])","[N]([C][C])","[C]([Cl][N][O])","[N]([C][Cl])","[C]([N][P])","[C]([O])","[C]([Cl][Cl])","[C]([C][P])","[C]([C][O][O])","[C]([Br][Cl])","[C]([C][F][S])","[N]([O][O][O])","[C]([C][O][S])","[C]([Br][C][C])","[C]([N][N][N])","[N]([O])","[C]([S])","[S]([C][C])","[C]([Cl][Cl][N])","[N]([O][S])","[N]([C][C][S])","[C]([C][Cl][F])","[C]([N][N][N][N])","[O]([C][N])","[C]([C][C][F][F])","[S]([C][C][O][O])","[C]([N][N])","[C]([C][N][O][O])","[C]([C][C][F])","[C]([C][Cl][N])","[C]([C][N])","[S]([C][C][O])","[P]([N][N][N][O])","[N]([O][O])","[Cl]([P])","[C]([C][Cl][O])","[C]([C][C][Cl][Cl])","[P]([N][O][O][O])","[P]([C][F][O][O])","[N]([N])","[H]([N])","[C]([C][C][N][N])","[C]([N][N][O])","[N]([C][O])","[I]([C])","[S]([C][P])","[C]([N][S][S])","[C]([C][N][P])","[N]([P])","[C]([C][F][N])","[N]([C][C][O])","[O]([C])","[C]([C][F][F])","[C]([C][C][N])","[C]([C])","[N]([C][C][C][O])","[S]([N][N][O][O])","[C]([C][N][O])","[N]([C][C][H])","[C]([S][S][S])","[N]([C][C][C][C])","[C]([C][S])","[N]([S])","[P]([O][O][O][S])","[C]([Cl][Cl][F][S])","[C]([Br][C][C][C])","[F]([P])","[O]([C][P])","[P]([N][N][O][O])","[N]([N][O])","[C]([N][S])","[S]([O][O][O])","[C]([Cl][Cl][Cl][F])","[C]([C][Cl][S])","[N]([C][C][P])","[N]([N][N][O])","[F]([C])","[C]([C][F][F][O])","[P]([O][O][S][S])","[C]([C][C][I])","[C]([N][O])","[C]([Cl][S])","[C]([Cl][O][S])","[P]([O][O][O])","[C]([Br][Br])","[S]([C][N][O][O])","[C]([C][C][C][O])","[O]([O])","[S]([O][O][O][O])","[N]([N][N])","[C]([C][O])","[C]([O][O][O])","[C]([C][N][S])","[C]([P])","[O]([N])","[O]([S])","[P]([C][C][C][C])","[P]([C][N][O][O])","1","[S]([N][N])","[N]([N][S])","[C]([Br])","[C]([C][C][S])","[C]([C][C][C][F])","[N]([C][H][S])","[C]([C][C][C][N])","[C]([C][C][S][S])","[C]([C][S][S])","[O]([P][P])","[N]([C][N][N])","[C]([Br][Br][C])","[C]([N])","[C]([C][O][P])","[O]([C][O])","[S]([N][O][O][O])","[C]([C][C][Cl][N])","[Br]([C])","[C]([Cl][O][O])","[C]([C][N][N])","[C]([C][C][C])","[S]([C][C][C])","[C]([Br][C])","[C]([C][O][O][O])","[Cl]([S])","[S]([P])","[C]([Cl][N])","[C]([N][O][O])","[C]([C][C][N][S])","[C]([F][F][F][S])","[C]([S][S])","[C]([C][C])"};
    public static final int nrSignatures = 198;

    // These variables are defined by the range file.
    //We include them here to avoid parsing the range file.
    //TODO: read this info from file
    public static final double lower = -1.0;
    public static final double upper = 1.0;
    public static final double[] feature_min = {0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 };
    public static final double[] feature_max = {2.0 , 2.0 , 3.0 , 4.0 , 1.0 , 6.0 , 6.0 , 1.0 , 6.0 , 1.0 , 12.0 , 1.0 , 1.0 , 1.0 , 3.0 , 2.0 , 3.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 1.0 , 2.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 8.0 , 3.0 , 14.0 , 1.0 , 1.0 , 1.0 , 2.0 , 1.0 , 1.0 , 8.0 , 1.0 , 1.0 , 2.0 , 13.0 , 1.0 , 3.0 , 1.0 , 2.0 , 1.0 , 1.0 , 2.0 , 3.0 , 6.0 , 1.0 , 2.0 , 6.0 , 8.0 , 4.0 , 1.0 , 3.0 , 1.0 , 1.0 , 23.0 , 2.0 , 2.0 , 3.0 , 6.0 , 1.0 , 32.0 , 1.0 , 3.0 , 1.0 , 6.0 , 1.0 , 1.0 , 7.0 , 1.0 , 1.0 , 4.0 , 1.0 , 10.0 , 6.0 , 1.0 , 2.0 , 3.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 4.0 , 8.0 , 2.0 , 6.0 , 1.0 , 5.0 , 2.0 , 10.0 , 1.0 , 1.0 , 1.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 2.0 , 3.0 , 1.0 , 3.0 , 2.0 , 6.0 , 2.0 , 2.0 , 1.0 , 1.0 , 2.0 , 2.0 , 39.0 , 1.0 , 23.0 , 12.0 , 1.0 , 1.0 , 29.0 , 3.0 , 1.0 , 1.0 , 4.0 , 1.0 , 2.0 , 1.0 , 1.0 , 1.0 , 6.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 1.0 , 3.0 , 1.0 , 19.0 , 1.0 , 1.0 , 6.0 , 2.0 , 1.0 , 1.0 , 2.0 , 1.0 , 2.0 , 4.0 , 1.0 , 1.0 , 2.0 , 15.0 , 2.0 , 2.0 , 1.0 , 12.0 , 12.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 1.0 , 4.0 , 1.0 , 1.0 , 2.0 , 1.0 , 1.0 , 1.0 , 2.0 , 1.0 , 6.0 , 1.0 , 4.0 , 1.0 , 1.0 , 10.0 , 1.0 , 3.0 , 16.0 , 1.0 , 3.0 , 1.0 , 1.0 , 2.0 , 1.0 , 2.0 , 2.0 , 1.0 , 1.0 , 35.0 };

    //The SVM model. Read once on initialization.
    public static svm_model bursiModel;
    
    //Parameters for the native signatures executable
    public static String signaturesExecutableOptions = " --height 1 --atomtype XYZ --filename ";

    // The ones below are needed, but they depend on some hardcoded info.
    //We need to avoid this, reading 198.
    private svm_node[] xScaled;
    private double[] x;

    //Instance fields for a prediction
    private Map<Integer,Integer> attributeValues;
    private Map<String,ArrayList<Integer>> signatureAtoms;
    private ArrayList<Integer> significantAtoms;
    private double prediction;
    private String significantSignature = "";
    
    /**
     * Default constructor
     */
    public SignSIcRunner(){
        super();
    }


    /**
     * Initialize all paths and read model files into memory.
     * @throws DSException 
     */
    private void initialize(){

        if (getTestErrorMessage().length()>1){
            logger.error("Trying to initialize test: " + getName() + " while " +
                "error message exists");
            return;
        }

        //Get signatures path depending on OS
        try {
            signaturesPath=getNativeSignaturesPath();
            logger.debug( "Signatures path is: " + signaturesPath );
        } catch ( DSException e ) {
            setTestErrorMessage( "Initialization failed; Signatures native " +
            		"path not found: " + e.getMessage() );
           return;
        }

        //Get model path depending on OS
        String modelPath;
        try {
            modelPath = getModelPath();
            logger.debug( "Model file path is: " + modelPath );
        } catch ( DSException e ) {
            setTestErrorMessage( "Initialization failed; Model file " +
            		"path not found: " + e.getMessage() );
           return;
        }
        
        //So, load the model file into memory
        //===================================
        try {
            bursiModel = svm.svm_load_model(modelPath);
        } catch (IOException e) {
            setTestErrorMessage( "Could not read model file '" + modelPath 
                                  + "' due to: " + e.getMessage() );
            return;
        }
        
        //Verify that the signatures file is accessible
        
        
        // Add the reading of the range file and set up the related variables.
        //TODO
    }

    
    /**
     * Get the path of the model file in plugin directory
     * @return path to model file.
     * @throws DSException
     */
    private String getModelPath() throws DSException {

        String modelPath="";
        try {
            URL url = FileLocator.toFileURL(Platform.getBundle(getPluginID()).getEntry(MODEL_FILE));
            modelPath=url.getFile();
        } catch ( IOException e1 ) {
            throw new DSException("Could not read model file: " + MODEL_FILE 
                                  + ". Reason: " + e1.getMessage());
        }

        //File could not be read
        if ("".equals( modelPath )){
            throw new DSException("Could not read model file: " + MODEL_FILE);
        }
        
        return modelPath;
    }


    /**
     * @return path to native signatures binary
     * @throws DSException
     */
    private String getNativeSignaturesPath() throws DSException {

        String filepath="";
        if (System.getProperty("os.name").toLowerCase().startsWith( "windows" )){
            throw new DSException("SignSic is currently not supported on windows.");
        }
        else if (System.getProperty("os.name").toLowerCase().startsWith( "mac" )){
            filepath="/exec/signatures-macosx";
        }
        else if (System.getProperty("os.name").toLowerCase().startsWith( "linux" )){
            filepath="/exec/signatures-linux";
        }
        else{
            throw new DSException("SignSic is currently not supported on " +
                               "the platform:" + System.getProperty("os.name"));                                  
        }
        
        logger.debug( "Based on OS, chosed signatures version: " + filepath );

        //Get path to our plugin
        String signaturesPath="";
        try {
            URL preurl=Platform.getBundle(getPluginID()).getEntry(filepath);
            URL url = FileLocator.toFileURL(preurl);
            signaturesPath=url.getFile();
        } catch ( Exception e ) {
            throw new DSException("Could not read native file: " + filepath);                                  
        }

        //File could not be read
        if ("".equals( signaturesPath )){
            throw new DSException("SignaturesPath is null. File: " + filepath + 
                                  " could not be read.");
        }
        
        return signaturesPath;
    }


    private double partialDerivative(int component)
    {
        // Component numbering starts at 1.
        double pD, xScaledCompOld;
        xScaledCompOld = xScaled[component-1].value; // Store the old component so we can copy it back.

        // Forward difference high value point.
        xScaled[component-1].value = lower + (upper-lower) * 
        (1.0+x[component-1]-feature_min[component-1])/
        (feature_max[component-1]-feature_min[component-1]);

        // Retrieve the decision function value.
        double[] decValues = new double[1]; // We only have two classes so this should be one. Look in svm_predict_values for an explanation. 
        svm.svm_predict_values(bursiModel, xScaled, decValues);

        xScaled[component-1].value = xScaledCompOld;

        pD = decValues[0];

        return pD;
    }

    
    private void predict()
    {
        prediction = svm.svm_predict(bursiModel, xScaled);

        // Retrieve the decision function value.
        double lowPointDecisionFuncValue;
        double[] decValues = new double[1]; // We only have two classes so this should be one. Look in svm_predict_values for an explanation. 
        svm.svm_predict_values(bursiModel, xScaled, decValues);
        logger.debug("Predicted decision function value: " + decValues[0]);
        lowPointDecisionFuncValue = decValues[0];

        // For a positive decision function we are looking for the largest positive component of the gradient.
        // For a negative, we are looking for the largest negative component.
        boolean maximum;
        double highPointDecisionFuncValue;
        if (lowPointDecisionFuncValue > 0)
        {
            maximum = true;
        }
        else
        {
            maximum = false;
        }
 
        //NB. We aren't looking for saddle points which is wrong but probably very rare.
        //For example if the lowPointDecisionFuncValue is greater than the highPointDecisionPointValue for a maximum case.
        double extremeValue = 0;
        int significantSignatureNr = 1;
        for (int key : attributeValues.keySet()) {
//            logger.debug("Keys:" + key);
            highPointDecisionFuncValue = partialDerivative(key);
            if (maximum)
            {
                if (extremeValue < highPointDecisionFuncValue)
                {
                    extremeValue = highPointDecisionFuncValue;
                    significantSignatureNr = key;
                }
            }
            else
            {
                if (extremeValue > highPointDecisionFuncValue)
                {
                    extremeValue = highPointDecisionFuncValue;
                    significantSignatureNr = key;
                }
            }
//            logger.debug(highPointDecisionFuncValue);			
        }
        logger.debug("Extreme value: " + extremeValue);
        logger.debug("Keys: " + significantSignatureNr);
        significantSignature = signatureList[significantSignatureNr-1];
        // Make sure significantAtoms is empty.
        significantAtoms.clear();
        for (int i : signatureAtoms.get(signatureList[significantSignatureNr-1])){
        	significantAtoms.add(i-1);
        }
    }


    private void scale()
    {
        //Initialize xScaled. In this case the lower value is -1. This is defined in the range file.
        for (int i = 0; i < nrSignatures; i++){
            xScaled[i] = new svm_node();
            xScaled[i].index = i + 1;
            xScaled[i].value = lower + (upper-lower) * 
            (x[i]-feature_min[i])/
            (feature_max[i]-feature_min[i]);
       }
    }


    private void predictAndComputeSignificance() {
        logger.debug("Predicting and computing significance.");

        // The unscaled attributes. 
        for (int i = 0; i < nrSignatures; i++){
            int signatureNr = i + 1;
            if (attributeValues.containsKey(signatureNr) ){
                x[i] = attributeValues.get(signatureNr);
                logger.debug("Singature number: " + signatureNr + ", value: " + x[i]);
            }
            else{
                x[i] = 0.0;
            }
        }
        // Do a scaling.
        scale();
        
        // Predict
        predict();

    }

    private void createSignatures(String molfilePath){
        Process signatureRun;
        try {
            //System.out.println("createSignatures::LC added: Path to signatures: " + signaturesPath + signaturesExecutableOptions + molfilePath);
            signatureRun = Runtime.getRuntime().exec(signaturesPath + signaturesExecutableOptions + molfilePath);
            BufferedReader br = new BufferedReader (new InputStreamReader(signatureRun.getInputStream ()));
            String line;
            int lineNr = 0;
            while ( (line = br.readLine()) != null ){
                lineNr = lineNr + 1; // This number corresponds to the atom number,
                if ( lineNr > 1 ){
                    //System.out.println(line);
                    int signatureNr = 0;
                    Integer currentAttributeValue = 0;
                    for (String signature : signatureList) {
                        signatureNr = signatureNr + 1;
                        if (signature.equals(line)){
                            // We have a matching signature. Add 1 to the attribute and append the atomNr to the signature hashmap list.
                            if (attributeValues.containsKey(signatureNr)){
                                currentAttributeValue = (Integer) attributeValues.get(signatureNr);
                                attributeValues.put(signatureNr, new Integer(currentAttributeValue + 1));
                            }
                            else {
                                attributeValues.put(signatureNr, new Integer(1));
                            }
                            if (signatureAtoms.containsKey(signature)){ 
                            	signatureAtoms.get(signature).add(lineNr);
                            	//System.out.println("Significant atom:" + lineNr);
                            	//System.out.println(signature);
                            }
                            else {
                            	signatureAtoms.put(signature, new ArrayList<Integer>());
                            	signatureAtoms.get(signature).add(lineNr);
                            	//System.out.println("Significant atom:" + lineNr);
                            	//System.out.println(signature);
                            }
                        }
                    }
                }
            }
            logger.debug("Result from signature creation was: " + lineNr + " lines");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }
    /**
     * Method that takes an IMolecule as input and deliver a list of testresults
     * in the form of SubstructureMatches.
     */
    public List<ITestResult> runWarningTest( IMolecule molecule, IProgressMonitor monitor ){

        //Check for cancellation
        if (monitor.isCanceled())
            return returnError( "Cancelled","");

        if (getTestErrorMessage().length()>1){
            return returnError( "Test has error message and should not be run", "" );
        }

        //If bursimodel is null it is not loaded, so initialize
        if (bursiModel==null)
                initialize();

        if (getTestErrorMessage().length()>1){
            return returnError( "Test has error message and should not be run", "" );
        }

        //Get the CDKManager that can carry out cheminfo stuff
        ICDKManager cdk = Activator.getDefault().getJavaCDKManager();
        
        ICDKMolecule cdkmol=null;
        try {
            cdkmol = cdk.create( molecule );
        } catch ( BioclipseException e1 ) {
            return returnError("Could not convert input molecule to " +
            		"CDKMolecule" , e1.getMessage());
        }

        //Check for cancellation
        if (monitor.isCanceled())
            return returnError( "Cancelled","");

        //Remove all hydrogens in molecule
        //cdkmol=cdk.removeExplicitHydrogens( cdkmol );
        //cdkmol=cdk.removeImplicitHydrogens( cdkmol );

        //Write a temp molfile and return path
        File tempfile=null;
        try {
        	// Is this the way to do it? Can we produce a unique filename that can be removed after it has been used? Thread safe?
            tempfile = File.createTempFile("signsig", ".mol");

            //Write mol as MDL to the temp file
            String mdlString=cdk.getMDLMolfileString( cdkmol );
            FileWriter w = new FileWriter(tempfile);
            w.write( mdlString );
            w.close();

        } catch ( Exception e ) {
            LogUtils.debugTrace( logger, e );
            return returnError("Could not write MDL file" , e.getMessage());
        }

        //Just another check for null
        if (tempfile==null)
            return returnError("Path to temp molfile is empty", "");

        //Set this file as input file
        String molfilePath=tempfile.getAbsolutePath();
        if (molfilePath==null || molfilePath.length()<=0)
            return returnError("Path to temp molfile is empty", "");

        //Ready to invoke signatures and predict
        logger.debug("Wrote temp MDL file as: " + molfilePath);
        logger.debug("Path to signatures: " + signaturesPath);
        
        //=======================================
        // Here comes the actual predictions
        //=======================================
        
        //Make room for new predicions
        attributeValues=new HashMap<Integer, Integer>();
        signatureAtoms=new HashMap<String, ArrayList<Integer>>();
        significantAtoms=new ArrayList<Integer>();
        xScaled = new svm_node[nrSignatures];
        x=new double[nrSignatures];

        //Check for cancellation
        if (monitor.isCanceled())
            return returnError( "Cancelled","");

        //Create signatures for this molfile
        createSignatures(molfilePath);
        
        //Ensure we have what we need
        if (signatureAtoms.size()<=0){
            return returnError("No signature atoms produced by signaturesrunner", "");
        }

        //Check for cancellation
        if (monitor.isCanceled())
            return returnError( "Cancelled","");

        //Predict using the signatureatoms and attributevalues
        predictAndComputeSignificance();
        
        //Remove the temp file
        tempfile.delete();

        //Create a new match with correct coloring
        SignSicHit match=new SignSicHit();
        if (prediction>0)
            match.setPositive( true );
        else
            match.setPositive( false );
            
        IAtomContainer significantAtomsContainer=cdkmol.getAtomContainer().getBuilder().newAtomContainer();
        for (int significantAtom : significantAtoms){
        	significantAtomsContainer.addAtom( cdkmol.getAtomContainer().getAtom( significantAtom-1 ));
        	logger.debug("center atom: " + significantAtom);
        	//Also add all atoms connected to significant atoms to list
        	for (IAtom nbr : cdkmol.getAtomContainer().getConnectedAtomsList(cdkmol.getAtomContainer().getAtom( significantAtom-1 )) ){
        		int nbrAtomNr = cdkmol.getAtomContainer().getAtomNumber(nbr) + 1;
        		significantAtomsContainer.addAtom(cdkmol.getAtomContainer().getAtom(nbrAtomNr-1));
        		logger.debug("nbr: " + nbrAtomNr);
        	}
        }
        logger.debug("Number of center atoms: " + significantAtoms.size());
        
        //We want to set the color of the hilighting depending on the prediction. If the decision function > 0.0 the color should be red, otherwise it should be green.
        //we also want the filled circles to be larger so that they become visible for non carbons.
        match.setAtomContainer( significantAtomsContainer );
        match.setName( significantSignature ); //Will appear in GUI

        //We can have multiple hits...
        List<ITestResult> results=new ArrayList<ITestResult>();
        //...but here we only have one
        results.add( match );

        return results;
    }


    @Override
    public String toString() {
        return getName();
    }


}
