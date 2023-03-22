package com.epam.esm.utils;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchParam {
    public static final String SEARCH_PARAM_DELIMITER = ":";
    public static final String DEFAULT_SEARCH_VALUE = "";
    private String fieldName;
    private String searchValue;

    public List<SearchParam> parseAll(String[] searchParams) {
        return Arrays.stream(searchParams)
                .map(this::parse)
                .collect(Collectors.toList());
    }

    public SearchParam parse(String searchParam) {
        String[] tokens = searchParam.split(SEARCH_PARAM_DELIMITER);
        String tokenValue = (tokens.length >= 2) ? tokens[1] : DEFAULT_SEARCH_VALUE;
        return new SearchParam(tokens[0].trim(), tokenValue.trim());
    }
}
