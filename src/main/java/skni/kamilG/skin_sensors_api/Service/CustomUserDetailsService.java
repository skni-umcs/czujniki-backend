package skni.kamilG.skin_sensors_api.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import skni.kamilG.skin_sensors_api.Model.Role;
import skni.kamilG.skin_sensors_api.Model.User;
import skni.kamilG.skin_sensors_api.Repository.UserRepository;

@Service
public final class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
  }

  private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(final Collection<Role> roles) {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .collect(Collectors.toList());
  }
}
