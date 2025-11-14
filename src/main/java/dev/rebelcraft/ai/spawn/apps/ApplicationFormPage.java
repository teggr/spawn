package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

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

}
