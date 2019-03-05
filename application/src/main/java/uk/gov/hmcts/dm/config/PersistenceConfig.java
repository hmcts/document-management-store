package uk.gov.hmcts.dm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.hmcts.reform.auth.checker.spring.serviceonly.ServiceDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class PersistenceConfig {
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                ServiceDetails userDetails = (ServiceDetails) authentication.getPrincipal();
                return Optional.ofNullable(userDetails.getUsername());
            } else {
                return Optional.empty();
            }
        }

    }
}
