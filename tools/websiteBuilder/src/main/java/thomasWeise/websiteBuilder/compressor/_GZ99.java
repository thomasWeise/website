package thomasWeise.websiteBuilder.compressor;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.io.paths.TempDir;
import org.optimizationBenchmarking.utils.tools.impl.process.EProcessStream;
import org.optimizationBenchmarking.utils.tools.impl.process.ExternalProcess;
import org.optimizationBenchmarking.utils.tools.impl.process.ExternalProcessExecutor;

/** The compressor class using the GZ99 script. */
final class _GZ99 {

  /** the path */
  private static final String GZ99 = "gz99.sh";//$NON-NLS-1$

  /**
   * Compress a given array of byte with GZIP and return the smallest-size
   * result.
   *
   * @param source
   *          the source data
   * @param logger
   *          the logger
   * @return the compressed result
   */
  static final byte[] _compress(final byte[] source, final Logger logger) {
    final Path temp, script, dest;
    final byte[] res;

    if (_GZIP._GZIP_PATH == null) {
      return null;
    }

    try (final TempDir tempDir = new TempDir()) {
      temp = tempDir.getPath();
      script = PathUtils.createPathInside(temp, _GZ99.GZ99);
      try (final InputStream is = _GZ99.class
          .getResourceAsStream(_GZ99.GZ99)) {
        Files.copy(is, script);
      }
      Files.setPosixFilePermissions(script,
          EnumSet.allOf(PosixFilePermission.class));
      dest = Files.createTempFile(temp, "comp", "res"); //$NON-NLS-1$ //$NON-NLS-2$
      PathUtils.delete(dest);

      try (final ExternalProcess ep = ExternalProcessExecutor.getInstance()
          .use()//
          .setDirectory(temp)//
          .setExecutable(script)//
          .addPathArgument(dest)//
          .setLogger(logger)//
          .setStdErr(EProcessStream.REDIRECT_TO_LOGGER)//
          .setStdOut(EProcessStream.REDIRECT_TO_LOGGER)//
          .setMergeStdOutAndStdErr(true)//
          .setStdIn(EProcessStream.AS_STREAM)////
          .create()) {
        try (final OutputStream os = ep.getStdIn()) {
          os.write(source);
        }
        if (ep.waitFor() != 0) {
          return null;
        }
      }
      res = Files.readAllBytes(dest);
      if ((res != null) && (res.length > 0)) {
        return res;
      }
      return null;
    } catch (final Throwable ioe) { // ignore!
      if ((logger != null) && (logger.isLoggable(Level.SEVERE))) {
        logger.log(Level.SEVERE, "Error when running GZ99.", ioe);//$NON-NLS-1$
      }
      return null;
    }
  }
}
