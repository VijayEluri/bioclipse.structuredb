package net.bioclipse.core;

import net.bioclipse.core.util.LogUtils;
import net.bioclipse.recording.IHistory;
import net.bioclipse.recording.IRecordingAdvice;

import org.apache.log4j.Logger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "net.bioclipse.core";

    private static final long SERVICE_TIMEOUT_MILLIS = 10*1000;
    
    // The shared instance
    private static Activator plugin;
    
    private static final Logger logger = Logger.getLogger(Activator.class);
    
    private ServiceTracker historyTracker;
    private ServiceTracker recordingAdviceTracker;

    
    public Activator() {
    }

    
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        historyTracker = new ServiceTracker( context, 
                IHistory.class.getName(), 
                null );
        historyTracker.open();
        recordingAdviceTracker = new ServiceTracker( context, 
                IRecordingAdvice.class.getName(), 
                null );
        recordingAdviceTracker.open();
    }

    
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    
    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    
    public IHistory getHistory() {
        IHistory history = null;
        try {
            history = (IHistory) historyTracker.waitForService(SERVICE_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Error getting History service: " + e);
            LogUtils.debugTrace(logger, e);
        }
        if(history == null) {
            throw new IllegalStateException("Could not get the history");
        }
        return history;
    }
    
    
    public IRecordingAdvice getRecordingAdvice() {
        IRecordingAdvice recordingAdvice = null;
        try {
            recordingAdvice = (IRecordingAdvice) 
                recordingAdviceTracker.waitForService(SERVICE_TIMEOUT_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Error getting RecordingAdvice service: " + e);
            LogUtils.debugTrace(logger, e);
        }
        if(recordingAdvice == null) {
            throw new IllegalStateException("Could not get the recordingAdvice");
        }
        return recordingAdvice;
    }
}
