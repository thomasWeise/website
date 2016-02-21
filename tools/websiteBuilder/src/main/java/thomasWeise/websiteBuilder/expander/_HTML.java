package thomasWeise.websiteBuilder.expander;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.AbstractTextOutput;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.text.transformations.LaTeXCharTransformer;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/** The HTML file processor. */
class _HTML {

  /** the opening tag for LaTeX processing */
  private static final String LATEX_OPEN = "<latex>"; //$NON-NLS-1$
  /** the closing tag for LaTeX processing */
  private static final String LATEX_CLOSE = "</latex>"; //$NON-NLS-1$

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
  static final void _processHTML(final _Fragment fragment, final Path dest)
      throws IOException {
    _HTML.__processFragment(fragment, fragment.parent);
    _HTML.__store(fragment, dest);
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
  private static final void __processFragment(final _Fragment fragment,
      final Path relative) throws IOException {
    boolean looper;

    do {
      looper = _HTML.__LaTeX(fragment.data);
      if (_HTML.__replacements(fragment.data)) {
        looper = true;
      }
      if (_HTML.__resolveURLs(fragment, relative)) {
        looper = true;
      }
      if (_HTML.__resolveLinks(fragment, relative)) {
        looper = true;
      }
      if (_HTML.__resolveIncludes(fragment, relative)) {
        looper = true;
      }
    } while (looper);
  }

  /**
   * Perform default replacements.
   *
   * @param data
   *          the data
   * @return {@code true} if something has changed, {@code false} otherwise
   */
  private static final boolean __replacements(final StringBuilder data) {
    boolean changed;

    changed = _HTML.__replace(data, "<dquote> ", "<dquote> "); //$NON-NLS-1$//$NON-NLS-2$
    if (_HTML.__replace(data, " </dquote> ", "</dquote> ")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    if (_HTML.__replace(data, "<dquote>", "&ldquo;")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    if (_HTML.__replace(data, "</dquote>", "&rdquo;")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    if (_HTML.__replace(data, "<squote> ", "<squote>")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    if (_HTML.__replace(data, " </squote>", "</squote>")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    if (_HTML.__replace(data, "<squote>", "&lsquo;")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    if (_HTML.__replace(data, "</squote>", "&rsquo;")) { //$NON-NLS-1$//$NON-NLS-2$
      changed = true;
    }
    return changed;
  }

  /**
   * Resolve LaTeX
   *
   * @param data
   *          the data
   * @return {@code true} if something has changed, {@code false} otherwise
   */
  private static final boolean __LaTeX(final StringBuilder data) {
    boolean changed;
    int i, j;

    changed = false;
    i = 0;
    while (i >= 0) {
      i = data.indexOf(_HTML.LATEX_OPEN, i);
      if (i >= 0) {
        j = data.indexOf(_HTML.LATEX_CLOSE, i);
        if (j > i) {

          data.replace(i, j + _HTML.LATEX_CLOSE.length(),
              LaTeXCharTransformer.getInstance().transform(
                  data.substring(i + _HTML.LATEX_OPEN.length(), j)));

          changed = true;
          continue;
        }
      }
      break;
    }

    return changed;
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
   * @return {@code true} if something has changed, {@code false} otherwise
   */
  private static final boolean __replace(final StringBuilder data,
      final String src, final String repl) {
    final int sl;
    boolean changed;
    int j;

    j = 0;
    sl = src.length();
    changed = false;
    while ((j = data.indexOf(src, j)) >= 0) {
      changed = true;
      data.replace(j, j + sl, repl);
    }
    return changed;
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
  private static final boolean __resolveIncludes(final _Fragment fragment,
      final Path relative) throws IOException {
    boolean changed;
    _Fragment resolved;
    int i, j;

    changed = false;
    i = 0;
    while (i >= 0) {
      i = fragment.data.indexOf("<<", i); //$NON-NLS-1$
      if (i >= 0) {
        j = fragment.data.indexOf(">>", i); //$NON-NLS-1$
        if (j > i) {
          resolved = fragment.context._load(//
              fragment._resolveSourcePath(//
                  fragment.data.substring(i + 2, j) + '.'
                      + _EFragmentType.HTML_INCLUDE.suffix//
          ));

          _HTML.__processFragment(resolved, relative);

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
   * Resolve urls {{ urls }}
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
  private static final boolean __resolveURLs(final _Fragment fragment,
      final Path relative) throws IOException {
    int i, j;
    boolean changed;

    i = 0;
    changed = false;
    while (i >= 0) {
      i = fragment.data.indexOf("{{", i); //$NON-NLS-1$
      if (i >= 0) {
        j = fragment.data.indexOf("}}", i); //$NON-NLS-1$
        if (j > i) {
          fragment.data.replace(i, j + 2, //
              fragment._resolveAndRelativize(//
                  fragment.data.substring(i + 2, j), null, relative));
          changed = true;
          continue;
        }
      }
      break;
    }
    return changed;
  }

  /**
   * Resolve links [[ link ] title ]
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
  private static final boolean __resolveLinks(final _Fragment fragment,
      final Path relative) throws IOException {
    int i, j, k, z;
    String rawURL, rawPath, rawAnchor, url, title, replace, extension;
    boolean changed;

    changed = false;
    i = 0;
    while (i >= 0) {
      i = fragment.data.indexOf("[[", i); //$NON-NLS-1$
      if (i >= 0) {
        j = fragment.data.indexOf("]", i); //$NON-NLS-1$
        if (j > i) {
          k = fragment.data.indexOf("]", j + 1); //$NON-NLS-1$
          if (k > j) {

            rawURL = fragment.data.substring(i + 2, j);
            z = rawURL.lastIndexOf('#');
            if (z >= 0) {
              rawPath = TextUtils.prepare(rawURL.substring(0, z));
              rawAnchor = TextUtils.prepare(rawURL).substring(z + 1);
            } else {
              rawPath = rawURL;
              rawAnchor = null;
            }

            url = fragment._resolveAndRelativize(rawPath, rawAnchor,
                relative);

            z = rawPath.lastIndexOf('/');

            extension = PathUtils.getFileExtension(
                (z > 0) ? rawPath.substring(z + 1) : rawPath);
            if (extension != null) {
              extension = TextUtils.toLowerCase(extension);
            } else {// assume HTML
              extension = _EFragmentType.HTML.suffix;
            }
            title = TextUtils.prepare(fragment.data.substring(j + 1, k));

            if (title == null) {
              title = extension;
            }

            replace = "<a href=\"" + url //$NON-NLS-1$
                + "\">"; //$NON-NLS-1$

            if (!(_EFragmentType.HTML.suffix
                .equalsIgnoreCase(extension))) {
              replace += "<img src=\"" //$NON-NLS-1$
                  + relative
                      .relativize(//
                          fragment.context.sourceBase.resolve(//
                              "icons/" + extension + ".png"))//$NON-NLS-1$ //$NON-NLS-2$
                      .toString()
                  + "\" class=\"icon\" alt=\"" //$NON-NLS-1$
                  + extension + " icon\" />&nbsp;"; //$NON-NLS-1$
            }

            replace += title + "</a>"; //$NON-NLS-1$

            fragment.data.replace(i, k + 1, replace);
            changed = true;
            continue;
          }
        }
      }
      break;
    }

    return changed;
  }

  /**
   * store the HTML fragment
   *
   * @param fragment
   *          the html fragment
   * @param dest
   *          the dest path
   * @throws IOException
   *           if i/o fails
   */
  private static final void __store(final _Fragment fragment,
      final Path dest) throws IOException {
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

          length = fragment.data.length();

          for (i = 0; i < length; i++) {
            ch = fragment.data.charAt(i);
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
