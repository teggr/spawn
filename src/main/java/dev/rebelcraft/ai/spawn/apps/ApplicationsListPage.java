package dev.rebelcraft.ai.spawn.apps;

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
public class ApplicationsListPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

      @SuppressWarnings("unchecked")
    List<ApplicationResponse> applications = (List<ApplicationResponse>) model.get("applications");

    return createPage(
      "Applications - Spawn",
      ACTIVATE_APPS_NAV_LINK,
      each(
        div(
          attrs(".container.mt-4"),
          div(
            attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
            h1("Applications"),
            a(attrs(".btn.btn-primary"), "Create New Application").withHref("/applications/new")
          ),
          applicationsTable(applications)
        )
      )
    );
  }

  private ContainerTag applicationsTable(List<ApplicationResponse> applications) {
    if (applications == null || applications.isEmpty()) {
      return div(
        attrs(".alert.alert-info"),
        "No applications found. Create your first application to get started!"
      );
    }

    return table(
      attrs(".table.table-striped"),
      thead(
        tr(
          th("ID"),
          th("Name"),
          th("Models"),
          th("Agents"),
          th("Created At"),
          th("Actions")
        )
      ),
      tbody(
        each(applications, app -> tr(
          td(app.getId().toString()),
          td(app.getName()),
          td(app.getModels() != null ? String.valueOf(app.getModels().size()) : "0"),
          td(app.getAgents() != null ? String.valueOf(app.getAgents().size()) : "0"),
          td(app.getCreatedAt() != null ? app.getCreatedAt().toString() : ""),
          td(
            a(attrs(".btn.btn-sm.btn-info.me-2"), "View")
              .withHref("/applications/" + app.getId()),
            a(attrs(".btn.btn-sm.btn-primary.me-2"), "Edit")
              .withHref("/applications/" + app.getId() + "/edit"),
            form(
              attrs(".d-inline"),
              button(attrs(".btn.btn-sm.btn-danger"), "Delete")
                .attr("type", "submit")
                .attr("onclick", "return confirm('Are you sure you want to delete this application?')")
            ).attr("method", "post")
              .attr("action", "/applications/" + app.getId() + "/delete")
          )
        ))
      )
    );
  }

}
