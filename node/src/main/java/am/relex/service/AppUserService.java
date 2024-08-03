package am.relex.service;

import am.relex.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser,String email);
}
