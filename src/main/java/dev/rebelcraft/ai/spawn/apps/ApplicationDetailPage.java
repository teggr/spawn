package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout;
import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static j2html.TagCreator.*;

@Component
public class ApplicationDetailPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

    ApplicationResponse app = (ApplicationResponse) model.get("application");
    @SuppressWarnings("unchecked")
    List<McpServerResponse> availableServers = (List<McpServerResponse>) model.get("availableServers");

    return DefaultPageLayout.createPage(
      "Application Details - Spawn",
      DefaultPageLayout.ACTIVATE_APPS_NAV_LINK,
      each(
        div(
          attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
          h1("Application: " + app.getName()),
          div(
            a(attrs(".btn.btn-primary.me-2"), "Edit")
              .withHref("/applications/" + app.getId() + "/edit"),
            a(attrs(".btn.btn-secondary"), "Back to List")
              .withHref("/applications")
          )
        ),
        div(
          attrs(".card.mb-4"),
          div(
            attrs(".card-body"),
            h5(attrs(".card-title"), "Details"),
            p(strong("ID: "), text(app.getId().toString())),
            p(strong("Name: "), text(app.getName())),
            p(strong("Model Provider: "), text(app.getModel() != null ?
              app.getModel().getProvider() : "None")),
            p(strong("Created At: "), text(app.getCreatedAt() != null ? app.getCreatedAt().toString() : ""))
          )
        ),
        h3("Associated MCP Servers"),
        mcpServersSection(app, availableServers)
      )
    );
  }

  private ContainerTag mcpServersSection(ApplicationResponse app, List<McpServerResponse> availableServers) {
    Set<McpServerResponse> currentServers = app.getMcpServers();

    return div(
      currentServers != null && !currentServers.isEmpty() ?
        table(
          attrs(".table.table-striped.mb-4"),
          thead(
            tr(
              th("Name"),
              th("Icon"),
              th("Description"),
              th("Actions")
            )
          ),
          tbody(
            each(currentServers, server -> tr(
              td(server.getName()),
              td(
                img(attrs(".rounded-circle"))
                  .withSrc(server.getIcon())
                  .withAlt(server.getName() + " logo")
                  .withStyle("width: 32px; height: 32px;")
              ),
              td(server.getDescription() != null ? server.getDescription() : ""),
              td(
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-danger"), "Remove")
                    .attr("type", "submit")
                    .attr("onclick", "return confirm('Are you sure you want to remove this MCP server?')")
                ).attr("method", "post")
                  .attr("action", "/applications/" + app.getId() + "/mcp-servers/" + server.getName() + "/remove")
              )
            ))
          )
        ) :
        div(attrs(".alert.alert-info"), "No MCP servers associated with this application."),

      h4(attrs(".mt-4"), "Add MCP Server"),
      availableServers != null && !availableServers.isEmpty() ?
        form(
          attrs(".row.g-3.align-items-end"),
          div(
            attrs(".col-auto"),
            label(attrs(".form-label"), "Select MCP Server").attr("for", "mcpServerName"),
            select(attrs(".form-select"))
              .attr("id", "mcpServerName")
              .attr("name", "mcpServerName")
              .attr("required", "required")
              .with(
                option("Choose...").attr("value", ""),
                each(availableServers, server ->
                  option(server.getName() + " - " + server.getDescription())
                    .attr("value", server.getName())
                )
              )
          ),
          div(
            attrs(".col-auto"),
            button(attrs(".btn.btn-primary"), "Add Server")
              .attr("type", "submit")
          )
        ).attr("method", "post")
          .attr("action", "/applications/" + app.getId() + "/mcp-servers/add") :
        div(attrs(".alert.alert-warning"), "No MCP servers available to add.")
    );
  }

}
