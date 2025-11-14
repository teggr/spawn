package dev.rebelcraft.ai.spawn.models;

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
public class ModelsListPage extends PageView {

  @Override
  protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
    @SuppressWarnings("unchecked")
    List<ModelResponse> models = (List<ModelResponse>) model.get("models");

    // Split into favorites and non-favorites, both sorted alphabetically
    List<ModelResponse> favorites = models.stream()
        .filter(ModelResponse::isFavorite)
        .sorted((a, b) -> a.getProvider().compareToIgnoreCase(b.getProvider()))
        .collect(java.util.stream.Collectors.toList());
    
    List<ModelResponse> others = models.stream()
        .filter(m -> !m.isFavorite())
        .sorted((a, b) -> a.getProvider().compareToIgnoreCase(b.getProvider()))
        .collect(java.util.stream.Collectors.toList());

    return createPage(
      "Models - Spawn",
      ACTIVATE_MODELS_NAV_LINK,
      each(
        div(
          attrs(".container.mt-4"),
          div(
            attrs(".mb-3"),
            h1("Available AI Models"),
            p(attrs(".text-muted"), "These models are loaded from the models.csv configuration file.")
          ),
          each(renderModelSections(favorites, others))
        )
      )
    );

  }

  private DomContent[] renderModelSections(List<ModelResponse> favorites, List<ModelResponse> others) {
    java.util.List<DomContent> sections = new java.util.ArrayList<>();
    
    if (!favorites.isEmpty()) {
      sections.add(div(
        h3("Favorites"),
        modelsTable(favorites)
      ));
    }
    
    if (!others.isEmpty()) {
      ContainerTag heading = favorites.isEmpty() ? h3("All Models") : h3(attrs(".mt-4"), "All Models");
      sections.add(div(
        heading,
        modelsTable(others)
      ));
    }
    
    if (favorites.isEmpty() && others.isEmpty()) {
      sections.add(div(
        attrs(".alert.alert-info"),
        "No models found."
      ));
    }
    
    return sections.toArray(new DomContent[0]);
  }

  private ContainerTag modelsTable(List<ModelResponse> models) {
    if (models == null || models.isEmpty()) {
      return div(
        attrs(".alert.alert-info"),
        "No models found."
      );
    }

    return div(
      attrs(".table-responsive"),
      table(
        attrs(".table.table-striped.table-hover"),
        thead(
          tr(
            th("Provider"),
            th("Multimodality"),
            th("Tools/Functions"),
            th("Streaming"),
            th("Retry"),
            th("Observability"),
            th("Built-in JSON"),
            th("Local"),
            th("OpenAI API Compatible"),
            th("Actions")
          )
        ),
        tbody(
          each(models, modelResponse -> tr(
            td(modelResponse.getProvider()),
            td(modelResponse.getMultimodality()),
            td(modelResponse.getToolsFunctions()),
            td(modelResponse.getStreaming()),
            td(modelResponse.getRetry()),
            td(modelResponse.getObservability()),
            td(modelResponse.getBuiltInJson()),
            td(modelResponse.getLocal()),
            td(modelResponse.getOpenAiApiCompatible()),
            td(
              modelResponse.isFavorite() ?
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-outline-warning"))
                    .withType("submit")
                    .attr("formaction", "/models/" + modelResponse.getProvider() + "/unfavorite")
                    .attr("formmethod", "post")
                    .withText("★ Unfavorite")
                ) :
                form(
                  attrs(".d-inline"),
                  button(attrs(".btn.btn-sm.btn-outline-primary"))
                    .withType("submit")
                    .attr("formaction", "/models/" + modelResponse.getProvider() + "/favorite")
                    .attr("formmethod", "post")
                    .withText("☆ Favorite")
                )
            )
          ))
        )
      )
    );
  }

}
