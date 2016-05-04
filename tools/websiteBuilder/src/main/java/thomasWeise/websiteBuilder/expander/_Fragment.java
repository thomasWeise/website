package thomasWeise.websiteBuilder.expander;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.transformations.XMLCharTransformer;

/**
 * A fragment of a website
 */
final class _Fragment {

  /** the owning context */
  final _Context context;

  /** the owner fragment */
  private final _Fragment m_owner;

  /** the source path */
  final Path sourcePath;

  /** the parent path */
  final Path parent;

  /** the fragment type */
  final _EFragmentType type;

  /** the data */
  final StringBuilder data;

  /** the citation ids */
  private HashMap<Object, Integer> m_citationIDs;
  /** the citations */
  private StringBuilder m_citations;
  /** the citation counter */
  private int m_citationCounter;

  /** the footnotes */
  private StringBuilder m_footnotes;
  /** the footnote ids */
  private HashMap<String, Integer> m_footnoteIDs;
  /** the footnotes counter */
  private int m_footnoteCounter;

  /**
   * Create
   *
   * @param _owner
   *          the owning fragment
   * @param _context
   *          the owning context
   * @param _sourcePath
   *          the source path
   * @param _data
   *          the data
   */
  _Fragment(final _Fragment _owner, final _Context _context,
      final Path _sourcePath, final StringBuilder _data) {
    super();
    this.m_owner = _owner;
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
   * @throws IOException
   *           if path resolution fails
   */
  final Path _resolveSourcePath(final String path) throws IOException {
    final Path ret, check;
    String str;

    str = TextUtils.prepare(path);
    if (str == null) {
      throw new IllegalArgumentException("Illegal path '" + path + '\''); //$NON-NLS-1$
    }

    if (str.charAt(0) == '/') {
      ret = this.context._resolveSourcePath(str.substring(1));
    } else {
      ret = PathUtils.normalize(this.parent.resolve(str));
      this.context._checkSourcePath(path, ret);
    }

    if (Files.exists(ret)) {
      return ret;
    }

    check = PathUtils.normalize(this.context.resourcesBase.resolve(//
        this.context.sourceBase.relativize(ret)));

    if (Files.exists(check)) {
      return ret;
    }

    throw new IOException("Could not find element '" + path + //$NON-NLS-1$
        "' relative to '" + this.parent + //$NON-NLS-1$
        "' - neither '" + ret + //$NON-NLS-1$
        "' nor '" + check + //$NON-NLS-1$
        "' exists.");//$NON-NLS-1$
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

  /**
   * cite the given source path.
   *
   * @param thePaths
   *          the source paths
   * @param relativize
   *          the path against which all links need to be relativized
   * @return the citation id
   * @throws IOException
   *           if I/O fails
   */
  final String _cite(final String thePaths, final Path relativize)
      throws IOException {
    final String[] paths;
    final StringBuilder sb;
    final int initial;
    int index;

    if (this.m_owner != null) {
      return this.m_owner._cite(thePaths, relativize);
    }

    paths = thePaths.split(","); //$NON-NLS-1$
    sb = new StringBuilder();
    sb.append("&nbsp;["); //$NON-NLS-1$
    initial = sb.length();
    for (index = 0; index < paths.length; index++) {
      if (sb.length() > initial) {
        sb.append(',');
      }
      this.__cite(this._resolveSourcePath((TextUtils.prepare(paths[index])
          + '.' + _EFragmentType.HTML_INCLUDE.suffix)), relativize, sb);
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * cite the given source path.
   *
   * @param thePath
   *          the source path
   * @param relativize
   *          the path against which all links need to be relativized
   * @param dest
   *          the destination string builder
   * @throws IOException
   *           if I/O fails
   */
  private final void __cite(final Path thePath, final Path relativize,
      final StringBuilder dest) throws IOException {
    final _Fragment fragment;
    Object pathId;
    Integer id1, id2;
    int intVal;

    if (this.m_owner != null) {
      this.m_owner.__cite(thePath, relativize, dest);
      return;
    }

    pathId = Files.readAttributes(thePath, BasicFileAttributes.class)
        .fileKey();

    finder: synchronized (this) {
      if (this.m_citationCounter <= 0) {
        this.m_citationIDs = new HashMap<>();
        this.m_citations = new StringBuilder();
        id1 = id2 = null;
      } else {
        if (pathId != null) {
          id2 = this.m_citationIDs.get(pathId);
        } else {
          id2 = null;
        }
        id1 = this.m_citationIDs.get(thePath);

        if (id1 != null) {
          if ((id2 == null) && (pathId != null)) {
            this.m_citationIDs.put(pathId, id1);
          }
          intVal = id1.intValue();
          break finder;
        }
        if (id2 != null) {
          this.m_citationIDs.put(id1, id2);
          intVal = id2.intValue();
          break finder;
        }
      }

      intVal = (++this.m_citationCounter);
      id1 = Integer.valueOf(intVal);
      this.m_citationIDs.put(thePath, id1);
      if (pathId != null) {
        this.m_citationIDs.put(pathId, id1);
      }

      fragment = this.context._load(this, thePath);
      _HTML._processFragment(fragment, relativize);

      this.m_citations.append("<li><span id=\"cte") //$NON-NLS-1$
          .append(intVal).append("\"></span>")//$NON-NLS-1$
          .append(fragment.data).append("</li>"); //$NON-NLS-1$
    }

    dest.append("<a href=\"#cte")//$NON-NLS-1$
        .append(intVal).append('"').append('>').append(intVal)
        .append("</a>"); //$NON-NLS-1$
  }

  /**
   * get the citations
   *
   * @param style
   *          the style to use
   * @return the citations
   */
  final String _getCitations(final String style) {
    final StringBuilder sb;

    if (this.m_owner != null) {
      return this.m_owner._getCitations(style);
    }

    synchronized (this) {
      if ((this.m_citationCounter <= 0)
          || (this.m_citations.length() <= 0)) {
        return ""; //$NON-NLS-1$
      }

      sb = new StringBuilder();
      sb.append("<ol");//$NON-NLS-1$
      if (style != null) {
        sb.append(" class=\"");//$NON-NLS-1$
        sb.append(style);
        sb.append('"');
      }
      sb.append('>');
      sb.append(this.m_citations);
      this.m_citations.setLength(0);
      this.m_citationIDs.clear();
    }
    sb.append("</ol>");//$NON-NLS-1$
    return sb.toString();
  }

  /**
   * add a given footnote
   *
   * @param footnote
   *          the footnote
   * @return the footnote id
   */
  final String _footnote(final String footnote) {
    final String useNote;
    int counter;
    Integer value;

    if (this.m_owner != null) {
      return this.m_owner._footnote(footnote);
    }
    useNote = TextUtils.prepare(footnote);
    synchronized (this) {
      counter = this.m_footnoteCounter;
      if (counter <= 0) {
        this.m_footnotes = new StringBuilder();
        this.m_footnoteIDs = new HashMap<>();
      }

      value = this.m_footnoteIDs.get(useNote);
      if (value != null) {
        counter = value.intValue();
      } else {
        this.m_footnoteCounter = (++counter);
        this.m_footnoteIDs.put(useNote, Integer.valueOf(counter));
      }

      this.m_footnotes.append("<li><span id=\"ftnte") //$NON-NLS-1$
          .append(counter).append("\"/></span>")//$NON-NLS-1$
          .append(useNote).append("</li>"); //$NON-NLS-1$
    }
    return ((((("<sup><a href=\"#ftnte" + counter) //$NON-NLS-1$
        + '"') + '>') + counter) + "</a></sup>"); //$NON-NLS-1$
  }

  /**
   * get the footnotes
   *
   * @param style
   *          the style to use
   * @return the citations
   */
  final String _getFootnotes(final String style) {
    final StringBuilder sb;

    if (this.m_owner != null) {
      return this.m_owner._getFootnotes(style);
    }

    synchronized (this) {
      if ((this.m_footnoteCounter <= 0)
          || (this.m_footnotes.length() <= 0)) {
        return ""; //$NON-NLS-1$
      }
      this.m_footnoteIDs.clear();
      sb = new StringBuilder();
      sb.append("<ol");//$NON-NLS-1$
      if (style != null) {
        sb.append(" class=\"");//$NON-NLS-1$
        sb.append(style);
        sb.append('"');
      }
      sb.append('>');
      sb.append(this.m_footnotes);
      this.m_footnotes.setLength(0);
    }
    sb.append("</ol>");//$NON-NLS-1$
    return sb.toString();
  }
}