package top.wboost.common.es.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;

import top.wboost.common.es.entity.BaseEsIndex;

/**
 * ES基本查询实体类
 * @author jwSun
 * @date 2017年4月18日 上午11:56:22
 */
public class EsSearch extends BaseEsIndex {

    /**
     * 默认查询类型,查出来的size与用户指定相同
     */
    private SearchType searchType = SearchType.QUERY_THEN_FETCH;

    private Map<EsQueryType, Map<String, Set<String>>> queryMap = new HashMap<>();
    private Map<EsQueryType, List<QueryBuilder>> specialMap = new HashMap<>();

    {
        queryMap.put(EsQueryType.MUST, new HashMap<>());
        queryMap.put(EsQueryType.SHOULD, new HashMap<>());
        queryMap.put(EsQueryType.MUST_NOT, new HashMap<>());
        specialMap.put(EsQueryType.MUST, new ArrayList<>());
        specialMap.put(EsQueryType.SHOULD, new ArrayList<>());
        specialMap.put(EsQueryType.MUST_NOT, new ArrayList<>());
    }

    /**
     * should至少匹配数量
     */
    private Integer minimumNumberShouldMatch;
    /**
     * 查询分词时使用模式(ES默认OR 本类默认AND),一般情况无需修改
     */
    private Operator operator = Operator.AND;

    /**子文档MAP key:value  子文档名:子文档search**/
    private Map<String, EsSearch> childs = new LinkedHashMap<>();

    public EsSearch child(String type, EsSearch childSearch) {
        this.childs.put(type, childSearch);
        return this;
    }

    public Map<String, EsSearch> getChilds() {
        return this.childs;
    }

    public EsSearch(String index, String type, SearchType searchType) {
        super(index, type);
        this.searchType = searchType;
    }

    public EsSearch(String index, String type) {
        super(index, type);
    }

    public EsSearch must(String key, String val) {
        return this.must(key, val, Boolean.FALSE);
    }

    public EsSearch must(String key, String val, Boolean cover) {
        return putToMap(key, val, cover, getMustMap());
    }

    public SpecialBuilder special(EsQueryType esQueryType) {
        return new SpecialBuilder(esQueryType, this);
    }

    public static class SpecialBuilder {
        private EsQueryType esQueryType;
        private List<QueryBuilder> queryBuilders;

        protected void initSpecial(EsSearch esSearch) {
            if (esSearch.specialMap == null) {
                esSearch.specialMap = new HashMap<>();
            }
            if (!esSearch.specialMap.containsKey(esQueryType)) {
                this.queryBuilders = new ArrayList<>();
                esSearch.specialMap.put(esQueryType, this.queryBuilders);
            } else {
                this.queryBuilders = esSearch.specialMap.get(esQueryType);
            }
        }

        public SpecialBuilder(EsQueryType esQueryType, EsSearch esSearch) {
            super();
            this.esQueryType = esQueryType;
            initSpecial(esSearch);
        }

        public SpecialBuilder fuzzy(String key, Object val) {
            this.queryBuilders.add(QueryBuilders.fuzzyQuery(key, val));
            return this;
        }

        public SpecialBuilder regexp(String key, String regexp) {
            this.queryBuilders.add(QueryBuilders.regexpQuery(key, regexp));
            return this;
        }
    }

    protected EsSearch putToMap(String key, String val, Boolean cover, Map<String, Set<String>> map) {
        if (map.get(key) == null || cover) {
            Set<String> set = new HashSet<String>();
            set.add(val);
            map.put(key, set);
        } else {
            map.get(key).add(val);
        }
        return this;
    }

    public EsSearch mustAll(Map<String, Set<String>> mustMap) {
        getMustMap().putAll(mustMap);
        return this;
    }

    public EsSearch mustNot(String key, String val) {
        return this.mustNot(key, val, Boolean.FALSE);
    }

    public EsSearch mustNot(String key, String val, Boolean cover) {
        return putToMap(key, val, cover, getMustNotMap());
    }

    public EsSearch mustNotAll(Map<String, Set<String>> mustMap) {
        getMustNotMap().putAll(mustMap);
        return this;
    }

    public EsSearch should(String key, String val) {
        return this.should(key, val, Boolean.FALSE);
    }

    public EsSearch should(String key, String val, Boolean cover) {
        return putToMap(key, val, cover, getShouldMap());
    }

    public EsSearch shouldAll(Map<String, Set<String>> shouldMap) {
        getShouldMap().putAll(shouldMap);
        return this;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public Map<String, Set<String>> getMustMap() {
        return this.queryMap.get(EsQueryType.MUST);
    }

    public Map<String, Set<String>> getMustNotMap() {
        return this.queryMap.get(EsQueryType.MUST_NOT);
    }

    public Map<String, Set<String>> getShouldMap() {
        return this.queryMap.get(EsQueryType.SHOULD);
    }

    public Map<EsQueryType, List<QueryBuilder>> getSpecialMap() {
        return this.specialMap;
    }

    public Integer getMinimumNumberShouldMatch() {
        return minimumNumberShouldMatch;
    }

    public void setMinimumNumberShouldMatch(Integer minimumNumberShouldMatch) {
        this.minimumNumberShouldMatch = minimumNumberShouldMatch;
    }

    /**
     * @Description 根据参数增加最小匹配数，若minimumNumberShouldMatch为空则初始化并为参数i
     * @param i 增加最小匹配数
     */
    public void addMinimumNumberShouldMatch(int i) {
        if (this.minimumNumberShouldMatch == null) {
            this.minimumNumberShouldMatch = i;
        } else {
            this.minimumNumberShouldMatch += i;
        }
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

}
