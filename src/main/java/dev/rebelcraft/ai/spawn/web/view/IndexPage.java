package dev.rebelcraft.ai.spawn.web.view;

import dev.rebelcraft.ai.spawn.apps.ApplicationResponse;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

@Component
public class IndexPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

    @SuppressWarnings("unchecked")
    List<ApplicationResponse> recentApplications = (List<ApplicationResponse>) model.get("recentApplications");

    return DefaultPageLayout.createPage(
      "Spawn - AI Application Builder",
      DefaultPageLayout.NO_ACTIVE_NAV_LINK,
      each(
        h1("Welcome to Spawn"),
        p("Application for building AI capable applications on the fly with UI, models and MCP servers."),

        // Top row: Models, MCP Servers, Agents
        div(
          attrs(".row.mt-4"),
          div(
            attrs(".col-md-4"),
            div(
              attrs(".card"),
              div(
                attrs(".card-body"),
                h5(attrs(".card-title"), "Models"),
                p(attrs(".card-text"), "Manage AI models like GPT-4, Claude, etc."),
                a(attrs(".btn.btn-primary"), "Browse Models").withHref("/models")
              )
            )
          ),
          div(
            attrs(".col-md-4"),
            div(
              attrs(".card"),
              div(
                attrs(".card-body"),
                h5(attrs(".card-title"), "MCP Servers"),
                p(attrs(".card-text"), "Configure MCP servers for your applications."),
                a(attrs(".btn.btn-primary"), "Browse MCP Servers").withHref("/mcp-servers")
              )
            )
          ),
          div(
            attrs(".col-md-4"),
            div(
              attrs(".card"),
              div(
                attrs(".card-body"),
                h5(attrs(".card-title"), "Agents"),
                p(attrs(".card-text"), "Create and manage expert agents for specific tasks."),
                a(attrs(".btn.btn-primary"), "Manage Agents").withHref("/agents")
              )
            )
          )
        ),

        // Second row: Applications card with recent apps
        div(
          attrs(".row.mt-4"),
          div(
            attrs(".col-12"),
            div(
              attrs(".card"),
              div(
                attrs(".card-body"),
                h5(attrs(".card-title"), "Applications"),
                p(attrs(".card-text"), "Build and deploy AI applications."),

                // Recent applications list: show name + created date + link to detail
                recentApplications != null && !recentApplications.isEmpty() ?
                  ul(each(recentApplications, app -> li(
                      a(app.getName()).withHref("/applications/" + app.getId()),
                      span(" "),
                      small(app.getCreatedAt() != null ? app.getCreatedAt().toString() : "")
                  ))) :
                  div(attrs(".alert.alert-info"), "No recent applications"),

                a(attrs(".btn.btn-primary.mt-3"), "Manage Applications").withHref("/applications")
              )
            )
          )
        )

      )
    );

  }

}
