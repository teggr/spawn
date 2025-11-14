package dev.rebelcraft.ai.spawn.models;

import dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout;
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

    DefaultPageLayout.createPage(
      "Models - Spawn",
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
    ).render(response.getWriter());

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

  @Override
  public String getContentType() {
    return MediaType.TEXT_HTML_VALUE;
  }
}
