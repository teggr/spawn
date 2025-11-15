package dev.rebelcraft.ai.spawn.mcp;

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
public class McpServerTemplatePage extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        String serverName = (String) model.get("serverName");
        String serverDescription = (String) model.get("serverDescription");
        String templateFilename = (String) model.get("templateFilename");
        String templateRaw = (String) model.get("templateRaw");
        String templateCompiled = (String) model.get("templateCompiled");
        
        @SuppressWarnings("unchecked")
        List<McpTemplate.McpTemplateInput> inputs = (List<McpTemplate.McpTemplateInput>) model.get("inputs");

        return createPage(
            serverName + " Template - Spawn",
            ACTIVATE_MCP_NAV_LINK,
            each(
                div(
                    attrs(".container.mt-4"),
                    div(
                        attrs(".mb-3"),
                        h1("MCP Server Template: " + serverName),
                        p(attrs(".text-muted"), serverDescription),
                        p(attrs(".text-muted"), "Template file: " + templateFilename)
                    ),
                    
                    // Back button
                    div(
                        attrs(".mb-4"),
                        a(attrs(".btn.btn-secondary"), "← Back to MCP Servers").withHref("/mcp-servers")
                    ),
                    
                    // Inputs section
                    renderInputsSection(inputs),
                    
                    // Template preview section
                    renderTemplatePreview(templateCompiled)
                )
            )
        );
    }

    private DomContent renderInputsSection(List<McpTemplate.McpTemplateInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return div(
                attrs(".mb-4"),
                h3("Inputs"),
                p(attrs(".text-muted"), "This template has no configurable inputs.")
            );
        }

        return div(
            attrs(".mb-4"),
            h3("Configuration Inputs"),
            p(attrs(".text-muted"), 
                "The following inputs are required to configure this MCP server. " +
                "Password fields are masked in the preview below."),
            div(
                attrs(".table-responsive"),
                table(
                    attrs(".table.table-bordered.table-striped"),
                    thead(
                        tr(
                            th("ID"),
                            th("Description"),
                            th("Type"),
                            th("Password"),
                            th("Default")
                        )
                    ),
                    tbody(
                        each(inputs, input -> tr(
                            td(code(input.getId())),
                            td(input.getDescription() != null ? input.getDescription() : ""),
                            td(input.getType() != null ? input.getType() : ""),
                            td(Boolean.TRUE.equals(input.getPassword()) ? 
                                span(attrs(".badge.bg-warning"), "Yes") : 
                                span(attrs(".badge.bg-secondary"), "No")),
                            td(renderDefaultValue(input))
                        ))
                    )
                )
            )
        );
    }

    private DomContent renderDefaultValue(McpTemplate.McpTemplateInput input) {
        String defaultValue = input.getDefaultValue();
        if (defaultValue == null || defaultValue.isEmpty()) {
            return span(attrs(".text-muted.fst-italic"), "(none)");
        }
        
        if (Boolean.TRUE.equals(input.getPassword())) {
            return code("*****");
        }
        
        return code(defaultValue);
    }

    private DomContent renderTemplatePreview(String templateCompiled) {
        return div(
            attrs(".mb-4"),
            h3("Template Preview"),
            p(attrs(".text-muted"), 
                "This preview shows the template with placeholders replaced. " +
                "Password fields are masked with *****, and fields without defaults show placeholder hints."),
            pre(
                attrs(".bg-light.p-3.rounded.border"),
                code(templateCompiled != null ? templateCompiled : "{}")
            )
        );
    }
}
