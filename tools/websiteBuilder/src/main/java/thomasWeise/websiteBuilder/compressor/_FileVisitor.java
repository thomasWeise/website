package thomasWeise.websiteBuilder.compressor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** The visitor for all of our files. */
class _FileVisitor extends SimpleFileVisitor<Path> {

  /** the logger */
  private final Logger m_logger;

  /**
   * create
   *
   * @param logger
   *          the logger
   */
  _FileVisitor(final Logger logger) {
    super();
    this.m_logger = logger;
  }

  /** {@inheritDoc} */
  @Override
  public FileVisitResult visitFile(final Path file,
      final BasicFileAttributes attrs) throws IOException {
    final FileVisitResult res;
    final Path dest;
    final byte[] plain, compressed;
    final int origSize, improvement;

    res = super.visitFile(file, attrs);
    if (res == FileVisitResult.CONTINUE) {

      if (attrs.size() > 0L) {

        switch (TextUtils.toLowerCase(PathUtils.getFileExtension(file))) {
          case "css"://$NON-NLS-1$
          case "gif"://$NON-NLS-1$
          case "html"://$NON-NLS-1$
          case "jpg"://$NON-NLS-1$
          case "js"://$NON-NLS-1$
          case "pdf"://$NON-NLS-1$
          case "png"://$NON-NLS-1$
          case "txt": {//$NON-NLS-1$

            if ((this.m_logger != null)
                && (this.m_logger.isLoggable(Level.INFO))) {
              this.m_logger.info("Now processing '" + file + '\'' + '.'); //$NON-NLS-1$
            }

            dest = PathUtils.normalize(
                file.getParent().resolve(file.getFileName() + ".gz"));//$NON-NLS-1$
            if (Files.exists(dest)) {
              throw new IOException("Destination '" + dest //$NON-NLS-1$
                  + "' of source resource '" + file //$NON-NLS-1$
                  + "' already exists."); //$NON-NLS-1$
            }

            plain = Files.readAllBytes(file);
            origSize = plain.length;
            if (origSize > 50) {
              compressed = _Compressor._compress(plain, this.m_logger,
                  '\'' + file.toString() + '\'');
              if (compressed != null) {
                improvement = (origSize - compressed.length);
                if ((improvement > 50) && //
                    ((improvement > (origSize / 20))
                        || (improvement > 4096))) {
                  Files.write(dest, compressed,
                      StandardOpenOption.CREATE_NEW);
                }
              }
            }

            if ((this.m_logger != null)
                && (this.m_logger.isLoggable(Level.INFO))) {
              this.m_logger.info("Finished processing '"//$NON-NLS-1$
                  + file + '\'' + '.');
            }
            break;
          }

          default: {
            break; // do nothing
          }
        }

      }

      return FileVisitResult.CONTINUE;
    }

    return res;
  }
}
