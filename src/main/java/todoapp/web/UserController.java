package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import todoapp.core.user.domain.ProfilePicture;
import todoapp.security.UserSession;

@Controller
public class UserController {

    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("/user/profile-picture")
    public ProfilePicture changeProfilePicture(UserSession userSession) {
        return userSession.getUser().getProfilePicture();
    }
}
