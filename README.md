# website
This repository holds the utilities and tools I use to build my (new) website.
They are really light-weight and designed to generate very compact and quickly-loading pages

## 1. Directory Structure
It prescribes the following folder structure:

- `tools`: The tools for building the site, included in the project.
- `website`: The website files, not included in the project.
  * `source`: the sources
    + `website.properties`: properties which are resolved in all HTML files via `${propertyName}`
    + `css`: the style sheets
    + `html`: the HTML files
    + `resources`: Same folder structure as `html`, contains all static resources such as images or icons.
      - `icons`: 32px*32px `png` icons for file types. You can take these from [my icons project](http://www.github.com/thomasWeise/icons) on GitHub.
  * `build`: The generated website


## 2. Functionality

The following functionality is provided by building the website:

1. Both `html` and `css` are rigorously minified.
2. All non-ASCII characters (Chinese, German Umlauts) are translated to HTML entities in `html` files.
3. Properties from `website.properties` are automatically resolved in all HTML files.
4. `{{url}}` resolves relative and absolute URLs towards the URL of the current element. Absolute URLs start with `/`.
5. `[[url]title]` generates links inside the website and resolves URLs. If `url` is not a `html` file, an icon of the corresponding file type (from `resources/icons`) will be added. Absolute URLs start with `/`.
6. ``<<path>>`` includes the fragment identified by `path`. If used inside an `html` file, this will look for a fragment with suffix `inc-html`. If used inside a `css` file, this will look for a fragment with suffix `inc-css`. All the rules in this enumeration are also recursively applied to the fragments.
6. `<dquote>xxx</dquote>` will resolve to double-quoted `xxx` in `html` files.
7. `<squote>xxx</squote>` will resolve to single-quoted `xxx` in `html` files.
8. create static gzip versions of static resources for faster serving

## 3. How to Use
Generate the above file structure (under the `website` folder, the `tools` folder is already there). Then run `build.xml` as `Ant` build. This will first build the website builder tool, then build the website.

## 4. Requirements

* [Apache `Ant`](http://ant.apache.org/bindownload.cgi) 1.9.4 or later
* [Maven](http://maven.apache.org/) version 3.0 and above

## 5. Static GZIP Resources
In order to serve stuff maximally fast, we often want to compress contents on the server side before
sending it. This can be done on-the-fly, but this creates workload on the server (the more the stronger
the compression we choose) and some hosters (such as 1and1) do not allow for that.

The website builder tool attempts to compress as many resources as possible and store them as static
`gzip` files. You can add the following rules (based on [1](http://stackoverflow.com/questions/11420992/)
and [2](https://developers.google.com/speed/docs/insights/EnableCompression)) to your `.htaccess` files in order to serve these
compressed versions:

    ## Serving gzip'ed resources if possible
    <IfModule mod_rewrite.c>
    RewriteEngine on
    RewriteBase /
    RewriteOptions Inherit
    ReWriteCond %{HTTP:accept-encoding} (gzip.*) 
    ReWriteCond %{REQUEST_FILENAME} !.+\.gz$ 
    RewriteCond %{REQUEST_FILENAME}.gz -f 
    
    # Redirect the following resource types
    RewriteRule "^(.*)\.(css|gif|html|jpg|js|pdf|png|txt)" "$1\.$2\.gz" [QSA]
    
    # Serve correct content types, and prevent mod_deflate double gzip.
    RewriteRule "\.css\.gz$" "-" [T=text/css,E=no-gzip:1]
    RewriteRule "\.gif\.gz$" "-" [T=image/gif,E=no-gzip:1]
    RewriteRule "\.html\.gz$" "-" [T=text/html,E=no-gzip:1]
    RewriteRule "\.jpg\.gz$" "-" [T=image/jpeg,E=no-gzip:1]
    RewriteRule "\.js\.gz$" "-" [T=text/javascript,E=no-gzip:1]
    RewriteRule "\.pdf\.gz$" "-" [T=application/pdf,E=no-gzip:1]
    RewriteRule "\.png\.gz$" "-" [T=image/png,E=no-gzip:1]
    RewriteRule "\.txt\.gz$" "-" [T=text/plain,E=no-gzip:1]
    
    <FilesMatch "(\.css\.gz|\.gif\.gz|\.html\.gz|\.jpg\.gz|\.js\.gz|\.pdf\.gz|\.png\.gz|\.txt\.gz)$">
    Header append Content-Encoding gzip
    Header append Vary Accept-Encoding
    </FilesMatch>
    </IfModule>
    
You may notice that we also try to serve gzip'ed `jpg`s, `png`s, and `pdf`s. This
may usually not make sense. The website builder, however, tries to also compress these
file types. If the compressed results are at least 50 bytes smaller than the original,
it will preserve the compressed versions so that they can be served. For all resources
where this fails, the above rules will simply serve the uncompressed version.