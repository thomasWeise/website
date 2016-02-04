# website
This repository holds the utilities and tools I use to build my (new) website.
They are really light-weight and designed to generate very compact and quickly-loading pages

## Directory Structure
It prescribes the following folder structure:

- `tools`: The tools for building the site, included in the project.
- `website`: The website files, not included in the project.
  * `source`: the sources
    + `website.properties`: properties which are resolved in all HTML files via `${propertyName}`
    + `css`: the style sheets
      - `print`: the style sheets for print layout
        * `base`
          + `normalize.css`: a style sheet to normalize the layout of basic elements over browsers 
          + `baseLayout.css`: the theme base layout
        * `extensions`
          + ...`.css`: additional style sheets
      - `screen`: the style sheets for screen layout
        * `base`
          + `normalize.css`: a style sheet to normalize the layout of basic elements over browsers 
          + `baseLayout.css`: the theme base layout
        * `extensions`
          + ...`.css`: additional style sheets
    + `html`: The HTML files.
    + `include`: Files includes into the generated pages
      - `header.html`: the common header
      - `footer.html`: the common footer
      - `menu.html`: the menu
    + `resources`: Same folder structure as `html`, contains all static resources such as images or icons.
      - `icons`: 20pxx20px `png` icons for file types
  * `build`: The generated website


## Functionality

The following functionality is provided by building the website:

1. Both `html` and `css` are rigorously minified.
2. All non-ASCII characters (Chinese, German Umlauts) are translated to HTML entities in both `css` and `html`.
3. Properties from `website.properties` are automatically resolved in all HTML files.
4. `{{url}}` resolves relative and absolute Urls towards the website base URL.
5. `[[url]title]` generates links inside the website. If `url` is not a `html` file, an icon of the corresponding file type (from `resources/icons`) will be added.
6. `<dquote>xxx</dquote>` will resolve to double-quoted `xxx`
7. `<squote>xxx</squote>` will resolve to single-quoted `xxx`

## How to Use
Generate the above file structure.
The run `build.xml` as `Ant` build (requires Ant 1.9.4 and Maven 3.3.3)