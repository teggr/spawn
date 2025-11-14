package dev.rebelcraft.ai.spawn.web.view;

import j2html.tags.DomContent;
import j2html.tags.specialized.NavTag;

import static j2html.TagCreator.*;

public class DefaultPageLayout {

  public static final String NO_ACTIVE_NAV_LINK = "";
  public static final String ACTIVATE_MODELS_NAV_LINK = "models";
  public static final String ACTIVATE_MCP_NAV_LINK = "mcp";
  public static final String ACTIVATE_APPS_NAV_LINK = "apps";

  public static DomContent createPage(String title, String activeNavLink, DomContent bodyContent) {
    return html(
      head(
        meta().attr("charset", "utf-8"),
        meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
        title(title),
        link().withRel("stylesheet")
          .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
      ),
      body(
        navbar(activeNavLink),
        div(
          attrs(".container.mt-4"),
          // insert body content here
          bodyContent
        ),
        script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js")
      )
    );
  }

  private static NavTag navbar(String activeNavLink) {
    return nav(
      attrs(".navbar.navbar-expand-lg.navbar-dark.bg-dark"),
      div(
        attrs(".container-fluid"),
        a(attrs(".navbar-brand"), "Spawn").withHref("/"),
        div(
          attrs(".navbar-nav"),
          a(attrs(ACTIVATE_MODELS_NAV_LINK.equals(activeNavLink) ? ".nav-link.active" : ".nav-link"), "Models").withHref("/models"),
          a(attrs(ACTIVATE_MCP_NAV_LINK.equals(activeNavLink) ? ".nav-link.active" : ".nav-link"), "MCP Servers").withHref("/mcp-servers"),
          a(attrs(ACTIVATE_APPS_NAV_LINK.equals(activeNavLink) ? ".nav-link.active" : ".nav-link"), "Applications").withHref("/applications")
        )
      )
    );
  }

}
