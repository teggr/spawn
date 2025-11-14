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
import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.ACTIVATE_MCP_NAV_LINK;
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
            attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
            h1("MCP Servers"),
            a(attrs(".btn.btn-primary"), "Create New MCP Server").withHref("/mcp-servers/new")
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
        "No MCP servers found. Create your first MCP server to get started!"
      );
    }

    return table(
      attrs(".table.table-striped"),
      thead(
        tr(
          th("ID"),
          th("Name"),
          th("URL"),
          th("Description"),
          th("Actions")
        )
      ),
      tbody(
        each(servers, server -> tr(
          td(server.getId().toString()),
          td(server.getName()),
          td(server.getUrl()),
          td(server.getDescription() != null ? server.getDescription() : ""),
          td(
            a(attrs(".btn.btn-sm.btn-primary.me-2"), "Edit")
              .withHref("/mcp-servers/" + server.getId() + "/edit"),
            form(
              attrs(".d-inline"),
              button(attrs(".btn.btn-sm.btn-danger"), "Delete")
                .attr("type", "submit")
                .attr("onclick", "return confirm('Are you sure you want to delete this MCP server?')")
            ).attr("method", "post")
              .attr("action", "/mcp-servers/" + server.getId() + "/delete")
          )
        ))
      )
    );
  }

}
