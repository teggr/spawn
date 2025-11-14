package dev.rebelcraft.ai.spawn.web.view;

import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import java.util.Map;

public abstract class PageView implements View {

  @Override
  public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    renderPage(model, request, response)
      .render(response.getWriter());
  }

  protected abstract DomContent renderPage(Map<String,?> model, HttpServletRequest request, HttpServletResponse response);


  @Override
  public String getContentType() {
    return MediaType.TEXT_HTML_VALUE;
  }
  
}
