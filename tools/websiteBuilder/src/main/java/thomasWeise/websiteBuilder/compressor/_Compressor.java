package thomasWeise.websiteBuilder.compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** The compressor class. */
final class _Compressor {

  /**
   * Compress a given array of byte with GZIP and return the smallest-size
   * result.
   *
   * @param source
   *          the source data
   * @param logger
   *          the logger to use
   * @param path
   *          the path
   * @return the compressed result
   * @throws IOException
   *           if something goes wrong
   */
  static final byte[] _compress(final byte[] source, final Logger logger,
      final String path) throws IOException {
    int level;
    byte[] result, tmp;

    result = null;
    try (final ByteArrayOutputStream bos = new ByteArrayOutputStream(
        source.length)) {

      for (level = 0; level <= 9; level++) {

        if ((logger != null) && (logger.isLoggable(Level.INFO))) {
          logger.info("Now compressing " + path + //$NON-NLS-1$
              " with Java's own GZIP implementation at level " + level); //$NON-NLS-1$
        }
        try (final __JavaGZIPOutputStream gos = new __JavaGZIPOutputStream(
            bos, source.length, level)) {
          gos.write(source);
        }
        if ((result == null) || (bos.size() < result.length)) {
          result = bos.toByteArray();
        }
        if ((logger != null) && (logger.isLoggable(Level.INFO))) {
          logger.info("Finished compressing " + path + //$NON-NLS-1$
              " with Java's own GZIP implementation at level " + level + //$NON-NLS-1$
              ", resulting size " + bos.size() + //$NON-NLS-1$
              "; now using JZLib.");//$NON-NLS-1$
        }
        bos.reset();

        try (
            final __JZLibGZIPOutputStream gos = new __JZLibGZIPOutputStream(
                bos, source.length, level)) {
          gos.write(source);
        }
        if ((result == null) || (bos.size() < result.length)) {
          result = bos.toByteArray();
        }
        if ((logger != null) && (logger.isLoggable(Level.INFO))) {
          logger.info("Finishedcompressing " + path + //$NON-NLS-1$
              " with JZLib's GZIP implementation at level " + level + //$NON-NLS-1$
              ", resulting size " + bos.size());//$NON-NLS-1$
        }
        bos.reset();
      }

      if (_GZIP._GZIP_PATH != null) {

        if ((logger != null) && (logger.isLoggable(Level.INFO))) {
          logger.info("Now attempting compressing " + path + //$NON-NLS-1$
              " with the gzip implementation of the OS.");//$NON-NLS-1$
        }
        tmp = _GZIP._gzip(source, bos, logger);
        if (tmp != null) {
          if (((result == null) || (tmp.length < result.length))) {
            result = tmp;
          }
          if ((logger != null) && (logger.isLoggable(Level.INFO))) {
            logger.info("Finished compressing " + path + //$NON-NLS-1$
                " with the gzip implementation of the OS, resulting size " //$NON-NLS-1$
                + tmp.length);
          }
        } else {
          if ((logger != null) && (logger.isLoggable(Level.INFO))) {
            logger.info("Failed compressing " + path + //$NON-NLS-1$
                " with the gzip implementation of the OS.");//$NON-NLS-1$
          }
        }

        if ((logger != null) && (logger.isLoggable(Level.INFO))) {
          logger.info("Now attempting compressing " + path + //$NON-NLS-1$
              " with the GZ99 script by gmatht.");//$NON-NLS-1$
        }
        tmp = _GZ99._compress(source, logger);
        if (tmp != null) {
          if ((result == null) || (tmp.length < result.length)) {
            result = tmp;
          }
          if ((logger != null) && (logger.isLoggable(Level.INFO))) {
            logger.info("Finished compressing " + path + //$NON-NLS-1$
                " with the GZ99 script by gmatht, resulting size " //$NON-NLS-1$
                + tmp.length);
          }
        } else {
          if ((logger != null) && (logger.isLoggable(Level.INFO))) {
            logger.info("Failed compressing " + path + //$NON-NLS-1$
                " with tthe GZ99 script by gmatht.");//$NON-NLS-1$
          }
        }
      }
    }

    return result;
  }

  /** the internal gzip class */
  private static final class __JavaGZIPOutputStream
      extends java.util.zip.GZIPOutputStream {

    /**
     * Create the stream
     *
     * @param _out
     *          the output stream to write to
     * @param size
     *          the size
     * @param level
     *          the level to use
     * @throws IOException
     *           if something goes wrong
     */
    __JavaGZIPOutputStream(final ByteArrayOutputStream _out,
        final int size, final int level) throws IOException {
      super(_out, size, false);
      this.def.setLevel(level);
    }
  }

  /** the internal gzip class */
  private static final class __JZLibGZIPOutputStream
      extends com.jcraft.jzlib.GZIPOutputStream {

    /**
     * Create the stream
     *
     * @param _out
     *          the output stream to write to
     * @param size
     *          the size
     * @param level
     *          the level to use
     * @throws IOException
     *           if something goes wrong
     */
    __JZLibGZIPOutputStream(final ByteArrayOutputStream _out,
        final int size, final int level) throws IOException {
      super(_out, new com.jcraft.jzlib.Deflater(level, 15 + 16), size,
          false);
      this.mydeflater = true;
    }
  }
}
