package thomasWeise.websiteBuilder;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** The visitor for all of our files. */
public class FileVisitor extends SimpleFileVisitor<Path> {

  /** the logger */
  private final Logger m_logger;

  /** the source path */
  private final Path m_source;

  /** the destination path */
  private final Path m_dest;

  /**
   * create
   * 
   * @param source
   *          the source path
   * @param dest
   *          the dest path
   * @param logger
   *          the logger
   */
  public FileVisitor(final Path source, final Path dest,
      final Logger logger) {
    super();
    this.m_dest = dest;
    this.m_source = source;
    this.m_logger = logger;
  }

  /** {@inheritDoc} */
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
      throws IOException {
    final FileVisitResult res;
    final Path dest;

    if ((this.m_logger != null)
        && (this.m_logger.isLoggable(Level.INFO))) {
      this.m_logger.info("Now processing '" + file + '\'' + '.'); //$NON-NLS-1$
    }

    res = super.visitFile(file, attrs);
    if (res == FileVisitResult.CONTINUE) {

      dest = PathUtils
          .normalize(this.m_dest.resolve(this.m_source.relativize(file)));
      if ((dest != null) && (dest.startsWith(this.m_dest))) {
        if (attrs.size() > 0L) {
          Files.createDirectories(dest.getParent());

          FileProcessor.processFile(file, dest, attrs, this.m_logger);

          if ((this.m_logger != null)
              && (this.m_logger.isLoggable(Level.INFO))) {
            this.m_logger.info("Successfully processed '" + file + //$NON-NLS-1$
                "' to '" + dest + '\'' + '.'); //$NON-NLS-1$
          }
        } else {
          if ((this.m_logger != null)
              && (this.m_logger.isLoggable(Level.WARNING))) {
            this.m_logger.warning("File '" + file + //$NON-NLS-1$
                "' has size 0."); //$NON-NLS-1$
          }
        }
      } else {
        throw new IOException("Path '" + dest + //$NON-NLS-1$
            "' is outside of '" + this.m_dest + '\'' + '.');//$NON-NLS-1$
      }

      return FileVisitResult.CONTINUE;
    }

    return res;
  }
}
