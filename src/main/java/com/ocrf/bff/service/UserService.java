package com.ocrf.bff.service;

import com.ocrf.bff.service.dto.PnUserLevel;
import com.ocrf.bff.service.dto.Userinfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    public Userinfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Userinfo userinfo = new Userinfo();
        if (isUserAnonymous()) {
            userinfo.setName(authentication.getName());
            userinfo.setFio(authentication.getName());
        } else {
            OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userinfo.setAuthorities(principal.getAttribute("authorities"));
            userinfo.setName(principal.getName());
            userinfo.setFio(principal.getAttribute("fio"));
            userinfo.setDepartment(principal.getAttribute("department"));
            userinfo.setTelephone(principal.getAttribute("telephone"));
            userinfo.setTelephoneSuffix(principal.getAttribute("telephoneSuffix"));
            userinfo.setFunction(principal.getAttribute("function"));
            String level = principal.getAttribute("level");
            if (PnUserLevel.valueExist(level)) {
                PnUserLevel pnUserLevel = PnUserLevel.getByValue(level);
                userinfo.setLevelName(pnUserLevel.name());
                userinfo.setLevelDescription(pnUserLevel.getDescription());
            }
        }
        return userinfo;
    }

    private boolean isUserAnonymous() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        return false;
    }
}
