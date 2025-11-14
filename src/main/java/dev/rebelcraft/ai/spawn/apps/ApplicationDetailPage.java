package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import j2html.tags.ContainerTag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static j2html.TagCreator.*;

@Component
public class ApplicationDetailPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ApplicationResponse app = (ApplicationResponse) model.get("application");
        @SuppressWarnings("unchecked")
        List<McpServerResponse> availableServers = (List<McpServerResponse>) model.get("availableServers");

        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title("Application Details - Spawn"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                navbar(),
                div(
                    attrs(".container.mt-4"),
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
                            p(strong("Model Provider: "), text(app.getModel() != null ? 
                                app.getModel().getProvider() : "None")),
                            p(strong("Created At: "), text(app.getCreatedAt() != null ? app.getCreatedAt().toString() : ""))
                        )
                    ),
                    h3("Associated MCP Servers"),
                    mcpServersSection(app, availableServers)
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
                    a(attrs(".nav-link"), "MCP Servers").withHref("/mcp-servers"),
                    a(attrs(".nav-link.active"), "Applications").withHref("/applications")
                )
            )
        );
    }

    private ContainerTag mcpServersSection(ApplicationResponse app, List<McpServerResponse> availableServers) {
        Set<McpServerResponse> currentServers = app.getMcpServers();
        
        return div(
            currentServers != null && !currentServers.isEmpty() ? 
                table(
                    attrs(".table.table-striped.mb-4"),
                    thead(
                        tr(
                            th("Name"),
                            th("Icon"),
                            th("Description"),
                            th("Actions")
                        )
                    ),
                    tbody(
                        each(currentServers, server -> tr(
                            td(server.getName()),
                            td(
                                img(attrs(".rounded-circle"))
                                    .withSrc(server.getIcon())
                                    .withAlt(server.getName() + " logo")
                                    .withStyle("width: 32px; height: 32px;")
                            ),
                            td(server.getDescription() != null ? server.getDescription() : ""),
                            td(
                                form(
                                    attrs(".d-inline"),
                                    button(attrs(".btn.btn-sm.btn-danger"), "Remove")
                                        .attr("type", "submit")
                                        .attr("onclick", "return confirm('Are you sure you want to remove this MCP server?')")
                                ).attr("method", "post")
                                 .attr("action", "/applications/" + app.getId() + "/mcp-servers/" + server.getName() + "/remove")
                            )
                        ))
                    )
                ) : 
                div(attrs(".alert.alert-info"), "No MCP servers associated with this application."),
            
            h4(attrs(".mt-4"), "Add MCP Server"),
            availableServers != null && !availableServers.isEmpty() ?
                form(
                    attrs(".row.g-3.align-items-end"),
                    div(
                        attrs(".col-auto"),
                        label(attrs(".form-label"), "Select MCP Server").attr("for", "mcpServerName"),
                        select(attrs(".form-select"))
                            .attr("id", "mcpServerName")
                            .attr("name", "mcpServerName")
                            .attr("required", "required")
                            .with(
                                option("Choose...").attr("value", ""),
                                each(availableServers, server -> 
                                    option(server.getName() + " - " + server.getDescription())
                                        .attr("value", server.getName())
                                )
                            )
                    ),
                    div(
                        attrs(".col-auto"),
                        button(attrs(".btn.btn-primary"), "Add Server")
                            .attr("type", "submit")
                    )
                ).attr("method", "post")
                 .attr("action", "/applications/" + app.getId() + "/mcp-servers/add") :
                div(attrs(".alert.alert-warning"), "No MCP servers available to add.")
        );
    }

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }
}
