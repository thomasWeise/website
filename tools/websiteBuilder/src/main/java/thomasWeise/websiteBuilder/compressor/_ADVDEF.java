package thomasWeise.websiteBuilder.compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.optimizationBenchmarking.utils.io.IOUtils;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.io.paths.TempDir;
import org.optimizationBenchmarking.utils.io.paths.predicates.CanExecutePredicate;
import org.optimizationBenchmarking.utils.io.paths.predicates.FileNamePredicate;
import org.optimizationBenchmarking.utils.io.paths.predicates.IsFilePredicate;
import org.optimizationBenchmarking.utils.predicates.AndPredicate;
import org.optimizationBenchmarking.utils.tools.impl.process.EProcessStream;
import org.optimizationBenchmarking.utils.tools.impl.process.ExternalProcess;
import org.optimizationBenchmarking.utils.tools.impl.process.ExternalProcessExecutor;

/** The compressor class used Advanced Computing. */
final class _ADVDEF {

  /** the advdef executable */
  static final Path _ADVDEF_PATH = PathUtils.findFirstInPath(
      new AndPredicate<>(new FileNamePredicate(true, "advdef"), //$NON-NLS-1$
          CanExecutePredicate.INSTANCE), //
      IsFilePredicate.INSTANCE, null);

  /**
   * Try to re-compress the the specified data . This will only work on
   * Linux-like systems, otherwise return {@code null}.
   *
   * @param compressed
   *          the compressed input data
   * @param uncompressed
   *          the uncompressed data
   * @param bos
   *          the buffer
   * @param logger
   *          the logger
   * @return the output data or {@code null} if compression failed
   */
  static final byte[] _advdef(final byte[] compressed,
      final byte[] uncompressed, final ByteArrayOutputStream bos,
      final Logger logger) {
    final Path tempFile;
    final byte[] result;
    byte[] buffer;

    if (_ADVDEF._ADVDEF_PATH == null) {
      return null;
    }

    try {
      try (final TempDir dir = new TempDir()) {

        tempFile = Files.createTempFile(dir.getPath(), "advdef", //$NON-NLS-1$
            ".gz"); //$NON-NLS-1$
        try (
            final OutputStream os = PathUtils.openOutputStream(tempFile)) {
          os.write(compressed);
        }

        try (final ExternalProcess ep = ExternalProcessExecutor
            .getInstance().use()//
            .setDirectory(dir.getPath())//
            .setExecutable(_ADVDEF._ADVDEF_PATH)//
            .addStringArgument("-4") //$NON-NLS-1$
            .addStringArgument("-i 64") //$NON-NLS-1$
            .addStringArgument("-z") //$NON-NLS-1$
            .addStringArgument("-q") //$NON-NLS-1$
            .addPathArgument(tempFile)//
            .setLogger(logger)//
            .setStdErr(EProcessStream.REDIRECT_TO_LOGGER)//
            .setStdIn(EProcessStream.IGNORE)//
            .setStdOut(EProcessStream.REDIRECT_TO_LOGGER)//
            .setMergeStdOutAndStdErr(true)//
            .create()) {
          if (ep.waitFor() != 0) {
            return null;
          }

          if (Files.readAttributes(tempFile, BasicFileAttributes.class)
              .size() >= compressed.length) {
            return compressed;
          }

          result = Files.readAllBytes(tempFile);
          try (final ByteArrayInputStream bis = new ByteArrayInputStream(
              result)) {
            try (final GZIPInputStream gis = new GZIPInputStream(bis)) {
              bos.reset();
              IOUtils.copy(gis, bos);
              buffer = bos.toByteArray();
              bos.reset();
              if (Arrays.equals(buffer, uncompressed)) {
                return result;
              }
              logger.warning("advdef produced incoherent result."); //$NON-NLS-1$
            }
          }

          return null;
        }
      }
    } catch (final Throwable ioe) { // ignore!
      if ((logger != null) && (logger.isLoggable(Level.SEVERE))) {
        logger.log(Level.SEVERE, "Error when running advdef.", ioe);//$NON-NLS-1$
      }
      return null;
    }
  }
}
