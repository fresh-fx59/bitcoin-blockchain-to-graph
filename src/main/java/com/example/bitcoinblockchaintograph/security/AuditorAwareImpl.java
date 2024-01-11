package com.example.bitcoinblockchaintograph.security;

import com.example.bitcoinblockchaintograph.entity.enumeration.CreatedByEnum;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

/**
 * Overrides @CreatedBy @ModifiedBy annotation population
 * <a href="https://www.baeldung.com/database-auditing-jpa">Manual by Baeldung</a>
 *
 */
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.of(authentication == null ?
                CreatedByEnum.UNKNOWN.getInDb() :
                ((User) authentication.getPrincipal()).getUsername());

    }
}
