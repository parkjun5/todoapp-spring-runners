package todoapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import todoapp.core.todo.application.FindTodos;
import todoapp.core.todo.domain.support.SpreadsheetConverter;
import todoapp.web.model.SiteProperties;

import java.util.Objects;

@Controller
public class TodoController {

    private final FindTodos findTodos;
    private final SiteProperties siteProperties;

    public TodoController(FindTodos findTodos, SiteProperties siteProperties) {
        this.findTodos = findTodos;
        this.siteProperties = Objects.requireNonNull(siteProperties);
    }

    @RequestMapping("/todos")
    public void todos(Model model) {
        model.addAttribute("site", siteProperties);
//        return "todos";
    }

    @RequestMapping(path = "/todos", produces = "text/csv")
    public void downloadTodos(Model model) {
        var todos = findTodos.all();
        model.addAttribute(SpreadsheetConverter.convert(todos));
    }

/*    public static class TodoCsvViewResolver implements ViewResolver {

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            if ("todos".equals(viewName)) {
                return new CommaSeparatedValuesView();
            }
            return null;
        }
    }*/
//
//    public static class TodoCsvView extends AbstractView implements View {
//
//        final Logger log = LoggerFactory.getLogger(getContentType());
//
//        public TodoCsvView() {
//            setContentType("text/csv");
//        }
//
//        @Override
//        protected boolean generatesDownloadContent() {
//            return true;
//        }
//
//        @Override
//        protected void renderMergedOutputModel(
//                Map<String, Object> model,
//                HttpServletRequest request,
//                HttpServletResponse response
//        ) throws Exception {
//            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.csv\"");
//            response.setContentType("text/csv");
//            response.getWriter().println("username,text,completed");
//            var todos = (List<Todo>) model.getOrDefault("todos", Collections.emptyList());
//            for (var todo : todos) {
//                var line = "%s,%s,%s".formatted(todo.getId(), todo.getText(), todo.isCompleted());
//                response.getWriter().println(line);
//            }
//            response.flushBuffer();
//        }
//    }
}
