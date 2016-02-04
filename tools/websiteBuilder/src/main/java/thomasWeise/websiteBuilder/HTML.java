package thomasWeise.websiteBuilder;

import java.nio.file.Path;

import org.optimizationBenchmarking.utils.text.TextUtils;

/** The HTML file processor. */
public class HTML {

  /**
   * Process a HTML document
   * 
   * @param source
   *          the source path
   * @param base
   *          the base path
   * @param data
   *          the document
   */
  public static final void processHTML(final Path source, final Path base,
      final StringBuilder data) {
    final Path parent;
    int i, j;

    parent = source.getParent();

    j = 0;
    while (j >= 0) {
      i = data.indexOf("{{", j); //$NON-NLS-1$
      if (i > 0) {
        j = data.indexOf("}}", i); //$NON-NLS-1$
        if (j > i) {
          data.replace(i, j + 2, //
              resolve(data.substring(i + 2, j), parent, base));
          continue;
        }
      }
      break;
    }
  }

  /**
   * Resolve a URL
   * 
   * @param url
   *          the url
   * @param parent
   *          the parent
   * @param base
   *          the base url
   * @return the resolved url
   */
  public static final String resolve(final String url, final Path parent,
      final Path base) {
    final Path path, path2;
    String str;

    str = TextUtils.prepare(url);
    if (str == null) {
      throw new IllegalArgumentException("Illegal url '" + url + '\''); //$NON-NLS-1$
    }

    if (str.charAt(0) == '/') {
      path = base.resolve(str.substring(1));
    } else {
      path = parent.resolve(str);
    }

    if ((path == null) || (!(path.startsWith(base)))) {
      throw new IllegalArgumentException(
          "Illegal relative url '" + url + '\''); //$NON-NLS-1$
    }

    path2 = parent.relativize(path);
    if (path2 == null) {
      throw new IllegalArgumentException("Illegal relative url '" + url + //$NON-NLS-1$
          "' resolved to '" + path + '\''); //$NON-NLS-1$
    }
    return path2.toString();
  }
}
