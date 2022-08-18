package com.example.ecollege.api.exceptions;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;


public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    List<Locale> locales = Arrays.asList(new Locale("en"),new Locale("ru")
            ,new Locale("es"), new Locale("fr"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (request.getHeader("Accept-Language").isEmpty()) {
            return Locale.getDefault();
        }
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader("Accept-Language"));
        return Locale.lookup(list,locales);
    }
}