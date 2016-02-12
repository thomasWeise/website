package thomasWeise.websiteBuilder.compressor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.io.paths.predicates.CanExecutePredicate;
import org.optimizationBenchmarking.utils.io.paths.predicates.FileNamePredicate;
import org.optimizationBenchmarking.utils.io.paths.predicates.IsFilePredicate;
import org.optimizationBenchmarking.utils.predicates.AndPredicate;
import org.optimizationBenchmarking.utils.tools.impl.process.EProcessStream;
import org.optimizationBenchmarking.utils.tools.impl.process.ExternalProcess;
import org.optimizationBenchmarking.utils.tools.impl.process.ExternalProcessExecutor;

/** The compressor class. */
final class _GZIP {

  /** the GZIP executable */
  static final Path _GZIP_PATH = PathUtils.findFirstInPath(
      new AndPredicate<>(new FileNamePredicate(true, "gzip"), //$NON-NLS-1$
          CanExecutePredicate.INSTANCE), //
      IsFilePredicate.INSTANCE, null);

  /**
   * Try to use GZIP command line tool, if it is installed. This will only
   * work on Linux-like systems, otherwise return {@code null}.
   *
   * @param in
   *          the input data
   * @param buffer
   *          the internal buffer to use
   * @param logger
   *          the logger
   * @return the output data or {@code null} if compression failed
   */
  static final byte[] _gzip(final byte[] in,
      final ByteArrayOutputStream buffer, final Logger logger) {
    final byte[] rb;
    byte[] result, temp;
    int level, read;

    if (_GZIP._GZIP_PATH == null) {
      return null;
    }

    result = null;
    rb = new byte[16384];

    for (level = 1; level <= 9; level++) {
      try {
        try (final ExternalProcess ep = ExternalProcessExecutor
            .getInstance().use()//
            .setDirectory(PathUtils.getTempDir())//
            .setExecutable(_GZIP._GZIP_PATH)//
            .addStringArgument("-" + level) //$NON-NLS-1$
            .addStringArgument("-c") //$NON-NLS-1$
            .setLogger(logger)//
            .setStdErr(EProcessStream.REDIRECT_TO_LOGGER)//
            .setStdIn(EProcessStream.AS_STREAM)//
            .setStdOut(EProcessStream.AS_STREAM)//
            .create()) {

          try (final OutputStream os = ep.getStdIn()) {
            os.write(in);
          }

          buffer.reset();
          try (final InputStream is = ep.getStdOut()) {
            while ((read = is.read(rb)) >= 0) {
              buffer.write(rb, 0, read);
            }
          }

          if (ep.waitFor() != 0) {
            return null;
          }

          temp = buffer.toByteArray();
          buffer.reset();
          if ((result == null) || (temp.length < result.length)) {
            result = temp;
          }
        }
      } catch (final Throwable ioe) { // ignore!
        if ((logger != null) && (logger.isLoggable(Level.SEVERE))) {
          logger.log(Level.SEVERE, "Error when running gzip.", ioe);//$NON-NLS-1$
        }
        return null;
      }
    }

    return result;
  }
}
