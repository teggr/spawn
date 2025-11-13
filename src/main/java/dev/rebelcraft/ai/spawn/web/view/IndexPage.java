package dev.rebelcraft.ai.spawn.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.util.Map;

import static j2html.TagCreator.*;

@Component
public class IndexPage implements View {

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        html(
            head(
                meta().attr("charset", "utf-8"),
                meta().attr("name", "viewport").attr("content", "width=device-width, initial-scale=1"),
                title("Spawn - AI Application Builder"),
                link().withRel("stylesheet")
                    .withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css")
            ),
            body(
                nav(
                    attrs(".navbar.navbar-expand-lg.navbar-dark.bg-dark"),
                    div(
                        attrs(".container-fluid"),
                        a(attrs(".navbar-brand"), "Spawn").withHref("/"),
                        div(
                            attrs(".navbar-nav"),
                            a(attrs(".nav-link"), "Models").withHref("/models"),
                            a(attrs(".nav-link"), "MCP Servers").withHref("/mcp-servers"),
                            a(attrs(".nav-link"), "Applications").withHref("/applications")
                        )
                    )
                ),
                div(
                    attrs(".container.mt-4"),
                    h1("Welcome to Spawn"),
                    p("Application for building AI capable applications on the fly with UI, models and MCP servers."),
                    div(
                        attrs(".row.mt-4"),
                        div(
                            attrs(".col-md-4"),
                            div(
                                attrs(".card"),
                                div(
                                    attrs(".card-body"),
                                    h5(attrs(".card-title"), "Models"),
                                    p(attrs(".card-text"), "Manage AI models like GPT-4, Claude, etc."),
                                    a(attrs(".btn.btn-primary"), "Manage Models").withHref("/models")
                                )
                            )
                        ),
                        div(
                            attrs(".col-md-4"),
                            div(
                                attrs(".card"),
                                div(
                                    attrs(".card-body"),
                                    h5(attrs(".card-title"), "MCP Servers"),
                                    p(attrs(".card-text"), "Configure MCP servers for your applications."),
                                    a(attrs(".btn.btn-primary"), "Manage MCP Servers").withHref("/mcp-servers")
                                )
                            )
                        ),
                        div(
                            attrs(".col-md-4"),
                            div(
                                attrs(".card"),
                                div(
                                    attrs(".card-body"),
                                    h5(attrs(".card-title"), "Applications"),
                                    p(attrs(".card-text"), "Build and deploy AI applications."),
                                    a(attrs(".btn.btn-primary"), "Manage Applications").withHref("/applications")
                                )
                            )
                        )
                    )
                ),
                script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js")
            )
        ).render(response.getWriter());
    }

    @Override
    public String getContentType() {
        return MediaType.TEXT_HTML_VALUE;
    }
}
