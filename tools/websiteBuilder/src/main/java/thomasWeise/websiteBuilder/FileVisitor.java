package thomasWeise.websiteBuilder;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;

/** The visitor for all of our files. */
public class FileVisitor extends SimpleFileVisitor<Path> {

  /** the context */
  private final Context m_context;

  /**
   * create
   *
   * @param context
   *          the context
   */
  public FileVisitor(final Context context) {
    super();
    this.m_context = context;
  }

  /** {@inheritDoc} */
  @Override
  public FileVisitResult visitFile(final Path file,
      final BasicFileAttributes attrs) throws IOException {
    final FileVisitResult res;
    final Path dest;
    EFragmentType type;

    if ((this.m_context.logger != null)
        && (this.m_context.logger.isLoggable(Level.INFO))) {
      this.m_context.logger.info("Now processing '" + file + '\'' + '.'); //$NON-NLS-1$
    }

    res = super.visitFile(file, attrs);
    if (res == FileVisitResult.CONTINUE) {

      if (attrs.size() > 0L) {
        dest = this.m_context.sourcePathToDestPath(file);

        type = EFragmentType.getType(file);
        if (type == null) {
          throw new IllegalArgumentException(//
              "Path '" + file + //$NON-NLS-1$
                  "' has no corresponding fragment type.");//$NON-NLS-1$
        }
        if (type.shouldProcess) {
          Files.createDirectories(dest.getParent());
          type.process(this.m_context.load(file, attrs), dest);
        }

      } else {
        if ((this.m_context.logger != null)
            && (this.m_context.logger.isLoggable(Level.WARNING))) {
          this.m_context.logger.warning("File '" + file + //$NON-NLS-1$
              "' has size 0."); //$NON-NLS-1$
        }
      }

      return FileVisitResult.CONTINUE;
    }

    return res;
  }
}
