package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
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
    List<ModelResponse> availableModels = (List<ModelResponse>) model.get("availableModels");
    @SuppressWarnings("unchecked")
    List<AgentResponse> availableAgents = (List<AgentResponse>) model.get("availableAgents");
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
            p(strong("Created At: "), text(app.getCreatedAt() != null ? app.getCreatedAt().toString() : ""))
          )
        ),
        h3("Associated Models"),
        modelsSection(app, availableModels),
        h3(attrs(".mt-4"), "Associated Agents"),
        agentsSection(app, availableAgents),
        h3(attrs(".mt-4"), "Associated MCP Servers"),
        mcpServersSection(app, availableServers)
      )
    );
  }

  private ContainerTag modelsSection(ApplicationResponse app, List<ModelResponse> availableModels) {
    Set<ModelResponse> currentModels = app.getModels();

    return div(
      currentModels != null && !currentModels.isEmpty() ?
        table(
          attrs(".table.table-striped.mb-4"),
          thead(
            tr(
              th("Provider"),
              th("Multimodality"),
              th("Tools/Functions"),
              th("Streaming"),
              th("Actions")
            )
          ),
          tbody(
            each(currentModels, m -> tr(
              td(m.getProvider()),
              td(m.getMultimodality()),
              td(m.getToolsFunctions()),
              td(m.getStreaming()),
              td(
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-danger"), "Remove")
                    .attr("type", "submit")
                    .attr("onclick", "return confirm('Are you sure you want to remove this model?')")
                ).attr("method", "post")
                  .attr("action", "/applications/" + app.getId() + "/models/" + m.getProvider() + "/remove")
              )
            ))
          )
        ) :
        div(attrs(".alert.alert-info"), "No models associated with this application."),

      h4(attrs(".mt-4"), "Add Model"),
      availableModels != null && !availableModels.isEmpty() ?
        form(
          attrs(".row.g-3.align-items-end"),
          div(
            attrs(".col-auto"),
            label(attrs(".form-label"), "Select Model").attr("for", "modelProvider"),
            select(attrs(".form-select"))
              .attr("id", "modelProvider")
              .attr("name", "modelProvider")
              .attr("required", "required")
              .with(
                option("Choose...").attr("value", ""),
                each(renderAvailableModelOptions(availableModels))
              )
          ),
          div(
            attrs(".col-auto"),
            button(attrs(".btn.btn-primary"), "Add Model")
              .attr("type", "submit")
          )
        ).attr("method", "post")
          .attr("action", "/applications/" + app.getId() + "/models/add") :
        div(attrs(".alert.alert-warning"), "No models available to add.")
    );
  }

  private ContainerTag agentsSection(ApplicationResponse app, List<AgentResponse> availableAgents) {
    Set<AgentResponse> currentAgents = app.getAgents();

    return div(
      currentAgents != null && !currentAgents.isEmpty() ?
        table(
          attrs(".table.table-striped.mb-4"),
          thead(
            tr(
              th("Name"),
              th("Description"),
              th("Actions")
            )
          ),
          tbody(
            each(currentAgents, agent -> tr(
              td(agent.getName()),
              td(agent.getDescription() != null ? agent.getDescription() : ""),
              td(
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-danger"), "Remove")
                    .attr("type", "submit")
                    .attr("onclick", "return confirm('Are you sure you want to remove this agent?')")
                ).attr("method", "post")
                  .attr("action", "/applications/" + app.getId() + "/agents/" + agent.getName() + "/remove")
              )
            ))
          )
        ) :
        div(attrs(".alert.alert-info"), "No agents associated with this application."),

      h4(attrs(".mt-4"), "Add Agent"),
      availableAgents != null && !availableAgents.isEmpty() ?
        form(
          attrs(".row.g-3.align-items-end"),
          div(
            attrs(".col-auto"),
            label(attrs(".form-label"), "Select Agent").attr("for", "agentName"),
            select(attrs(".form-select"))
              .attr("id", "agentName")
              .attr("name", "agentName")
              .attr("required", "required")
              .with(
                option("Choose...").attr("value", ""),
                each(renderAvailableAgentOptions(availableAgents))
              )
          ),
          div(
            attrs(".col-auto"),
            button(attrs(".btn.btn-primary"), "Add Agent")
              .attr("type", "submit")
          )
        ).attr("method", "post")
          .attr("action", "/applications/" + app.getId() + "/agents/add") :
        div(attrs(".alert.alert-warning"), "No agents available to add.")
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
                each(renderAvailableServerOptions(availableServers))
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

  private DomContent[] renderAvailableModelOptions(List<ModelResponse> models) {
    // Split into favorites and non-favorites, both sorted alphabetically
    List<ModelResponse> favorites = models.stream()
        .filter(ModelResponse::isFavorite)
        .sorted((a, b) -> a.getProvider().compareToIgnoreCase(b.getProvider()))
        .collect(java.util.stream.Collectors.toList());
    
    List<ModelResponse> others = models.stream()
        .filter(m -> !m.isFavorite())
        .sorted((a, b) -> a.getProvider().compareToIgnoreCase(b.getProvider()))
        .collect(java.util.stream.Collectors.toList());
    
    java.util.List<DomContent> options = new java.util.ArrayList<>();
    
    if (!favorites.isEmpty()) {
      options.add(optgroup().attr("label", "Favorites")
        .with(each(favorites, m ->
          option(m.getProvider())
            .attr("value", m.getProvider())
        ))
      );
    }
    
    if (!others.isEmpty()) {
      options.add(optgroup().attr("label", favorites.isEmpty() ? "All Models" : "All Others")
        .with(each(others, m ->
          option(m.getProvider())
            .attr("value", m.getProvider())
        ))
      );
    }
    
    return options.toArray(new DomContent[0]);
  }

  private DomContent[] renderAvailableAgentOptions(List<AgentResponse> agents) {
    return agents.stream()
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .map(agent -> option(agent.getName() + " - " + (agent.getDescription() != null ? agent.getDescription() : ""))
            .attr("value", agent.getName()))
        .toArray(DomContent[]::new);
  }

  private DomContent[] renderAvailableServerOptions(List<McpServerResponse> servers) {
    // Split into favorites and non-favorites, both sorted alphabetically
    List<McpServerResponse> favorites = servers.stream()
        .filter(McpServerResponse::isFavorite)
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .collect(java.util.stream.Collectors.toList());
    
    List<McpServerResponse> others = servers.stream()
        .filter(s -> !s.isFavorite())
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .collect(java.util.stream.Collectors.toList());
    
    java.util.List<DomContent> options = new java.util.ArrayList<>();
    
    if (!favorites.isEmpty()) {
      options.add(optgroup().attr("label", "Favorites")
        .with(each(favorites, server ->
          option(server.getName() + " - " + server.getDescription())
            .attr("value", server.getName())
        ))
      );
    }
    
    if (!others.isEmpty()) {
      options.add(optgroup().attr("label", favorites.isEmpty() ? "All MCP Servers" : "All Others")
        .with(each(others, server ->
          option(server.getName() + " - " + server.getDescription())
            .attr("value", server.getName())
        ))
      );
    }
    
    return options.toArray(new DomContent[0]);
  }

}
