package dev.rebelcraft.ai.spawn.mcp;

import dev.rebelcraft.ai.spawn.mcp.templates.McpServerExampleConfiguration;
import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.ACTIVATE_MCP_NAV_LINK;
import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.createPage;
import static j2html.TagCreator.*;

@Component
public class McpServerPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

    McpServerResponse mcpServer = (McpServerResponse) model.get("mcpServer");
    List<McpServerExampleConfiguration> configurationTemplateList = (List<McpServerExampleConfiguration>) model.get("configurationTemplateList");

    return createPage(
      mcpServer.getName() + " - Spawn",
      ACTIVATE_MCP_NAV_LINK,
      each(
        div(
          attrs(".container.mt-4"),
          // Back button
          div(
            attrs(".mb-4"),
            a(attrs(".btn.btn-secondary"), "← Back to MCP Servers").withHref("/mcp-servers")
          ),
          div(
            attrs(".mb-3"),
            h1("MCP Server: " + mcpServer.getName()),
            p(attrs(".text-muted"), mcpServer.getDescription())
          ),

          each(configurationTemplateList, template -> renderTemplateSection(template))
        )
      )
    );
  }

  private DomContent renderTemplateSection(McpServerExampleConfiguration template) {
    return div(
      attrs(".mb-4"),
      h3(template.description()),
      p(attrs(".text-muted"), "Example file: " + template.name()),
      p(attrs(".text-muted"),
        "Password fields are masked with *****, and fields without defaults show placeholder hints."),
      pre(
        attrs(".bg-light.p-3.rounded.border"),
        code(template.configuration())
      )
    );
  }
}
