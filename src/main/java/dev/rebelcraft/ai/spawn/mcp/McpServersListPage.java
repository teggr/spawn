package dev.rebelcraft.ai.spawn.mcp;

import j2html.tags.ContainerTag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

@Component
public class McpServersListPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<McpServerResponse> servers = (List<McpServerResponse>) model.get("servers");

        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title("MCP Servers - Spawn"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                navbar(),
                div(
                    attrs(".container.mt-4"),
                    div(
                        attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
                        h1("MCP Servers"),
                        a(attrs(".btn.btn-primary"), "Create New MCP Server").withHref("/mcp-servers/new")
                    ),
                    serversTable(servers)
                ),
                script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js")
            )
        ).render(response.getWriter());
    }

    private ContainerTag navbar() {
        return nav(
            attrs(".navbar.navbar-expand-lg.navbar-dark.bg-dark"),
            div(
                attrs(".container-fluid"),
                a(attrs(".navbar-brand"), "Spawn").withHref("/"),
                div(
                    attrs(".navbar-nav"),
                    a(attrs(".nav-link"), "Models").withHref("/models"),
                    a(attrs(".nav-link.active"), "MCP Servers").withHref("/mcp-servers"),
                    a(attrs(".nav-link"), "Applications").withHref("/applications")
                )
            )
        );
    }

    private ContainerTag serversTable(List<McpServerResponse> servers) {
        if (servers == null || servers.isEmpty()) {
            return div(
                attrs(".alert.alert-info"),
                "No MCP servers found. Create your first MCP server to get started!"
            );
        }

        return table(
            attrs(".table.table-striped"),
            thead(
                tr(
                    th("ID"),
                    th("Name"),
                    th("URL"),
                    th("Description"),
                    th("Actions")
                )
            ),
            tbody(
                each(servers, server -> tr(
                    td(server.getId().toString()),
                    td(server.getName()),
                    td(server.getUrl()),
                    td(server.getDescription() != null ? server.getDescription() : ""),
                    td(
                        a(attrs(".btn.btn-sm.btn-primary.me-2"), "Edit")
                            .withHref("/mcp-servers/" + server.getId() + "/edit"),
                        form(
                            attrs(".d-inline"),
                            button(attrs(".btn.btn-sm.btn-danger"), "Delete")
                                .attr("type", "submit")
                                .attr("onclick", "return confirm('Are you sure you want to delete this MCP server?')")
                        ).attr("method", "post")
                         .attr("action", "/mcp-servers/" + server.getId() + "/delete")
                    )
                ))
            )
        );
    }

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }
}
