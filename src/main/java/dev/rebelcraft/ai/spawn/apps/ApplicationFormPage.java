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
            label(attrs(".form-label"), "Model Providers (select multiple)").attr("for", "modelProviders"),
            select(attrs(".form-select"))
              .attr("id", "modelProviders")
              .attr("name", "modelProviders")
              .attr("multiple", "multiple")
              .attr("size", "5")
              .with(
                models != null ? each(renderModelOptions(models, selectedModelProviders)) : text("")
              )
          ),
          div(
            attrs(".mb-3"),
            label(attrs(".form-label"), "Agents (select multiple)").attr("for", "agentNames"),
            select(attrs(".form-select"))
              .attr("id", "agentNames")
              .attr("name", "agentNames")
              .attr("multiple", "multiple")
              .attr("size", "5")
              .with(
                agents != null ? each(renderAgentOptions(agents, selectedAgentNames)) : text("")
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
            .condAttr(selectedProviders != null && selectedProviders.contains(m.getProvider()), "selected", "selected")
        ))
      );
    }
    
    if (!others.isEmpty()) {
      options.add(optgroup().attr("label", favorites.isEmpty() ? "All Models" : "All Others")
        .with(each(others, m ->
          option(m.getProvider())
            .attr("value", m.getProvider())
            .condAttr(selectedProviders != null && selectedProviders.contains(m.getProvider()), "selected", "selected")
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
            .attr("value", a.getName())
            .condAttr(selectedNames != null && selectedNames.contains(a.getName()), "selected", "selected"))
        .toArray(DomContent[]::new);
  }

}
