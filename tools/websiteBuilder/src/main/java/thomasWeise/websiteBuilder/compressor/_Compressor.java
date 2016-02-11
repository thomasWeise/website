package thomasWeise.websiteBuilder.compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/** The compressor class. */
final class _Compressor {

  /**
   * Compress a given array of byte with GZIP and return the smallest-size
   * result.
   *
   * @param source
   *          the source data
   * @return the compressed result
   * @throws IOException
   *           if something goes wrong
   */
  static final byte[] _compress(final byte[] source) throws IOException {
    int level;
    byte[] result, tmp;

    result = null;
    try (final ByteArrayOutputStream bos = new ByteArrayOutputStream(
        source.length)) {

      for (level = 0; level <= 9; level++) {

        try (final __JavaGZIPOutputStream gos = new __JavaGZIPOutputStream(
            bos, source.length, level)) {
          gos.write(source);
        }
        if ((result == null) || (bos.size() < result.length)) {
          result = bos.toByteArray();
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
        bos.reset();
      }

      tmp = _Compressor.__gzip(source, bos);
      if ((tmp != null)
          && ((result == null) || (tmp.length < result.length))) {
        result = tmp;
      }
    }

    return result;
  }

  /**
   * Try to use GZIP command line tool, if it is installed. This will only
   * work on Linux-like systems, otherwise return {@code null}.
   *
   * @param in
   *          the input data
   * @param buffer
   *          the internal buffer to use
   * @return the output data or {@code null} if compression failed
   */
  private static final byte[] __gzip(final byte[] in,
      final ByteArrayOutputStream buffer) {
    final Runtime runtime;
    final byte[] rb;
    Process proc;
    byte[] result, temp;
    int level, read;

    runtime = Runtime.getRuntime();
    result = null;
    rb = new byte[16384];

    for (level = 1; level <= 9; level++) {
      try {
        proc = runtime.exec(new String[] { "gzip", //$NON-NLS-1$
            "-" + level, //$NON-NLS-1$
            "-c", //$NON-NLS-1$
            "-q", //$NON-NLS-1$
            "-f", });//$NON-NLS-1$
        try {

          try (final OutputStream os = proc.getOutputStream()) {
            os.write(in);
            os.flush();
          }

          buffer.reset();
          try (final InputStream is = proc.getInputStream()) {
            while ((read = is.read(rb)) > 0) {
              buffer.write(rb, 0, read);
            }
          }

          temp = buffer.toByteArray();
          buffer.reset();
          if ((result == null) || (temp.length < result.length)) {
            result = temp;
          }
        } finally {
          try {
            proc.waitFor(5, TimeUnit.MINUTES);
          } finally {
            proc.destroy();
          }
        }
      } catch (@SuppressWarnings("unused") final Throwable ioe) { // ignore!
        return null;
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
