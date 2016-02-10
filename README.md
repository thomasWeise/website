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
      - `icons`: 20pxx20px `png` icons for file types. You can take these from [my icons project](http://www.github.com/thomasWeise/icons) on GitHub.
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

## 3. How to Use
Generate the above file structure (under the `website` folder, the `tools` folder is already there). Then run `build.xml` as `Ant` build. This will first build the website builder tool, then build the website.


## 4. Requirements

* [Apache `Ant`](http://ant.apache.org/bindownload.cgi) 1.9.4 or later
* [Maven](http://maven.apache.org/) version 3.0 and above