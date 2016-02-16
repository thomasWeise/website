package thomasWeise.websiteBuilder.expander;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;

/**
 * A website fragment loading context.
 */
final class _Context {

  /** the logger */
  final Logger logger;

  /** the base path of the source folder */
  final Path sourceBase;

  /** the base path of the resources folder */
  final Path resourcesBase;

  /** the destination base */
  final Path destBase;

  /** the buffer */
  char[] m_buffer;

  /**
   * Create the context.
   *
   * @param _logger
   *          the logger
   * @param _sourceBase
   *          the source base
   * @param _resourcesBase
   *          the resources base folder
   * @param _destBase
   *          the destination base
   */
  _Context(final Logger _logger, final Path _sourceBase,
      final Path _resourcesBase, final Path _destBase) {
    super();
    this.logger = _logger;
    this.sourceBase = _sourceBase;
    this.resourcesBase = _resourcesBase;
    this.destBase = _destBase;
    this.m_buffer = new char[16 * 1024 * 1024];
  }

  /**
   * Load a fragment
   *
   * @param path
   *          the path
   * @param attrs
   *          the file attributes
   * @return the fragment
   * @throws IOException
   *           if i/o fails
   */
  synchronized final _Fragment _load(final Path path,
      final BasicFileAttributes attrs) throws IOException {
    final Path usePath;
    final StringBuilder sb;
    final _Fragment frag;
    BasicFileAttributes useAttrs;
    char[] data;
    long lsize;
    int isize, read, current;

    if ((this.logger != null) && (this.logger.isLoggable(Level.INFO))) {
      this.logger.info("Now beginning to load path '" + path + '\'' + '.'); //$NON-NLS-1$
    }

    usePath = PathUtils.normalize(path);
    if (attrs == null) {
      useAttrs = Files.readAttributes(usePath, BasicFileAttributes.class);
    } else {
      useAttrs = attrs;
    }

    lsize = useAttrs.size();
    if ((lsize < 0L) || (lsize >= Integer.MAX_VALUE)) {
      throw new IOException("Illegal file size '" + lsize + //$NON-NLS-1$
          "' of fragment '" + path + '\'' + '.'); //$NON-NLS-1$
    }

    isize = ((int) lsize);
    data = this.m_buffer;
    if (data.length <= isize) {
      this.m_buffer = data = new char[isize + 1];
    }

    try (final InputStream is = PathUtils.openInputStream(usePath)) {
      try (final InputStreamReader fr = new InputStreamReader(is)) {

        read = 0;
        while ((current = fr.read(data, read, data.length - read)) >= 0) {
          read += current;
        }
      }
    }

    if ((read <= 0) || (read > isize)) {
      throw new IOException("File size of '" + path + //$NON-NLS-1$
          "' corrupt: expected no more than " + //$NON-NLS-1$
          isize + " chars but found at least " + read);//$NON-NLS-1$
    }
    sb = new StringBuilder(read);
    sb.append(data, 0, read);
    frag = new _Fragment(this, usePath, sb);

    if ((this.logger != null) && (this.logger.isLoggable(Level.INFO))) {
      this.logger.info("Finished loading path '" + path + //$NON-NLS-1$
          "' found " + read + " characters of text of type " + //$NON-NLS-1$//$NON-NLS-2$
          frag.type + '.');
    }

    return frag;
  }

  /**
   * Load a fragment
   *
   * @param path
   *          the path
   * @return the fragment
   * @throws IOException
   *           if i/o fails
   */
  synchronized final _Fragment _load(final Path path) throws IOException {
    return this._load(path, null);
  }

  /**
   * Check whether the given path is correct
   *
   * @param str
   *          the string
   * @param path
   *          the path
   */
  final void _checkSourcePath(final String str, final Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Cannot resolve source path '" //$NON-NLS-1$
          + str + '\'' + '.');
    }
    if (!(path.startsWith(this.sourceBase))) {
      throw new IllegalArgumentException("Source path '" + path + //$NON-NLS-1$
          "' obtained from source path '" + str + //$NON-NLS-1$
          "' lies outside destination base path '" + //$NON-NLS-1$
          this.sourceBase + '\'' + '.');
    }
  }

  /**
   * Resolve a URL or path to an absolute path in the source folder
   *
   * @param path
   *          the path
   * @return the resolved path
   */
  final Path _resolveSourcePath(final String path) {
    final Path ret;

    ret = PathUtils.normalize(this.sourceBase.resolve(path));
    this._checkSourcePath(path, ret);
    return ret;
  }

  /**
   * Convert a source path to a destination path
   *
   * @param source
   *          the source path
   * @return the destination path
   */
  final Path _sourcePathToDestPath(final Path source) {
    final Path ret;

    ret = PathUtils.normalize(
        this.destBase.resolve(this.sourceBase.relativize(source)));
    if (ret == null) {
      throw new IllegalArgumentException("Cannot resolve source path '" //$NON-NLS-1$
          + source + '\'' + '.');
    }
    if (ret.startsWith(this.destBase)) {
      return ret;
    }
    throw new IllegalArgumentException("Destination path '" + ret + //$NON-NLS-1$
        "' obtained from source path '" + source + //$NON-NLS-1$
        "' lies outside destination base path '" + //$NON-NLS-1$
        this.destBase + '\'' + '.');
  }
}
