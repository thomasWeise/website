package thomasWeise.websiteBuilder.compressor;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.parallel.Execute;

import thomasWeise.ultraGzip.UltraGzip;

/** The compressor class. */
final class _Compressor {

  /**
   * Compress a given array of byte with GZIP and return the smallest-size
   * result. This implementation now just delegates to my Ultra GZIP
   * tool.hgv7opl13
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
    Future<byte[]> future;
    Throwable cause;

    future = Execute.parallel(//
        UltraGzip.getInstance().use()//
            .setData(source)//
            .setLogger(logger)//
            .setName(path).create());
    try {
      return future.get();
    } catch (final Exception ex) {
      cause = ex.getCause();
      if (cause instanceof IOException) {
        throw ((IOException) cause);
      }
      throw new IOException(ex);
    }
  }
}
