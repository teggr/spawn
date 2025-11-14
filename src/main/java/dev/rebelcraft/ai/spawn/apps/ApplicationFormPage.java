package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.models.ModelResponse;
import dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout;
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
    String modelProvider = (String) model.get("modelProvider");
    String error = (String) model.get("error");
    @SuppressWarnings("unchecked")
    List<ModelResponse> models = (List<ModelResponse>) model.get("models");

    boolean isEdit = applicationId != null;

    DefaultPageLayout.createPage(
      (isEdit ? "Edit Application" : "Create Application") + " - Spawn",
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
            label(attrs(".form-label"), "Model Provider").attr("for", "modelProvider"),
            select(attrs(".form-select"))
              .attr("id", "modelProvider")
              .attr("name", "modelProvider")
              .with(
                option("Select a model provider...").attr("value", "").condAttr(modelProvider == null || modelProvider.isEmpty(), "selected", "selected"),
                models != null ? each(models, m ->
                  option(m.getProvider())
                    .attr("value", m.getProvider())
                    .condAttr(modelProvider != null && modelProvider.equals(m.getProvider()), "selected", "selected")
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
      )
    ).render(response.getWriter());
  }

  @Override
  public String getContentType() {
    return MediaType.TEXT_HTML_VALUE;
  }
}
