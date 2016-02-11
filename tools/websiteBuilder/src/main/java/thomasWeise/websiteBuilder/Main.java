package thomasWeise.websiteBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.config.Configuration;

/** The main class. */
public class Main {

  /** the source argument */
  public static final String PARAM_SOURCE = "source";//$NON-NLS-1$
  /** the destination argument */
  public static final String PARAM_DEST = "dest";//$NON-NLS-1$

  /**
   * The main entry point of the website builder
   *
   * @param args
   *          the command line arguments: 1) input folder, 2) output folder
   * @throws IOException
   *           if i/o fails
   */
  public static void main(final String[] args) throws IOException {
    final Logger logger;
    final Configuration config;

    Configuration.setup(args);
    config = Configuration.getRoot();
    logger = Configuration.getGlobalLogger();

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info(
          "Welcome to the website builder expander tool.\nThis tool resolves links and includes, encodes files, and handles some additional tags.."); //$NON-NLS-1$
    }

    if (config.getBoolean("expand", false)) {//$NON-NLS-1$
      thomasWeise.websiteBuilder.expander.Main.run(config, logger);
      return;
    }
    if (config.getBoolean("compress", false)) {//$NON-NLS-1$
      thomasWeise.websiteBuilder.compressor.Main.run(config, logger);
      return;
    }

    if ((logger != null) && (logger.isLoggable(Level.SEVERE))) {
      logger.severe("You did not specify what to do, I quit."); //$NON-NLS-1$
    }
  }
}
