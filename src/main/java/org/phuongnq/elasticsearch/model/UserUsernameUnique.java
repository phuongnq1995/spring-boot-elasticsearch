package org.phuongnq.elasticsearch.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.UUID;
import org.phuongnq.elasticsearch.service.UserService;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the username value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = UserUsernameUnique.UserUsernameUniqueValidator.class
)
public @interface UserUsernameUnique {

    String message() default "{Exists.user.username}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class UserUsernameUniqueValidator implements ConstraintValidator<UserUsernameUnique, String> {

        private final UserService userService;
        private final HttpServletRequest request;

        public UserUsernameUniqueValidator(final UserService userService,
                final HttpServletRequest request) {
            this.userService = userService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(userService.get(UUID.fromString(currentId)).getUsername())) {
                // value hasn't changed
                return true;
            }
            return !userService.usernameExists(value);
        }

    }

}
