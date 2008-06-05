package org.seasar.eclipse.common;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.seasar.eclipse.common.util.LogUtil;

/**
 * The main plugin class to be used in the desktop.
 */
public class CommonPlugin extends Plugin {

    // The shared instance.
    private static CommonPlugin plugin;

    /**
     * The constructor.
     */
    public CommonPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        throw new UnsupportedOperationException("This plugin cannot started.");
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static CommonPlugin getDefault() {
        return plugin;
    }

    public static void log(String msg) {
        LogUtil.log(getDefault(), msg);
    }

    public static void log(Throwable throwable) {
        LogUtil.log(getDefault(), throwable);
    }

}
