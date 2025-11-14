package dev.rebelcraft.ai.spawn.agents;

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
public class AgentsListPage extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

        @SuppressWarnings("unchecked")
        List<AgentResponse> agents = (List<AgentResponse>) model.get("agents");

        return createPage(
            "Agents - Spawn",
            ACTIVATE_AGENTS_NAV_LINK,
            each(
                div(
                    attrs(".container.mt-4"),
                    div(
                        attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
                        h1("Agents"),
                        a(attrs(".btn.btn-primary"), "Create New Agent").withHref("/agents/new")
                    ),
                    agentsTable(agents)
                )
            )
        );
    }

    private ContainerTag agentsTable(List<AgentResponse> agents) {
        if (agents == null || agents.isEmpty()) {
            return div(
                attrs(".alert.alert-info"),
                "No agents found. Create your first agent to get started!"
            );
        }

        return table(
            attrs(".table.table-striped"),
            thead(
                tr(
                    th("ID"),
                    th("Name"),
                    th("Description"),
                    th("MCP Servers"),
                    th("Created At"),
                    th("Actions")
                )
            ),
            tbody(
                each(agents, agent -> tr(
                    td(agent.getId().toString()),
                    td(agent.getName()),
                    td(agent.getDescription() != null ? agent.getDescription() : ""),
                    td(agent.getMcpServerNames() != null ? String.valueOf(agent.getMcpServerNames().size()) : "0"),
                    td(agent.getCreatedAt() != null ? agent.getCreatedAt().toString() : ""),
                    td(
                        a(attrs(".btn.btn-sm.btn-info.me-2"), "View").withHref("/agents/" + agent.getId()),
                        a(attrs(".btn.btn-sm.btn-primary.me-2"), "Edit").withHref("/agents/" + agent.getId() + "/edit"),
                        form(attrs(".d-inline"),
                            button(attrs(".btn.btn-sm.btn-danger"), "Delete")
                                .attr("type", "submit")
                                .attr("onclick", "return confirm('Are you sure you want to delete this agent?')")
                        ).attr("method", "post").attr("action", "/agents/" + agent.getId() + "/delete")
                    )
                ))
            )
        );
    }
}
