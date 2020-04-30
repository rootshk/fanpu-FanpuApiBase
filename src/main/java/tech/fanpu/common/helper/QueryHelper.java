package tech.fanpu.common.helper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.fanpu.bo.GridBo;
import tech.fanpu.vo.GridParamsVo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class QueryHelper {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    EntityManagerFactory emFactory;

    /**
     * 自定义搜索
     *
     * @param hql         自定义hql语句
     * @param name        列名，可自定义构造
     * @param order       排序
     * @param map         　模糊条件 or like '%%'
     * @param fixeds      固定条件 and xxx=xxx
     * @param custom      自定义条件，一般搭配着 hql　一起使用
     * @param resultClass 返回结果类型
     * @param begin       开始条数
     * @param size        　偏移多少条数据
     * @return
     */
    @SuppressWarnings("all")
    public Map<String, Object> queryHql(String hql, String name, String order, Map<String, Object> map, Map<String, Object> fixeds, Map<String, Object> custom, Class resultClass, Integer begin, Integer size) {
        String whereFixed = "";
        String where = "";

        Map<String, Object> resultMap = new HashMap<String, Object>();
        EntityManager em = null;
        try {
            if (fixeds != null) {
                for (String key : fixeds.keySet()) {
                    whereFixed += String.format(" %s=:%s and", key, key.replace(".", ""));
                }
                if (StringUtils.isNotBlank(whereFixed)) {
                    hql += (hql.indexOf("where") == -1 ? " where " : " and ") + whereFixed.substring(0, whereFixed.length() - 3);
                }
            }
            if (map != null) {
                for (String key : map.keySet()) {
                    where += String.format(" %s like :%s or", key, key.replace(".", ""));
                }
                if (StringUtils.isNotBlank(where)) {
                    hql += (hql.indexOf("where") == -1 ? " where " : " and ") + " (" + where.substring(0, where.length() - 2) + ")";
                }
            }
            em = emFactory.createEntityManager();
            String sql = hql + (StringUtils.isNotBlank(order) ? " order by " + order : "");
            if (name != null) {
                sql = "select " + name + " " + sql;
            }
            Query result = em.createQuery(sql, resultClass);
            Query count = em.createQuery("select count(1) " + hql);
            if (map != null) {
                Set<Map.Entry<String, Object>> sets = map.entrySet();
                for (Map.Entry<String, Object> m : sets) {
                    result.setParameter(m.getKey().replace(".", ""), "%" + m.getValue() + "%");
                    count.setParameter(m.getKey().replace(".", ""), "%" + m.getValue() + "%");
                }
            }
            if (fixeds != null) {
                Set<Map.Entry<String, Object>> sets = fixeds.entrySet();
                for (Map.Entry<String, Object> m : sets) {
                    result.setParameter(m.getKey().replace(".", ""), m.getValue());
                    count.setParameter(m.getKey().replace(".", ""), m.getValue());
                }
            }
            if (custom != null) {
                Set<Map.Entry<String, Object>> sets = custom.entrySet();
                for (Map.Entry<String, Object> m : sets) {
                    result.setParameter(m.getKey(), m.getValue());
                    count.setParameter(m.getKey(), m.getValue());
                }
            }
            result.setFirstResult(begin);
            result.setMaxResults(size);
            resultMap.put("count", Long.valueOf(count.getSingleResult().toString()));
            resultMap.put("result", result.getResultList());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("QueryHelper Error:", e);
            resultMap.put("count", 0L);
            resultMap.put("result", null);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            return resultMap;
        }
    }

    @SuppressWarnings("all")
    public Map<String, Object> queryHql(String hql, String order, Map<String, Object> map, Map<String, Object> fixeds, Map<String, Object> custom, Class resultClass, Integer begin, Integer size) {
        return queryHql(hql, null, order, map, fixeds, custom, resultClass, begin, size);
    }

    @SuppressWarnings("all")
    public Map<String, Object> queryHql(String hql, String order, Map<String, Object> map, Map<String, Object> fixeds, Class resultClass, Integer begin, Integer size) {
        return queryHql(hql, null, order, map, fixeds, null, resultClass, begin, size);
    }

    /**
     * 适配查询grid数据
     *
     * @param params
     * @param resultClass
     * @param fixeds      固定条件
     * @param searchNames
     * @return
     */
    public GridBo queryGrid(GridParamsVo params, String hql, String selectName, Class resultClass, Map<String, Object> fixeds, Map<String, Object> custom, String... searchNames) {
        Map<String, Object> map = null;
        if (StringUtils.isNotBlank(params.getSearch())) {
            map = new HashMap<>();
            for (String name : searchNames) {
                map.put(name, params.getSearch());
            }
        }
        String order = null;
        if (StringUtils.isNotBlank(params.getOrderBy())) {
            order = params.getOrderBy() + " " + params.getSort();
        }
        Map<String, Object> result = queryHql(hql != null ? hql : " from " + resultClass.getSimpleName(), selectName, order, map, fixeds, custom, resultClass, params.getBegin(), params.getEnd() - params.getBegin());
        Long count = Long.valueOf(result.get("count").toString());
        return new GridBo(params.getDraw(), count, count, result.get("result"));
    }

    public GridBo queryGrid(GridParamsVo params, String hql, String selectName, Class resultClass, Map<String, Object> fixeds, String... searchNames) {
        return queryGrid(params, hql, selectName, resultClass, fixeds, null, searchNames);
    }

    public GridBo queryGrid(GridParamsVo params, Class resultClass, Map<String, Object> fixeds, String... searchNames) {
        return queryGrid(params, null, null, resultClass, fixeds, null, searchNames);
    }

    public GridBo queryGrid(GridParamsVo params, String hql, Class resultClass, String selectName) {
        return queryGrid(params, hql, selectName, resultClass, null, null, new String[0]);
    }

}
