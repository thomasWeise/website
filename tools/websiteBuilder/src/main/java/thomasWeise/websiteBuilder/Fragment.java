package thomasWeise.websiteBuilder;

import java.nio.file.Path;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * A fragment of a website
 */
public final class Fragment {

  /** the owning context */
  public final Context context;

  /** the source path */
  public final Path sourcePath;

  /** the parent path */
  public final Path parent;

  /** the fragment type */
  public final EFragmentType type;

  /** the data */
  public final StringBuilder data;

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
  Fragment(final Context _context, final Path _sourcePath,
      final StringBuilder _data) {
    super();
    this.context = _context;
    this.sourcePath = _sourcePath;
    this.parent = PathUtils.normalize(this.sourcePath.getParent());
    this.type = EFragmentType.getType(this.sourcePath);
    this.data = _data;
  }

  /**
   * Resolve a URL or path to an absolute path in the source folder
   *
   * @param path
   *          the url or path
   * @return the resolved path
   */
  public final Path resolveSourcePath(final String path) {
    final Path ret;
    String str;

    str = TextUtils.prepare(path);
    if (str == null) {
      throw new IllegalArgumentException("Illegal path '" + path + '\''); //$NON-NLS-1$
    }

    if (str.charAt(0) == '/') {
      return this.context.resolveSourcePath(str.substring(1));
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
}