package thomasWeise.websiteBuilder;

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
    final Path source, dest;

    Configuration.setup(args);
    config = Configuration.getRoot();
    logger = Configuration.getGlobalLogger();

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Welcome to the website builder."); //$NON-NLS-1$
    }

    source = config.getPath(Main.PARAM_SOURCE, null);
    if (source == null) {
      if (logger != null) {
        logger.severe("Must specify source folder with argument '" + //$NON-NLS-1$
            Main.PARAM_SOURCE + "=...'."); //$NON-NLS-1$
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

    Main.build(source, dest, logger);
  }

  /**
   * Build the website
   *
   * @param source
   *          the source folder
   * @param dest
   *          the destination folder
   * @param logger
   *          the logger
   * @throws IOException
   *           if i/o fails
   */
  public static final void build(final Path source, final Path dest,
      final Logger logger) throws IOException {
    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Building website from source '" + source + //$NON-NLS-1$
          "' to dest '" + dest + '\'' + '.');//$NON-NLS-1$
    }

    Files.walkFileTree(source, new FileVisitor(source, dest, logger));

    if ((logger != null) && (logger.isLoggable(Level.INFO))) {
      logger.info("Finished building website.");//$NON-NLS-1$
    }
  }
}
