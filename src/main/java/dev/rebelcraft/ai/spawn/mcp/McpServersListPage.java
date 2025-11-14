package dev.rebelcraft.ai.spawn.mcp;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.*;
import static j2html.TagCreator.*;

@Component
public class McpServersListPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

    @SuppressWarnings("unchecked")
    List<McpServerResponse> servers = (List<McpServerResponse>) model.get("servers");

    // Split into favorites and non-favorites, both sorted alphabetically
    List<McpServerResponse> favorites = servers.stream()
        .filter(McpServerResponse::isFavorite)
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .collect(java.util.stream.Collectors.toList());
    
    List<McpServerResponse> others = servers.stream()
        .filter(s -> !s.isFavorite())
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .collect(java.util.stream.Collectors.toList());

    return createPage(
      "MCP Servers - Spawn",
      ACTIVATE_MCP_NAV_LINK,
      each(
        div(
          attrs(".container.mt-4"),
          div(
            attrs(".mb-3"),
            h1("MCP Servers"),
            p(attrs(".text-muted"), "These MCP servers are loaded from the mcp_servers.csv configuration file.")
          ),
          each(renderServerSections(favorites, others))
        )
      )
    );
  }

  private DomContent[] renderServerSections(List<McpServerResponse> favorites, List<McpServerResponse> others) {
    java.util.List<DomContent> sections = new java.util.ArrayList<>();
    
    if (!favorites.isEmpty()) {
      sections.add(div(
        h3("Favorites"),
        serversTable(favorites)
      ));
    }
    
    if (!others.isEmpty()) {
      ContainerTag heading = favorites.isEmpty() ? h3("All MCP Servers") : h3(attrs(".mt-4"), "All MCP Servers");
      sections.add(div(
        heading,
        serversTable(others)
      ));
    }
    
    if (favorites.isEmpty() && others.isEmpty()) {
      sections.add(div(
        attrs(".alert.alert-info"),
        "No MCP servers found."
      ));
    }
    
    return sections.toArray(new DomContent[0]);
  }

  private ContainerTag serversTable(List<McpServerResponse> servers) {
    if (servers == null || servers.isEmpty()) {
      return div(
        attrs(".alert.alert-info"),
        "No MCP servers found."
      );
    }

    return div(
      attrs(".table-responsive"),
      table(
        attrs(".table.table-striped.table-hover"),
        thead(
          tr(
            th("Name"),
            th("Icon"),
            th("Description"),
            th("Actions")
          )
        ),
        tbody(
          each(servers, server -> tr(
            td(server.getName()),
            td(
              img(attrs(".rounded-circle"))
                .withSrc(server.getIcon())
                .withAlt(server.getName() + " logo")
                .withStyle("width: 32px; height: 32px;")
            ),
            td(server.getDescription()),
            td(
              server.isFavorite() ?
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-outline-warning"))
                    .withType("submit")
                    .attr("formaction", "/mcp-servers/" + server.getName() + "/unfavorite")
                    .attr("formmethod", "post")
                    .withText("★ Unfavorite")
                ) :
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-outline-primary"))
                    .withType("submit")
                    .attr("formaction", "/mcp-servers/" + server.getName() + "/favorite")
                    .attr("formmethod", "post")
                    .withText("☆ Favorite")
                )
            )
          ))
        )
      )
    );
  }

}
