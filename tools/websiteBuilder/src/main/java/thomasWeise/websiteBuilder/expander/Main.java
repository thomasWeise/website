package thomasWeise.websiteBuilder.expander;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.config.Configuration;

/** The main class. */
public class Main {

  /** the source argument */
  public static final String PARAM_SOURCE = "source";//$NON-NLS-1$
  /** the resources argument */
  public static final String PARAM_RESOURCES = "resources";//$NON-NLS-1$
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
    final Path source, resources, dest;

    source = config.getPath(Main.PARAM_SOURCE, null);
    if (source == null) {
      if (logger != null) {
        logger.severe("Must specify source folder with argument '" + //$NON-NLS-1$
            Main.PARAM_SOURCE + "=...'."); //$NON-NLS-1$
      }
      return;
    }

    resources = config.getPath(Main.PARAM_RESOURCES, null);
    if (resources == null) {
      if (logger != null) {
        logger.severe("Must specify resource folder with argument '" + //$NON-NLS-1$
            Main.PARAM_RESOURCES + "=...'."); //$NON-NLS-1$
      }
      return;
    }

    dest = config.getPath(Main.PARAM_DEST, null);
    if (dest == null) {
      if (logger != null) {
        logger.severe("Must specify destination folder with argument '" + //$NON-NLS-1$
            Main.PARAM_SOURCE + "=...'."); //$NON-NLS-1$
      }
      return;
    }

    Main.__build(source, resources, dest, logger);
  }

  /**
   * Build the website
   *
   * @param source
   *          the source folder
   * @param resources
   *          the resources path
   * @param dest
   *          the destination folder
   * @param logger
   *          the logger
   * @throws IOException
   *           if i/o fails
   */
  private static final void __build(final Path source,
      final Path resources, final Path dest, final Logger logger)
          throws IOException {
    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Building website from source '" + source + //$NON-NLS-1$
          "' to dest '" + dest + '\'' + '.');//$NON-NLS-1$
    }

    Files.walkFileTree(source,
        new _FileVisitor(new _Context(logger, source, resources, dest)));

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Finished building website.");//$NON-NLS-1$
    }
  }
}
