package top.wboost.test.es;

import top.wboost.common.base.page.BasePage;
import top.wboost.common.es.search.EsSearch;
import top.wboost.common.es.util.EsQueryUtil;

/**
 * @Auther: jwsun
 * @Date: 2019/1/16 15:27
 */
public class SearchTest {

    public static void main(String[] args) {
        EsSearch esScrollSearch = new EsSearch("test_abc", "abc");
        System.out.println(EsQueryUtil.querySimpleList(esScrollSearch, new BasePage(1, 12345)).getResultList().size());
        /*10*/
        /*StringBuffer sb = new StringBuffer();
        for (int k = 0; k < 100; k++) {
            EsPut esPut = new EsPut("test_abc", "abc");
            for (int i = 0; i < 100 && k<100; i++,k++) {
                sb.setLength(0);
                sb.append(k);
                esPut.setPutMap(new QuickHashMap<String, Object>().quickPut("CAR_NUM", sb.toString())
                        .quickPut("CREATE_TIME", System.currentTimeMillis()));
            }
            System.out.println(k);
            EsChangeUtil.putToIndex(esPut);
        }*/

    }

}
