package todoapp.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import todoapp.core.user.application.RegisterUser;
import todoapp.core.user.application.VerifyUserPassword;
import todoapp.core.user.domain.User;
import todoapp.core.user.domain.UserNotFoundException;
import todoapp.core.user.domain.UserPasswordNotMatchedException;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;

import java.util.Objects;


@Controller
public class LoginController {

    private final VerifyUserPassword verifyUserPassword;
    private final RegisterUser registerUser;
    private final UserSessionHolder userSessionHolder;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public LoginController(VerifyUserPassword verifyUserPassword, RegisterUser registerUser, UserSessionHolder userSessionHolder) {
        this.verifyUserPassword = Objects.requireNonNull(verifyUserPassword);
        this.registerUser = Objects.requireNonNull(registerUser);
        this.userSessionHolder = Objects.requireNonNull(userSessionHolder);
    }

    @GetMapping("/login")
    public void login() {
    }

    @PostMapping(path = "/login")
    public String loginProcess(
            @Valid LoginUserCommand command,
            BindingResult bindingResult,
            Model model
    ) {
        log.info("request command: " + command);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("message", "입력 값이 없거나 올바르지 않습니다.");
            return "login";
        }

        User user;
        try {
            user = verifyUserPassword.verify(command.username, command.password);
        } catch (UserNotFoundException ex) {
            user = registerUser.register(command.username, command.password);
        }

        userSessionHolder.set(new UserSession(user));
        return "redirect:/todos";
    }

    @ExceptionHandler(UserPasswordNotMatchedException.class)
    public String handleUserPasswordNotMatched(UserPasswordNotMatchedException ex, Model model) {
        model.addAttribute("message", "비밀번호가 일치하지 않습니다.");
        return "login";
    }

    record LoginUserCommand(
            @Size(min = 4, max = 20) String username,
            @NotEmpty String password
    ) {
    }
}
