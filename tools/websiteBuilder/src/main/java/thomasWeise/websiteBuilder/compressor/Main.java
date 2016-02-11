package thomasWeise.websiteBuilder.compressor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.config.Configuration;

/** The main class. */
public class Main {

  /** the directory argument */
  public static final String PARAM_DIR = "dir";//$NON-NLS-1$

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
    Main.run(config, logger);
  }

  /**
   * The main entry point of the website builder
   *
   * @param config
   *          the configuration
   * @param logger
   *          the logger
   * @throws IOException
   *           if something goes wrong
   */
  public static void run(final Configuration config, final Logger logger)
      throws IOException {
    final Path dir;

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info(
          "Welcome to the website builder compressor tool.\nThis tool tries to create static compressed versions of text resources."); //$NON-NLS-1$
    }

    dir = config.getPath(Main.PARAM_DIR, null);
    if (dir == null) {
      if (logger != null) {
        logger.severe("Must specify a folder with argument '" + //$NON-NLS-1$
            Main.PARAM_DIR + "=...'."); //$NON-NLS-1$
      }
      return;
    }
    Main.__build(dir, logger);
  }

  /**
   * Build the website
   *
   * @param dir
   *          the folder to process
   * @param logger
   *          the logger
   * @throws IOException
   *           if i/o fails
   */
  private static final void __build(final Path dir, final Logger logger)
      throws IOException {
    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info(
          "Compressing static text resources in '" + dir + '\'' + '.');//$NON-NLS-1$
    }

    Files.walkFileTree(dir, new _FileVisitor(logger));

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Finished compressing static website content.");//$NON-NLS-1$
    }
  }
}
