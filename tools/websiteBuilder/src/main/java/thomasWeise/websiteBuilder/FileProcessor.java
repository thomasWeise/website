package thomasWeise.websiteBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** The file processor. */
public class FileProcessor {

  /**
   * process the file
   * 
   * @param source
   *          the source path
   * @param dest
   *          the dest path
   * @param logger
   *          the logger
   * @param attrs
   *          the file attribute
   * @throws IOException
   *           if i/o fails
   */
  public static final void processFile(final Path source, final Path dest,
      BasicFileAttributes attrs, final Logger logger) throws IOException {
    int size, read, current;
    char[] data, actual;

    try (final InputStream is = PathUtils.openInputStream(source)) {
      try (final InputStreamReader fr = new InputStreamReader(is)) {
        size = ((int) (attrs.size()));
        data = new char[size << 1];

        read = 0;
        while ((current = fr.read(data, read, data.length - read)) >= 0) {
          read += current;
        }
        if (read > size) {
          throw new IOException("File size of '" + source + "' corrupt."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        actual = new char[read];
        System.arraycopy(data, 0, actual, 0, read);
        data = null;
        processData(actual, dest, logger);
      }
    }
  }

  /**
   * process the data
   * 
   * @param data
   *          the data
   * @param dest
   *          the dest path
   * @param logger
   *          the logger
   * @throws IOException
   *           if i/o fails
   */
  public static final void processData(final char[] data, final Path dest,
      final Logger logger) throws IOException {
    final ITextOutput to;

    try (final OutputStream os = PathUtils.openOutputStream(dest)) {
      try (final OutputStreamWriter osw = new OutputStreamWriter(os,
          "ISO-8859-1")) { //$NON-NLS-1$
        try (final BufferedWriter bw = new BufferedWriter(osw)) {
          to = XMLCharTransformer.getInstance()
              .transform(AbstractTextOutput.wrap(bw));

          for (char ch : data) {
            if (ch <= 'z') {
              bw.write(ch);
            } else {
              to.append(ch);
            }
          }
        }
      }
    }
  }
}
