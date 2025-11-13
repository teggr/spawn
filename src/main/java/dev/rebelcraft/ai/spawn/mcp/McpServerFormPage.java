package dev.rebelcraft.ai.spawn.mcp;

import j2html.tags.ContainerTag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.util.Map;

import static j2html.TagCreator.*;

@Component
public class McpServerFormPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String serverId = (String) model.get("serverId");
        String name = (String) model.get("name");
        String url = (String) model.get("url");
        String description = (String) model.get("description");
        String error = (String) model.get("error");
        
        boolean isEdit = serverId != null;

        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title((isEdit ? "Edit MCP Server" : "Create MCP Server") + " - Spawn"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                navbar(),
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

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }
}
