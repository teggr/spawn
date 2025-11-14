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
        modelsSectionReadOnly(app),
        h3(attrs(".mt-4"), "Associated Agents"),
        agentsSectionReadOnly(app),
        h3(attrs(".mt-4"), "Associated MCP Servers"),
        mcpServersSectionReadOnly(app)
      )
    );
  }

  private ContainerTag<?> modelsSectionReadOnly(ApplicationResponse app) {
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
              th("Streaming")
            )
          ),
          tbody(
            each(currentModels, m -> tr(
              td(m.getProvider()),
              td(m.getMultimodality()),
              td(m.getToolsFunctions()),
              td(m.getStreaming())
            ))
          )
        ) :
        div(attrs(".alert.alert-info"), "No models associated with this application.")
    );
  }

  private ContainerTag<?> agentsSectionReadOnly(ApplicationResponse app) {
    Set<AgentResponse> currentAgents = app.getAgents();

    return div(
      currentAgents != null && !currentAgents.isEmpty() ?
        table(
          attrs(".table.table-striped.mb-4"),
          thead(
            tr(
              th("Name"),
              th("Description")
            )
          ),
          tbody(
            each(currentAgents, agent -> tr(
              td(agent.getName()),
              td(agent.getDescription() != null ? agent.getDescription() : "")
            ))
          )
        ) :
        div(attrs(".alert.alert-info"), "No agents associated with this application.")
    );
  }

  private ContainerTag<?> mcpServersSectionReadOnly(ApplicationResponse app) {
    Set<McpServerResponse> currentServers = app.getMcpServers();

    return div(
      currentServers != null && !currentServers.isEmpty() ?
        table(
          attrs(".table.table-striped.mb-4"),
          thead(
            tr(
              th("Name"),
              th("Icon"),
              th("Description")
            )
          ),
          tbody(
            each(currentServers, server -> tr(
              td(server.getName()),
              td(
                img(attrs(".rounded-circle")).withSrc(server.getIcon()).withAlt(server.getName() + " logo").withStyle("width: 32px; height: 32px;")
              ),
              td(server.getDescription() != null ? server.getDescription() : "")
            ))
          )
        ) :
        div(attrs(".alert.alert-info"), "No MCP servers associated with this application.")
    );
  }

}
