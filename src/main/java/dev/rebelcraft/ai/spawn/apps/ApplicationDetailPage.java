package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
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
    List<AgentResponse> availableAgents = (List<AgentResponse>) model.get("availableAgents");

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
        h3("Associated Agents"),
        agentsSection(app, availableAgents)
      )
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
              th("Model"),
              th("MCP Servers"),
              th("Actions")
            )
          ),
          tbody(
            each(currentAgents, agent -> tr(
              td(agent.getName()),
              td(agent.getDescription() != null ? agent.getDescription() : ""),
              td(agent.getModelProvider() != null ? agent.getModelProvider() : "None"),
              td(agent.getMcpServerNames() != null ? String.valueOf(agent.getMcpServerNames().size()) : "0"),
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
                each(availableAgents, agent ->
                  option(agent.getName() + " - " + (agent.getDescription() != null ? agent.getDescription() : ""))
                    .attr("value", agent.getName())
                )
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

}
