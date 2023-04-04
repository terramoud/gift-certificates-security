package com.epam.esm.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * A helper class for translating messages to the user's locale.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public class Translator {

    private final ResourceBundleMessageSource messageSource;

    /**
     * Constructs a new Translator object with the
     * specified message source.
     *
     * @param messageSource the message source to use
     *                      for translations
     */
    @Autowired
    public Translator(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Translates a message to the user's locale.
     *
     * @param msgCode the code of the message to translate
     * @return the translated message
     */
    public String toLocale(String msgCode) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, null, locale);
    }
}
