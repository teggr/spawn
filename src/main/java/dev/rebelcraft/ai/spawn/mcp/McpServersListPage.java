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
          serversTable(servers)
        )
      )
    );
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
            th("Description")
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
            td(server.getDescription())
          ))
        )
      )
    );
  }

}
