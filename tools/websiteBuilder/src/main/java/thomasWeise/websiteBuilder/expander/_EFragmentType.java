package thomasWeise.websiteBuilder.expander;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/**
 * The fragment types.
 */
enum _EFragmentType {

  /** the HTML type */
  HTML("html", true) { //$NON-NLS-1$
    /** {@inheritDoc} */
    @Override
    void _process(final _Fragment fragment, final Path dest)
        throws IOException {
      if ((fragment.context.logger != null)
          && (fragment.context.logger.isLoggable(Level.INFO))) {
        fragment.context.logger
            .info("Now processing fragment " + fragment);//$NON-NLS-1$
      }
      thomasWeise.websiteBuilder.expander._HTML._processHTML(fragment,
          dest);
      if ((fragment.context.logger != null)
          && (fragment.context.logger.isLoggable(Level.INFO))) {
        fragment.context.logger
            .info("Finished processing fragment " + fragment); //$NON-NLS-1$
      }
    }
  },
  /** the css type */
  CSS("css", true) { //$NON-NLS-1$
    /** {@inheritDoc} */
    @Override
    void _process(final _Fragment fragment, final Path dest)
        throws IOException {
      if ((fragment.context.logger != null)
          && (fragment.context.logger.isLoggable(Level.INFO))) {
        fragment.context.logger
            .info("Now processing fragment " + fragment);//$NON-NLS-1$
      }
      thomasWeise.websiteBuilder.expander._CSS._processCSS(fragment, dest);
      if ((fragment.context.logger != null)
          && (fragment.context.logger.isLoggable(Level.INFO))) {
        fragment.context.logger
            .info("Finished processing fragment " + fragment); //$NON-NLS-1$
      }
    }
  },
  /** the html include */
  HTML_INCLUDE("inc-html", false), //$NON-NLS-1$
  /** the css include */
  CSS_INCLUDE("inc-css", false); //$NON-NLS-1$

  /** the suffix */
  final String suffix;

  /** should this fragment type be processed? */
  final boolean shouldProcess;

  /**
   * Create the fragment type
   *
   * @param _suffix
   *          the file suffix
   * @param _shouldProcess
   *          should this fragment type be processed?
   */
  private _EFragmentType(final String _suffix,
      final boolean _shouldProcess) {
    this.suffix = _suffix;
    this.shouldProcess = _shouldProcess;
  }

  /**
   * Get the fragment type
   *
   * @param path
   *          the path
   * @return the fragment type
   */
  static final _EFragmentType getType(final Path path) {
    final String _suffix;

    _suffix = PathUtils.getFileExtension(path);
    for (final _EFragmentType type : _EFragmentType.values()) {
      if (type.suffix.equalsIgnoreCase(_suffix)) {
        return type;
      }
    }
    return null;
  }

  /**
   * Process the fragment.
   *
   * @param fragment
   *          the fragment
   * @param dest
   *          the destination path
   * @throws IOException
   *           the i/o exception
   */
  void _process(final _Fragment fragment, final Path dest)
      throws IOException {
    throw new IOException("Fragment " + fragment + //$NON-NLS-1$
        " cannot be processed."); //$NON-NLS-1$
  }
}
