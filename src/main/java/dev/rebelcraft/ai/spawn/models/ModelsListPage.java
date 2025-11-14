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
          modelsTable(models)
        )
      )
    );

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
            th("OpenAI API Compatible")
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
            td(modelResponse.getOpenAiApiCompatible())
          ))
        )
      )
    );
  }

}
