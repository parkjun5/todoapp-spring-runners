package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import todoapp.core.shared.identifier.TodoId;
import todoapp.core.todo.application.AddTodo;
import todoapp.core.todo.application.FindTodos;
import todoapp.core.todo.application.ModifyTodo;
import todoapp.core.todo.application.RemoveTodo;
import todoapp.core.todo.domain.Todo;
import todoapp.security.UserSession;

import java.util.Objects;

@RestController
@RequestMapping("/api/todos")
@RolesAllowed(UserSession.ROLE_USER)
public class TodoRestController {

    private final FindTodos find;
    private final AddTodo addTodo;
    private final ModifyTodo modifyTodo;
    private final RemoveTodo removeTodo;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public TodoRestController(FindTodos find, AddTodo addTodo, ModifyTodo modifyTodo, RemoveTodo removeTodo) {
        this.find = Objects.requireNonNull(find);
        this.addTodo = Objects.requireNonNull(addTodo);
        this.modifyTodo = Objects.requireNonNull(modifyTodo);
        this.removeTodo = Objects.requireNonNull(removeTodo);
    }

    @GetMapping
    public Iterable<Todo> readAll() {
        return find.all();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody @Valid WriteTodoCommand command) {
        addTodo.add(command.text());

    }

    @PutMapping("/{id}")
    public void modify(
            @PathVariable("id") String id,
            @RequestBody @Valid WriteTodoCommand command
    ) {
        log.debug("request username : {}, command : {}", id, command);
        modifyTodo.modify(TodoId.of(id), command.text(), command.completed());
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable("id") String id
    ) {
        removeTodo.remove(TodoId.of(id));
    }

    record WriteTodoCommand(
            @NotBlank @Size(min = 4, max = 140) String text,
            boolean completed
    ) {
    }

}
