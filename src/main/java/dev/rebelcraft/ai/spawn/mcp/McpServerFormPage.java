package dev.rebelcraft.ai.spawn.mcp;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.*;
import static j2html.TagCreator.*;

@Component
public class McpServerFormPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

    String serverId = (String) model.get("serverId");
    String name = (String) model.get("name");
    String url = (String) model.get("url");
    String description = (String) model.get("description");
    String error = (String) model.get("error");

    boolean isEdit = serverId != null;

    return createPage(
      (isEdit ? "Edit MCP Server" : "Create MCP Server") + " - Spawn",
      ACTIVATE_MCP_NAV_LINK,
      each(
        div(
          attrs(".container.mt-4"),
          h1(isEdit ? "Edit MCP Server" : "Create New MCP Server"),
          error != null ? div(attrs(".alert.alert-danger"), error) : text(""),
          form(
            attrs(".mt-3"),
            div(
              attrs(".mb-3"),
              label(attrs(".form-label"), "Name").attr("for", "name"),
              input(attrs(".form-control"))
                .attr("type", "text")
                .attr("id", "name")
                .attr("name", "name")
                .attr("required", "required")
                .condAttr(name != null, "value", name != null ? name : "")
            ),
            div(
              attrs(".mb-3"),
              label(attrs(".form-label"), "URL").attr("for", "url"),
              input(attrs(".form-control"))
                .attr("type", "text")
                .attr("id", "url")
                .attr("name", "url")
                .attr("required", "required")
                .attr("placeholder", "e.g., http://localhost:8080/mcp/filesystem")
                .condAttr(url != null, "value", url != null ? url : "")
            ),
            div(
              attrs(".mb-3"),
              label(attrs(".form-label"), "Description").attr("for", "description"),
              textarea(attrs(".form-control"))
                .attr("id", "description")
                .attr("name", "description")
                .attr("rows", "3")
                .withText(description != null ? description : "")
            ),
            div(
              attrs(".mt-3"),
              button(attrs(".btn.btn-primary.me-2"), "Save")
                .attr("type", "submit"),
              a(attrs(".btn.btn-secondary"), "Cancel").withHref("/mcp-servers")
            )
          ).attr("method", "post")
            .attr("action", isEdit ? "/mcp-servers/" + serverId : "/mcp-servers")
        )
      )
    );
  }

}
