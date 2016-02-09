package thomasWeise.websiteBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/** The HTML file processor. */
public class CSS {

  /**
   * Process a HTML document
   *
   * @param fragment
   *          the fragment
   * @param dest
   *          the destination file
   * @throws IOException
   *           if i/o fails
   */
  public static final void processCSS(final Fragment fragment,
      final Path dest) throws IOException {
    CSS.__processFragment(fragment, fragment.parent);
    CSS.__store(fragment, dest);
  }

  /**
   * Process the fragment
   *
   * @param fragment
   *          the fragment
   * @param relative
   *          the path towards which all output paths have to be
   *          relativized against
   * @throws IOException
   *           if i/o fails
   */
  private static final void __processFragment(final Fragment fragment,
      final Path relative) throws IOException {
    boolean looper;

    do {
      looper = CSS.__resolveIncludes(fragment, relative);
    } while (looper);
  }

  /**
   * Resolve includes << include >>
   *
   * @param fragment
   *          the fragment
   * @param relative
   *          the path towards which all output paths have to be
   *          relativized against
   * @return {@code true} if something has changed, {@code false} otherwise
   * @throws IOException
   *           if i/o fails
   */
  private static final boolean __resolveIncludes(final Fragment fragment,
      final Path relative) throws IOException {
    boolean changed;
    Fragment resolved;
    int i, j;

    changed = false;
    i = 0;
    while (i >= 0) {
      i = fragment.data.indexOf("<<", i); //$NON-NLS-1$
      if (i >= 0) {
        j = fragment.data.indexOf(">>", i); //$NON-NLS-1$
        if (j > i) {
          resolved = fragment.context.load(//
              fragment.resolveSourcePath(//
                  fragment.data.substring(i + 2, j) + '.'
                      + EFragmentType.CSS_INCLUDE.suffix//
          ));

          CSS.__processFragment(resolved, relative);

          fragment.data.replace(i, j + 2, resolved.data.toString());
          resolved = null;
          changed = true;
          continue;
        }
      }
      break;
    }

    return changed;
  }

  /**
   * store the CSS fragment
   *
   * @param fragment
   *          the css fragment
   * @param dest
   *          the dest path
   * @throws IOException
   *           if i/o fails
   */
  private static final void __store(final Fragment fragment,
      final Path dest) throws IOException {
    try (final OutputStream os = PathUtils.openOutputStream(dest)) {
      try (final OutputStreamWriter osw = new OutputStreamWriter(os,
          "ISO-8859-1")) { //$NON-NLS-1$
        try (final BufferedWriter bw = new BufferedWriter(osw)) {
          bw.append(fragment.data);
        }
      }
    }
  }
}
