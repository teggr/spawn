package dev.rebelcraft.ai.spawn.agents;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

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

                    // Top action bar: title + Edit / Back buttons
                    div(attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
                        h1(agent != null ? agent.getName() : ""),
                        div(
                            a(attrs(".btn.btn-primary.me-2"), "Edit").withHref("/agents/" + (agent != null ? agent.getId() : "") + "/edit"),
                            a(attrs(".btn.btn-secondary"), "Back").withHref("/agents")
                        )
                    ),

                    // Description and system prompt
                    p(agent != null && agent.getDescription() != null ? agent.getDescription() : ""),

                    div(attrs(".mb-3"),
                        h5("System Prompt"),
                        pre(agent != null && agent.getSystemPrompt() != null ? agent.getSystemPrompt() : ""),
                        hr()
                    ),

                    // MCP Servers - read-only list (no add/remove controls)
                    div(attrs(".mb-3"),
                        h5("MCP Servers"),
                        agent != null && agent.getMcpServerNames() != null && !agent.getMcpServerNames().isEmpty() ?
                            each(agent.getMcpServerNames(), name ->
                                div(attrs(".d-flex.align-items-center.justify-content-between.mb-2"),
                                    span(name),
                                    agent.getUnmatchedMcpNames() != null && agent.getUnmatchedMcpNames().contains(name) ?
                                        span(attrs(".badge.bg-warning.text-dark"), "Unknown MCP") : text("")
                                )
                            ) :
                            div(attrs(".alert.alert-info"), "No MCP servers attached to this agent.")
                    )

                )
            )
        );
    }
}
