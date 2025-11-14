package dev.rebelcraft.ai.spawn.agents;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.*;
import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.createPage;
import static j2html.TagCreator.*;

@Component
public class AgentFormPage extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

        String agentId = (String) model.get("agentId");
        String name = (String) model.get("name");
        String description = (String) model.get("description");
        String systemPrompt = (String) model.get("systemPrompt");
        String error = (String) model.get("error");

        @SuppressWarnings("unchecked")
        List<String> mcpServerNames = (List<String>) model.get("mcpServerNames");

        @SuppressWarnings("unchecked")
        List<McpServerResponse> mcpServers = (List<McpServerResponse>) model.get("mcpServers");

        boolean isEdit = agentId != null;

        // Small, safe inline JS that manages the client-side MCP list. Keep it concise to avoid quoting issues.
        String clientJs = "function addHiddenInput(name,value){var i=document.createElement('input');i.type='hidden';i.name=name;i.value=value;return i;}" +
                "function addMcpToList(val){if(!val) return;var c=document.getElementById('mcpListContainer');var item=document.createElement('div');item.className='d-flex align-items-center gap-2 mb-1';var span=document.createElement('span');span.innerText=val;var btn=document.createElement('button');btn.className='btn btn-sm btn-outline-danger';btn.type='button';btn.innerText='Remove';btn.onclick=function(){c.removeChild(item);};item.appendChild(span);item.appendChild(btn);item.appendChild(addHiddenInput('mcpServerNames',val));c.appendChild(item);}" +
                "function addMcpFromDropdown(){var sel=document.getElementById('mcpDropdown'); if(sel){addMcpToList(sel.value);} }" +
                "function addOtherMcp(){var inp=document.getElementById('otherMcpName'); if(inp){addMcpToList(inp.value); inp.value='';} }";

        return createPage(
                (isEdit ? "Edit Agent" : "Create Agent") + " - Spawn",
                ACTIVATE_AGENTS_NAV_LINK,
                each(
                        h1(isEdit ? "Edit Agent" : "Create New Agent"),
                        error != null ? div(attrs(".alert.alert-danger"), error) : text("") ,

                        form(
                                attrs(".mt-3"),

                                div(attrs(".mb-3"),
                                        label(attrs(".form-label"), "Name").attr("for", "name"),
                                        input(attrs(".form-control")).attr("type", "text").attr("id", "name").attr("name", "name").attr("required", "required").condAttr(name != null, "value", name != null ? name : "")
                                ),

                                div(attrs(".mb-3"),
                                        label(attrs(".form-label"), "Description").attr("for", "description"),
                                        input(attrs(".form-control")).attr("type", "text").attr("id", "description").attr("name", "description").condAttr(description != null, "value", description != null ? description : "")
                                ),

                                div(attrs(".mb-3"),
                                        label(attrs(".form-label"), "System Prompt").attr("for", "systemPrompt"),
                                        textarea(attrs(".form-control")).attr("id", "systemPrompt").attr("name", "systemPrompt").attr("rows", "10").withText(systemPrompt != null ? systemPrompt : "")
                                ),

                                div(attrs(".mb-3"),
                                        label(attrs(".form-label"), "MCP Servers"),

                                        // Dropdown + Add button
                                        div(attrs(".d-flex.gap-2.mb-2"),
                                                select(attrs(".form-select")).attr("id", "mcpDropdown").attr("name", "_mcpDropdown").with(
                                                        option("Select a server...").attr("value", ""),
                                                        mcpServers != null ? each(renderMcpServerOptions(mcpServers)) : text("")
                                                ),
                                                button(attrs(".btn.btn-secondary"), "Add").attr("type", "button").attr("onclick", "addMcpFromDropdown()")
                                        ),

                                        // Other input
                                        div(attrs(".mb-2"),
                                                input(attrs(".form-control")).attr("type", "text").attr("id", "otherMcpName").attr("placeholder", "Other MCP name")
                                        ),
                                        div(attrs(".mt-2"),
                                                button(attrs(".btn.btn-secondary"), "Add Other").attr("type", "button").attr("onclick", "addOtherMcp()")
                                        ),

                                        // Visible list container (pre-populate existing names)
                                        div(attrs(".mt-3"),
                                                div().attr("id", "mcpListContainer").with(
                                                        mcpServerNames != null ? each(mcpServerNames, n ->
                                                                div(attrs(".d-flex.align-items-center.gap-2.mb-1"),
                                                                        span(n),
                                                                        button(attrs(".btn.btn-sm.btn-outline-danger"), "Remove").attr("type", "button").attr("onclick", "(function(btn){var item = btn.parentElement; item.parentElement.removeChild(item);})(this);") ,
                                                                        input().attr("type", "hidden").attr("name", "mcpServerNames").attr("value", n)
                                                                )
                                                        ) : text("")
                                                )
                                        )
                                ),

                                div(attrs(".mt-3"),
                                        button(attrs(".btn.btn-primary.me-2"), "Save").attr("type", "submit"),
                                        a(attrs(".btn.btn-secondary"), "Cancel").withHref("/agents")
                                )

                        ).attr("method", "post").attr("action", isEdit ? "/agents/" + agentId : "/agents")
                                .with(
                                        script(clientJs)
                                )
                )
        );
    }

    private DomContent[] renderMcpServerOptions(List<McpServerResponse> mcpServers) {
        // Split into favorites and non-favorites, both sorted alphabetically
        List<McpServerResponse> favorites = mcpServers.stream()
            .filter(McpServerResponse::isFavorite)
            .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
            .collect(java.util.stream.Collectors.toList());
        
        List<McpServerResponse> others = mcpServers.stream()
            .filter(s -> !s.isFavorite())
            .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
            .collect(java.util.stream.Collectors.toList());
        
        java.util.List<DomContent> options = new java.util.ArrayList<>();
        
        if (!favorites.isEmpty()) {
            options.add(optgroup().attr("label", "Favorites")
                .with(each(favorites, s -> option(s.getName()).attr("value", s.getName())))
            );
        }
        
        if (!others.isEmpty()) {
            options.add(optgroup().attr("label", favorites.isEmpty() ? "All MCP Servers" : "All Others")
                .with(each(others, s -> option(s.getName()).attr("value", s.getName())))
            );
        }
        
        return options.toArray(new DomContent[0]);
    }
}
