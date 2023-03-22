package com.epam.esm.service.impl;

import com.epam.esm.domain.entity.AbstractEntity;
import com.epam.esm.domain.payload.PageDto;
import com.epam.esm.repository.api.BaseRepository;
import com.epam.esm.service.api.BaseService;
import com.epam.esm.utils.SearchParam;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
public abstract class AbstractService<T, N> implements BaseService<T, N> {
    protected static final String SORT_REQUEST_PARAM = "sort";
    protected static final String SEARCH_REQUEST_PARAM = "search";
    protected static final String ASC_BY_ID = "+id";
    protected static final String SQL_LIKE_PATTERN = "%{0}%";
    protected static final String SUB_PARAM_DELIMITER = ",";
    protected static final String DESC_PREFIX = "-";
    protected static final String ASC_PREFIX = "+";

    @SafeVarargs
    protected final boolean isEqualsIds(N... ids) {
        return List.of(ids).isEmpty() || Arrays.stream(ids).allMatch(ids[0]::equals);
    }

    protected <E extends AbstractEntity> List<E> findAllAbstract(
            LinkedMultiValueMap<String, String> requestParams,
            PageDto pageDto,
            BaseRepository<E, N> repository,
            Map<String, Sort> sortMap,
            Map<String, Function<String, Specification<E>>> filterMap,
            Map<String, Function<String, Specification<E>>> searchMap) {
        Sort sortParams = createSortParams(requestParams, sortMap);
        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sortParams);
        Specification<E> filterSpecs = createFilters(requestParams, filterMap);
        Specification<E> searchSpecs = createSearchSpecifications(requestParams, searchMap);
        return repository.findAll(filterSpecs.and(searchSpecs), pageable).getContent();
    }

    protected <E extends AbstractEntity> List<E> findAllAbstract(
            LinkedMultiValueMap<String, String> requestParams,
            PageDto pageDto,
            BaseRepository<E, N> repository,
            Map<String, Sort> sortMap,
            Map<String, Function<String, Specification<E>>> filterMap,
            Map<String, Function<String, Specification<E>>> searchMap,
            Specification<E> additionalSpecification) {
        Sort sortParams = createSortParams(requestParams, sortMap);
        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sortParams);
        Specification<E> filterSpecs = createFilters(requestParams, filterMap);
        Specification<E> searchSpecs = createSearchSpecifications(requestParams, searchMap);
        Specification<E> totalSpecification = filterSpecs.and(searchSpecs).and(additionalSpecification);
        return repository.findAll(totalSpecification, pageable).getContent();
    }

    protected Sort createSortParams(LinkedMultiValueMap<String, String> requestSortParams,
                                    Map<String, Sort> sortBy) {
        String sortParams = requestSortParams.getOrDefault(SORT_REQUEST_PARAM, List.of(ASC_BY_ID)).get(0);
        return Arrays.stream(sortParams.split(SUB_PARAM_DELIMITER))
                .map(String::trim)
                .map(sp -> (sp.startsWith(DESC_PREFIX)) ? sp : ASC_PREFIX.concat(sp))
                .distinct()
                .filter(sortBy::containsKey)
                .map(sortBy::get)
                .reduce(Sort::and)
                .orElse(sortBy.get(ASC_BY_ID));
    }

    protected <E extends AbstractEntity> Specification<E> createFilters(
            LinkedMultiValueMap<String, String> requestParams,
            Map<String, Function<String, Specification<E>>> filterBy) {
        return requestParams.entrySet()
                .stream()
                .distinct()
                .filter(param -> filterBy.containsKey(param.getKey()))
                .map(param -> filterBy.get(param.getKey()).apply(param.getValue().get(0).trim()))
                .reduce(Specification::and)
                .orElse(Specification.where(null));
    }

    protected <E extends AbstractEntity> Specification<E> createSearchSpecifications(
            LinkedMultiValueMap<String, String> requestParams,
            Map<String, Function<String, Specification<E>>> searchBy) {
        String searchParams = requestParams.getOrDefault(SEARCH_REQUEST_PARAM, List.of("")).get(0).trim();
        String[] searchParamsArray = searchParams.split(SUB_PARAM_DELIMITER);
        List<SearchParam> searchParamList = new SearchParam().parseAll(searchParamsArray);
        return searchParamList.stream()
                .distinct()
                .filter(param -> searchBy.containsKey(param.getFieldName()))
                .map(param -> searchBy.get(param.getFieldName()).apply(param.getSearchValue()))
                .reduce(Specification::and)
                .orElse(Specification.where(null));
    }

    protected static String createLikeQuery(String searchQuery) {
        return MessageFormat.format(SQL_LIKE_PATTERN, searchQuery);
    }
}
