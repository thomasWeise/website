package thomasWeise.websiteBuilder.expander;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/**
 * A fragment of a website
 */
final class _Fragment {

  /** the owning context */
  final _Context context;

  /** the source path */
  final Path sourcePath;

  /** the parent path */
  final Path parent;

  /** the fragment type */
  final _EFragmentType type;

  /** the data */
  final StringBuilder data;

  /**
   * Create
   *
   * @param _context
   *          the owning context
   * @param _sourcePath
   *          the source path
   * @param _data
   *          the data
   */
  _Fragment(final _Context _context, final Path _sourcePath,
      final StringBuilder _data) {
    super();
    this.context = _context;
    this.sourcePath = _sourcePath;
    this.parent = PathUtils.normalize(this.sourcePath.getParent());
    this.type = _EFragmentType.getType(this.sourcePath);
    this.data = _data;
  }

  /**
   * Resolve a URL or path to an absolute path in the source folder
   *
   * @param path
   *          the url or path
   * @return the resolved path
   */
  final Path _resolveSourcePath(final String path) {
    final Path ret;
    String str;

    str = TextUtils.prepare(path);
    if (str == null) {
      throw new IllegalArgumentException("Illegal path '" + path + '\''); //$NON-NLS-1$
    }

    if (str.charAt(0) == '/') {
      return this.context._resolveSourcePath(str.substring(1));
    }
    ret = PathUtils.normalize(this.parent.resolve(str));
    this.context._checkSourcePath(path, ret);
    return ret;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return this.type.suffix + '[' + this.sourcePath + ']';
  }

  /**
   * Resolve a source path or URI and relativize it.
   *
   * @param path
   *          the path
   * @param anchor
   *          a potential fragment
   * @param relativeTo
   *          the path to which resolve
   * @return the combined, resolved path
   * @throws IOException
   *           if something goes wrong
   */
  final String _resolveAndRelativize(final String path,
      final String anchor, final Path relativeTo) throws IOException {
    String resolved;

    if (path.startsWith("http://") || //$NON-NLS-1$
        path.startsWith("https://") || //$NON-NLS-1$
        path.startsWith("ftp://")) { //$NON-NLS-1$
      resolved = path;
      if (anchor != null) {
        resolved += '#' + anchor;
      }
      try {
        resolved = new URI(resolved).normalize().toString();
      } catch (final URISyntaxException use) {
        throw new IOException("Invalid path '" + //$NON-NLS-1$
            path + "' and anchor '" + anchor + //$NON-NLS-1$
            "' for relative path '" + relativeTo//$NON-NLS-1$
            + '\'' + '.', use);
      }
    } else {
      resolved = relativeTo.relativize(//
          this._resolveSourcePath(path)).toString();
      if (anchor != null) {
        resolved += '#' + anchor;
      }
    }

    return XMLCharTransformer.getInstance().transform(resolved);
  }
}