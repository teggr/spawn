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
public class ApplicationFormPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String applicationId = (String) model.get("applicationId");
        String name = (String) model.get("name");
        String modelId = (String) model.get("modelId");
        String error = (String) model.get("error");
        @SuppressWarnings("unchecked")
        List<ModelResponse> models = (List<ModelResponse>) model.get("models");
        
        boolean isEdit = applicationId != null;

        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title((isEdit ? "Edit Application" : "Create Application") + " - Spawn"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                navbar(),
                div(
                    attrs(".container.mt-4"),
                    h1(isEdit ? "Edit Application" : "Create New Application"),
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
                            label(attrs(".form-label"), "Model").attr("for", "modelId"),
                            select(attrs(".form-select"))
                                .attr("id", "modelId")
                                .attr("name", "modelId")
                                .with(
                                    option("Select a model...").attr("value", "").condAttr(modelId == null || modelId.isEmpty(), "selected", "selected"),
                                    models != null ? each(models, m -> 
                                        option(m.getName() + " (" + m.getType() + ")")
                                            .attr("value", m.getId().toString())
                                            .condAttr(modelId != null && modelId.equals(m.getId().toString()), "selected", "selected")
                                    ) : text("")
                                )
                        ),
                        div(
                            attrs(".mt-3"),
                            button(attrs(".btn.btn-primary.me-2"), "Save")
                                .attr("type", "submit"),
                            a(attrs(".btn.btn-secondary"), "Cancel").withHref("/applications")
                        )
                    ).attr("method", "post")
                     .attr("action", isEdit ? "/applications/" + applicationId : "/applications")
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

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }
}
