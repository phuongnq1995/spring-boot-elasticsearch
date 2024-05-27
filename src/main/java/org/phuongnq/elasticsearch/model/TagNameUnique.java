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
import org.phuongnq.elasticsearch.service.TagService;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the name value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = TagNameUnique.TagNameUniqueValidator.class
)
public @interface TagNameUnique {

    String message() default "{Exists.tag.name}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class TagNameUniqueValidator implements ConstraintValidator<TagNameUnique, String> {

        private final TagService tagService;
        private final HttpServletRequest request;

        public TagNameUniqueValidator(final TagService tagService,
                final HttpServletRequest request) {
            this.tagService = tagService;
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
            if (currentId != null && value.equalsIgnoreCase(tagService.get(UUID.fromString(currentId)).getName())) {
                // value hasn't changed
                return true;
            }
            return !tagService.nameExists(value);
        }

    }

}
