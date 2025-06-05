package mk.ukim.finki.wp.ukimctfwebsite.service.impl;

import org.springframework.stereotype.Service;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

@Service
public class MarkdownService {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownService() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }

    public String convertMarkdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}