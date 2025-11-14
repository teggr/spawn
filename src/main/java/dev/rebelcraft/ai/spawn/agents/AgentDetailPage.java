package dev.rebelcraft.ai.spawn.agents;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.*;
import static j2html.TagCreator.*;

@Component
public class AgentDetailPage extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

        AgentResponse agent = (AgentResponse) model.get("agent");

        return createPage(
            "Agent - " + (agent != null ? agent.getName() : "") + " - Spawn",
            ACTIVATE_APPS_NAV_LINK,
            each(
                div(attrs(".container.mt-4"),
                    h1(agent != null ? agent.getName() : ""),
                    p(agent != null && agent.getDescription() != null ? agent.getDescription() : ""),

                    div(attrs(".mb-3"),
                        h5("System Prompt"),
                        pre(agent != null && agent.getSystemPrompt() != null ? agent.getSystemPrompt() : ""),
                        hr()
                    ),

                    div(attrs(".mb-3"),
                        h5("MCP Servers"),
                        agent != null && agent.getMcpServerNames() != null && !agent.getMcpServerNames().isEmpty() ?
                            each(agent.getMcpServerNames(), name ->
                                div(attrs(".d-flex.align-items-center.justify-content-between.mb-2"),
                                    span(name),
                                    agent.getUnmatchedMcpNames() != null && agent.getUnmatchedMcpNames().contains(name) ?
                                        span(attrs(".badge.bg-warning.text-dark"), "Unknown MCP") : text(""),
                                    form(attrs(".d-inline"), button(attrs(".btn.btn-sm.btn-outline-danger"), "Remove").attr("type", "submit")).attr("method", "post").attr("action", "/agents/" + agent.getId() + "/mcp-servers/" + name + "/remove")
                                )
                            ) :
                            div(attrs(".alert.alert-info"), "No MCP servers attached to this agent.")
                    ),

                    div(attrs(".mb-3"),
                        form(attrs(".d-flex.gap-2"),
                            select(attrs(".form-select"))
                                .attr("name", "mcpName")
                                .with(
                                    option("Select a server...").attr("value", ""),
                                    each(((List<McpServerResponse>) model.get("mcpServers")), s -> option(s.getName()).attr("value", s.getName()))
                                ),
                            button(attrs(".btn.btn-secondary"), "Add").attr("type", "submit")
                        ).attr("method", "post").attr("action", "/agents/" + (agent != null ? agent.getId() : "") + "/mcp-servers/add"),

                        p(attrs(".text-muted.mt-2"), "You can also add a custom MCP name in the agent edit page.")
                    ),

                    div(attrs(".mt-3"),
                        a(attrs(".btn.btn-secondary"), "Back").withHref("/agents")
                    )
                )
            )
        );
    }
}
