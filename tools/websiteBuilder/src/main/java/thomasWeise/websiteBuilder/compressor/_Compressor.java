package thomasWeise.websiteBuilder.compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    byte[] result;

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
