package top.wboost.common.util;

import java.util.*;

/**
 * 集合工具类
 * @ClassName: CollectionUtil
 * @author sjw
 * @date 2016年9月12日 上午10:44:06
 */
public class CollectionUtil {

    @SuppressWarnings("unchecked")
    public static final Map<String, Object> EMPTY_MAP = Collections.EMPTY_MAP;

    /**
     * 判断是否为空
     * @param collection 集合
     * @return Boolean
     */
    public static <T> Boolean isEmpty(Collection<T> collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 集合内容过滤
     * @date 2017年5月27日 下午3:52:06
     * @param beConvertList 未过滤集合
     * @return
     */
    public static <T> CollectionDataFilter<T> createFilter(List<T> beConvertList) {
        return new CollectionDataFilter<T>(beConvertList);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    /**
     * 根据总段数分割集合
     *
     * @param calculateResultList
     * @param clazz
     * @param splitNum            总段数
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitCollectionBySplitNum(Collection<T> calculateResultList, Class<T> clazz, int splitNum) {
        List<List<T>> calculateApiResults = new ArrayList<>();
        int once = calculateResultList.size() / (splitNum - 1);
        if (once == 0) {
            calculateApiResults.add(new ArrayList<>(calculateResultList));
        } else {
            int now = 0;
            while (now <= calculateResultList.size()) {
                calculateApiResults.add(new ArrayList<>(calculateResultList).subList(now, now + once > calculateResultList.size() ? calculateResultList.size() : now + once));
                now += once;
            }
        }
        return calculateApiResults;
    }

    /**
     * 根据总集合数分割集合
     *
     * @param calculateResultList
     * @param clazz
     * @param oneCollectionNum    单个集合最大个数
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitCollectionByNum(Collection<T> calculateResultList, Class<T> clazz, int oneCollectionNum) {
        List<List<T>> calculateApiResults = new ArrayList<>();
        int once = oneCollectionNum;
        if (calculateResultList.size() <= oneCollectionNum) {
            calculateApiResults.add(new ArrayList<>(calculateResultList));
        } else {
            int now = 0;
            while (now <= calculateResultList.size()) {
                calculateApiResults.add(new ArrayList<>(calculateResultList).subList(now, now + once > calculateResultList.size() ? calculateResultList.size() : now + once));
                now += once;
            }
        }
        return calculateApiResults;
    }

}
