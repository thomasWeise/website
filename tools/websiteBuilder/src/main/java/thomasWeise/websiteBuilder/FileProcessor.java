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
   * @param base
   *          the base path
   * @param logger
   *          the logger
   * @param attrs
   *          the file attribute
   * @throws IOException
   *           if i/o fails
   */
  public static final void processFile(final Path source, final Path dest,
      final Path base, BasicFileAttributes attrs, final Logger logger)
          throws IOException {
    final StringBuilder data;

    data = load(source, attrs);
    
    if("html".equalsIgnoreCase(PathUtils.getFileExtension(source))){ //$NON-NLS-1$
      HTML.processHTML(source, base, data);
    }
    
    store(data, dest);
  }
  
  
 
  

  
  /**
   * Loads the contents of a given path.
   * 
   * @param path
   *          the path
   * @return the loaded contents
   * @param attrs
   *          the file attribute
   * @throws IOException
   *           if i/o fails
   */
  public static final StringBuilder load(final Path path,
      final BasicFileAttributes attrs) throws IOException {
    final char[] data;
    final StringBuilder sb;
    int size, read, current;

    try (final InputStream is = PathUtils.openInputStream(path)) {
      try (final InputStreamReader fr = new InputStreamReader(is)) {
        size = ((int) (attrs.size()));
        data = new char[size << 1];

        read = 0;
        while ((current = fr.read(data, read, data.length - read)) >= 0) {
          read += current;
        }
      }
    }

    if ((read <= 0) || (read > size)) {
      throw new IOException("File size of '" + path + "' corrupt."); //$NON-NLS-1$ //$NON-NLS-2$
    }
    sb = new StringBuilder(read);
    sb.append(data);
    return sb;
  }

  /**
   * process the data
   * 
   * @param data
   *          the data
   * @param dest
   *          the dest path
   * @throws IOException
   *           if i/o fails
   */
  public static final void store(final CharSequence data, final Path dest)
      throws IOException {
    final ITextOutput to;
    final int length;
    char ch;
    int i;

    try (final OutputStream os = PathUtils.openOutputStream(dest)) {
      try (final OutputStreamWriter osw = new OutputStreamWriter(os,
          "ISO-8859-1")) { //$NON-NLS-1$
        try (final BufferedWriter bw = new BufferedWriter(osw)) {
          to = XMLCharTransformer.getInstance()
              .transform(AbstractTextOutput.wrap(bw));

          length = data.length();

          for (i = 0; i < length; i++) {
            ch = data.charAt(i);
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
