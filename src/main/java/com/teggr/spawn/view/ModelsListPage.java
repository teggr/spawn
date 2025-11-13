package com.teggr.spawn.view;

import com.teggr.spawn.dto.ModelResponse;
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
public class ModelsListPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        @SuppressWarnings("unchecked")
        List<ModelResponse> models = (List<ModelResponse>) model.get("models");

        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title("Models - Spawn"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                navbar(),
                div(
                    attrs(".container.mt-4"),
                    div(
                        attrs(".d-flex.justify-content-between.align-items-center.mb-3"),
                        h1("Models"),
                        a(attrs(".btn.btn-primary"), "Create New Model").withHref("/models/new")
                    ),
                    modelsTable(models)
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
                    a(attrs(".nav-link.active"), "Models").withHref("/models"),
                    a(attrs(".nav-link"), "MCP Servers").withHref("/mcp-servers"),
                    a(attrs(".nav-link"), "Applications").withHref("/applications")
                )
            )
        );
    }

    private ContainerTag modelsTable(List<ModelResponse> models) {
        if (models == null || models.isEmpty()) {
            return div(
                attrs(".alert.alert-info"),
                "No models found. Create your first model to get started!"
            );
        }

        return table(
            attrs(".table.table-striped"),
            thead(
                tr(
                    th("ID"),
                    th("Name"),
                    th("Type"),
                    th("Description"),
                    th("Actions")
                )
            ),
            tbody(
                each(models, modelResponse -> tr(
                    td(modelResponse.getId().toString()),
                    td(modelResponse.getName()),
                    td(modelResponse.getType()),
                    td(modelResponse.getDescription() != null ? modelResponse.getDescription() : ""),
                    td(
                        a(attrs(".btn.btn-sm.btn-primary.me-2"), "Edit")
                            .withHref("/models/" + modelResponse.getId() + "/edit"),
                        form(
                            attrs(".d-inline"),
                            button(attrs(".btn.btn-sm.btn-danger"), "Delete")
                                .attr("type", "submit")
                                .attr("onclick", "return confirm('Are you sure you want to delete this model?')")
                        ).attr("method", "post")
                         .attr("action", "/models/" + modelResponse.getId() + "/delete")
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
