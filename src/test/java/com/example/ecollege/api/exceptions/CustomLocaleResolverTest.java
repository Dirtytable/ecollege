package com.example.ecollege.api.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.*;

class CustomLocaleResolverTest {

    @Test
    void haveLocale() {
        List<Locale> locales = new CustomLocaleResolver().locales;
        List<String> result = new ArrayList<>();
        for (Locale locale: locales
             ) {
            result.add(locale.getLanguage());
        }
        assertThat(result).contains("en").contains("ru").contains("fr").contains("es");
    }

    @Test
    void canResolveLocale() {
        Locale locale = new Locale("en");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", locale.getLanguage());
        Locale result = new CustomLocaleResolver().resolveLocale(request);
        assertThat(result.getLanguage()).isEqualTo(locale.getLanguage());
    }
    @Test
    void resolveLocale() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "");
        Locale result = new CustomLocaleResolver().resolveLocale(request);
        assertThat(result.getLanguage()).isEqualTo("ru");
    }
}