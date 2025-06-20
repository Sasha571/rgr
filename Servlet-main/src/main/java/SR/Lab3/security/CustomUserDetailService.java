package SR.Lab3.security;

import SR.Lab3.entity.User;
import SR.Lab3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository repository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Trying to load user: " + username);
        Optional<User> userEntity = repository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        System.out.println("Found user: " + userEntity.get().getUsername() +
                " with authority: " + userEntity.get().getAuthority());

        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(userEntity.get().getAuthority()));

        return new org.springframework.security.core.userdetails.User(
                userEntity.get().getUsername(), userEntity.get().getPassword(), grantedAuthorities);
    }

}
