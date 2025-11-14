package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
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
public class ApplicationFormPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

    String applicationId = (String) model.get("applicationId");
    String name = (String) model.get("name");
    String error = (String) model.get("error");
    @SuppressWarnings("unchecked")
    List<String> selectedModelProviders = (List<String>) model.get("selectedModelProviders");
    @SuppressWarnings("unchecked")
    List<String> selectedAgentNames = (List<String>) model.get("selectedAgentNames");
    @SuppressWarnings("unchecked")
    List<ModelResponse> models = (List<ModelResponse>) model.get("models");
    @SuppressWarnings("unchecked")
    List<AgentResponse> agents = (List<AgentResponse>) model.get("agents");

    boolean isEdit = applicationId != null;

    // Small, safe inline JS that manages the client-side lists. Keep it concise to avoid quoting issues.
    String clientJs = "function addHiddenInput(name,value){var i=document.createElement('input');i.type='hidden';i.name=name;i.value=value;return i;}" +
            "function addModelToList(val){if(!val) return;var c=document.getElementById('modelListContainer');var item=document.createElement('div');item.className='d-flex align-items-center gap-2 mb-1';var span=document.createElement('span');span.innerText=val;var btn=document.createElement('button');btn.className='btn btn-sm btn-outline-danger';btn.type='button';btn.innerText='Remove';btn.onclick=function(){c.removeChild(item);};item.appendChild(span);item.appendChild(btn);item.appendChild(addHiddenInput('modelProviders',val));c.appendChild(item);}" +
            "function addModelFromDropdown(){var sel=document.getElementById('modelDropdown'); if(sel){addModelToList(sel.value);} }" +
            "function addAgentToList(val){if(!val) return;var c=document.getElementById('agentListContainer');var item=document.createElement('div');item.className='d-flex align-items-center gap-2 mb-1';var span=document.createElement('span');span.innerText=val;var btn=document.createElement('button');btn.className='btn btn-sm btn-outline-danger';btn.type='button';btn.innerText='Remove';btn.onclick=function(){c.removeChild(item);};item.appendChild(span);item.appendChild(btn);item.appendChild(addHiddenInput('agentNames',val));c.appendChild(item);}" +
            "function addAgentFromDropdown(){var sel=document.getElementById('agentDropdown'); if(sel){addAgentToList(sel.value);} }";

    return createPage(
      (isEdit ? "Edit Application" : "Create Application") + " - Spawn",
      ACTIVATE_APPS_NAV_LINK,
      each(
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
            label(attrs(".form-label"), "Model Providers"),
            
            // Dropdown + Add button
            div(attrs(".d-flex.gap-2.mb-2"),
              select(attrs(".form-select")).attr("id", "modelDropdown").attr("name", "_modelDropdown").with(
                option("Select a model...").attr("value", ""),
                models != null ? each(renderModelOptions(models, selectedModelProviders)) : text("")
              ),
              button(attrs(".btn.btn-secondary"), "Add").attr("type", "button").attr("onclick", "addModelFromDropdown()")
            ),
            
            // Visible list container (pre-populate existing selections)
            div(attrs(".mt-3"),
              div().attr("id", "modelListContainer").with(
                selectedModelProviders != null ? each(selectedModelProviders, p ->
                  div(attrs(".d-flex.align-items-center.gap-2.mb-1"),
                    span(p),
                    button(attrs(".btn.btn-sm.btn-outline-danger"), "Remove").attr("type", "button").attr("onclick", "(function(btn){var item = btn.parentElement; item.parentElement.removeChild(item);})(this);"),
                    input().attr("type", "hidden").attr("name", "modelProviders").attr("value", p)
                  )
                ) : text("")
              )
            )
          ),
          div(
            attrs(".mb-3"),
            label(attrs(".form-label"), "Agents"),
            
            // Dropdown + Add button
            div(attrs(".d-flex.gap-2.mb-2"),
              select(attrs(".form-select")).attr("id", "agentDropdown").attr("name", "_agentDropdown").with(
                option("Select an agent...").attr("value", ""),
                agents != null ? each(renderAgentOptions(agents, selectedAgentNames)) : text("")
              ),
              button(attrs(".btn.btn-secondary"), "Add").attr("type", "button").attr("onclick", "addAgentFromDropdown()")
            ),
            
            // Visible list container (pre-populate existing selections)
            div(attrs(".mt-3"),
              div().attr("id", "agentListContainer").with(
                selectedAgentNames != null ? each(selectedAgentNames, n ->
                  div(attrs(".d-flex.align-items-center.gap-2.mb-1"),
                    span(n),
                    button(attrs(".btn.btn-sm.btn-outline-danger"), "Remove").attr("type", "button").attr("onclick", "(function(btn){var item = btn.parentElement; item.parentElement.removeChild(item);})(this);"),
                    input().attr("type", "hidden").attr("name", "agentNames").attr("value", n)
                  )
                ) : text("")
              )
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
          .with(
            script(clientJs)
          )
      )
    );
  }

  private DomContent[] renderModelOptions(List<ModelResponse> models, List<String> selectedProviders) {
    // Split into favorites and non-favorites, both sorted alphabetically
    List<ModelResponse> favorites = models.stream()
        .filter(ModelResponse::isFavorite)
        .sorted((a, b) -> a.getProvider().compareToIgnoreCase(b.getProvider()))
        .collect(java.util.stream.Collectors.toList());
    
    List<ModelResponse> others = models.stream()
        .filter(m -> !m.isFavorite())
        .sorted((a, b) -> a.getProvider().compareToIgnoreCase(b.getProvider()))
        .collect(java.util.stream.Collectors.toList());
    
    java.util.List<DomContent> options = new java.util.ArrayList<>();
    
    if (!favorites.isEmpty()) {
      options.add(optgroup().attr("label", "Favorites")
        .with(each(favorites, m ->
          option(m.getProvider())
            .attr("value", m.getProvider())
        ))
      );
    }
    
    if (!others.isEmpty()) {
      options.add(optgroup().attr("label", favorites.isEmpty() ? "All Models" : "All Others")
        .with(each(others, m ->
          option(m.getProvider())
            .attr("value", m.getProvider())
        ))
      );
    }
    
    return options.toArray(new DomContent[0]);
  }

  private DomContent[] renderAgentOptions(List<AgentResponse> agents, List<String> selectedNames) {
    // Sort alphabetically
    List<AgentResponse> sortedAgents = agents.stream()
        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
        .collect(java.util.stream.Collectors.toList());
    
    return sortedAgents.stream()
        .map(a -> option(a.getName())
            .attr("value", a.getName()))
        .toArray(DomContent[]::new);
  }

}
