package dev.rebelcraft.ai.spawn.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.util.Map;

import static j2html.TagCreator.*;

@Component
public class IndexPage implements View {

  @Override
  public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

    DefaultPageLayout.createPage(
      "Spawn - AI Application Builder",
      each(
        h1("Welcome to Spawn"),
        p("Application for building AI capable applications on the fly with UI, models and MCP servers."),
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
                a(attrs(".btn.btn-primary"), "Manage Models").withHref("/models")
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
                a(attrs(".btn.btn-primary"), "Manage MCP Servers").withHref("/mcp-servers")
              )
            )
          ),
          div(
            attrs(".col-md-4"),
            div(
              attrs(".card"),
              div(
                attrs(".card-body"),
                h5(attrs(".card-title"), "Applications"),
                p(attrs(".card-text"), "Build and deploy AI applications."),
                a(attrs(".btn.btn-primary"), "Manage Applications").withHref("/applications")
              )
            )
          )
        )
      )
    ).render(response.getWriter());

  }

  @Override
  public String getContentType() {
    return MediaType.TEXT_HTML_VALUE;
  }
}
