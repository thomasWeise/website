package thomasWeise.websiteBuilder;

import java.nio.file.Path;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
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

    parent = source.getParent();
    HTML.__resolveURLs(parent, base, data);
    HTML.__resolveLinks(parent, base, data);
    __replace(data);
  }

  /**
   * Perform default replacements.
   * 
   * @param data
   *          the data
   */
  private static final void __replace(final StringBuilder data) {
    __replace(data, "<dquote> ", "<dquote> "); //$NON-NLS-1$//$NON-NLS-2$
    __replace(data, " </dquote> ", "</dquote> "); //$NON-NLS-1$//$NON-NLS-2$
    __replace(data, "<dquote>", "&ldquo;"); //$NON-NLS-1$//$NON-NLS-2$
    __replace(data, "</dquote>", "&rdquo;");//$NON-NLS-1$//$NON-NLS-2$
    __replace(data, "<squote> ", "<squote>"); //$NON-NLS-1$//$NON-NLS-2$
    __replace(data, " </squote>", "</squote>"); //$NON-NLS-1$//$NON-NLS-2$
    __replace(data, "<squote>", "&lsquo;"); //$NON-NLS-1$//$NON-NLS-2$
    __replace(data, "</squote>", "&rsquo;");//$NON-NLS-1$//$NON-NLS-2$
  }

  /**
   * Perform default replacements.
   * 
   * @param src
   *          the string to be replaced
   * @param repl
   *          the replacement
   * @param data
   *          the data
   */
  private static final void __replace(final StringBuilder data,
      final String src, final String repl) {
    final int sl;
    int j;

    j = 0;
    sl = src.length();

    while (j >= 0) {
      j = data.indexOf(src, j);
      if (j >= 0) {
        data.replace(j, j + sl, repl);
        continue;
      }
      break;
    }
  }

  /**
   * Resolve urls {{ urls }}
   *
   * @param parent
   *          the parent dir
   * @param base
   *          the base path
   * @param data
   *          the document
   */
  private static final void __resolveURLs(final Path parent,
      final Path base, final StringBuilder data) {
    int i, j;

    j = 0;
    while (j >= 0) {
      i = data.indexOf("{{", j); //$NON-NLS-1$
      if (i > 0) {
        j = data.indexOf("}}", i); //$NON-NLS-1$
        if (j > i) {
          data.replace(i, j + 2, //
              HTML.__resolve(data.substring(i + 2, j), parent, base));
          continue;
        }
      }
      break;
    }
  }

  /**
   * Resolve links [[ link ] title ]
   *
   * @param parent
   *          the parent dir
   * @param base
   *          the base path
   * @param data
   *          the document
   */
  private static final void __resolveLinks(final Path parent,
      final Path base, final StringBuilder data) {
    int i, j, k;
    String url, title, replace, extension;

    k = 0;
    while (k >= 0) {
      i = data.indexOf("[[", k); //$NON-NLS-1$
      if (i > 0) {
        j = data.indexOf("]", i); //$NON-NLS-1$
        if (j > i) {
          k = data.indexOf("]", j + 1); //$NON-NLS-1$
          if (k > j) {

            url = HTML.__resolve(data.substring(i + 2, j), parent, base);
            extension = PathUtils.getFileExtension(url).toLowerCase();
            title = TextUtils.prepare(data.substring(j + 1, k));

            if (title == null) {
              title = extension;
            }

            replace = "<a href=\"" + url //$NON-NLS-1$
                + "\">"; //$NON-NLS-1$

            if (!("html".equalsIgnoreCase(extension))) {//$NON-NLS-1$
              replace += "<img src=\"" + //$NON-NLS-1$
                  parent.relativize(base.resolve("icons/" + extension + //$NON-NLS-1$
                      ".png")).toString() //$NON-NLS-1$
                  + "\" class=\"icon" //$NON-NLS-1$
                  + "\" />&nbsp;"; //$NON-NLS-1$
            }

            replace += title + "</a>"; //$NON-NLS-1$

            data.replace(i, k + 1, replace);
            continue;
          }
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
  private static final String __resolve(final String url,
      final Path parent, final Path base) {
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
