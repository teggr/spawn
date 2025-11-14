package dev.rebelcraft.ai.spawn.web.view;

import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class DefaultPageLayout {

  public static DomContent createPage(String title, DomContent bodyContent) {
    return html(
      head(
        meta().attr("charset", "utf-8"),
        meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
        title(title),
        link().withRel("stylesheet")
          .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
      ),
      body(
        nav(
          attrs(".navbar.navbar-expand-lg.navbar-dark.bg-dark"),
          div(
            attrs(".container-fluid"),
            a(attrs(".navbar-brand"), "Spawn").withHref("/"),
            div(
              attrs(".navbar-nav"),
              a(attrs(".nav-link"), "Models").withHref("/models"),
              a(attrs(".nav-link"), "MCP Servers").withHref("/mcp-servers"),
              a(attrs(".nav-link"), "Applications").withHref("/applications")
            )
          )
        ),
        div(
          attrs(".container.mt-4"),
          // insert body content here
          bodyContent
        ),
        script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js")
      )
    );
  }

}
