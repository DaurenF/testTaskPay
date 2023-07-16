package kz.s1lk.pay.service.impl;

import kz.s1lk.pay.model.entity.CardHolder;
import kz.s1lk.pay.repository.CardHolderRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CardHolderRepository cardHolderRepository;

    public UserDetailsServiceImpl(CardHolderRepository userRepository) {
        this.cardHolderRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<CardHolder> cardHolder = cardHolderRepository.findByEmail(username);
        if (cardHolder.isEmpty())
            throw new UsernameNotFoundException("User not found");

        return new UserDetailsImpl(cardHolder.get());
    }
}
