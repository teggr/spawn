package com.teggr.spawn.view;

import com.teggr.spawn.dto.ApplicationResponse;
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
public class ApplicationsListPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<ApplicationResponse> applications = (List<ApplicationResponse>) model.get("applications");

        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title("Applications - Spawn"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                navbar(),
                div(
                    attrs(".container.mt-4"),
                    div(
                        attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
                        h1("Applications"),
                        a(attrs(".btn.btn-primary"), "Create New Application").withHref("/applications/new")
                    ),
                    applicationsTable(applications)
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
                    th("Model"),
                    th("MCP Servers"),
                    th("Created At"),
                    th("Actions")
                )
            ),
            tbody(
                each(applications, app -> tr(
                    td(app.getId().toString()),
                    td(app.getName()),
                    td(app.getModel() != null ? app.getModel().getName() : "None"),
                    td(app.getMcpServers() != null ? String.valueOf(app.getMcpServers().size()) : "0"),
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

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }
}
