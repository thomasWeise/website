package thomasWeise.websiteBuilder.compressor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

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
        try (final MyGZIPOutputStream gos = new MyGZIPOutputStream(bos,
            source.length, level)) {
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
  private static final class MyGZIPOutputStream extends GZIPOutputStream {

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
    MyGZIPOutputStream(final ByteArrayOutputStream _out, final int size,
        final int level) throws IOException {
      super(_out, size, false);
      this.def.setLevel(level);
    }
  }
}
