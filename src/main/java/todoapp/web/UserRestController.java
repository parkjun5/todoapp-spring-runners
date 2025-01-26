package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import todoapp.core.user.application.ChangeUserProfilePicture;
import todoapp.core.user.domain.ProfilePicture;
import todoapp.core.user.domain.ProfilePictureStorage;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;
import todoapp.web.model.UserProfile;

@RestController
public class UserRestController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ProfilePictureStorage profilePictureStorage;
    private final ChangeUserProfilePicture changeUserProfilePicture;
    private final UserSessionHolder userSessionHolder;

    public UserRestController(ProfilePictureStorage profilePictureStorage, ChangeUserProfilePicture changeUserProfilePicture, UserSessionHolder userSessionHolder) {
        this.profilePictureStorage = profilePictureStorage;
        this.changeUserProfilePicture = changeUserProfilePicture;
        this.userSessionHolder = userSessionHolder;
    }

    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("/api/user/profile")
    public UserProfile userProfile(UserSession userSession) {
        return new UserProfile(userSession.getUser());
    }

    @PostMapping("/api/user/profile-picture")
    public UserProfile changeProfilePicture(
            UserSession userSession,
            @RequestParam("profilePicture") MultipartFile profilePicture
    ) {
        log.debug("processing user-session profile picture: " + profilePicture);
        var profilePictureUri = profilePictureStorage.save(profilePicture.getResource());
        var updatedUser = changeUserProfilePicture.change(userSession.getName(), new ProfilePicture(profilePictureUri));
        userSessionHolder.set(new UserSession(updatedUser));
        return new UserProfile(userSession.getUser());
    }
}
