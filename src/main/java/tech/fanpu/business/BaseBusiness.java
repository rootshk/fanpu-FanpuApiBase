package tech.fanpu.business;

import org.springframework.data.domain.Sort;
import tech.fanpu.bo.GridBo;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.vo.GridParamsVo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @Title 基础逻辑类
 * @Package com.wecreditlife.business
 * @Author porridge
 * @Date 2019-08-07 20:48
 * @Version V1.0
 */
public interface BaseBusiness<T> {

    /**
     * 查询
     *
     * @param id id
     * @return T
     */
    T findById(Long id);

    /**
     * 查询, 无则抛异常
     *
     * @param id                id
     * @param exceptionSupplier 异常
     * @return T
     */
    T findByIdThrow(Long id, Supplier<SystemException> exceptionSupplier);

    /**
     * 查询，无则抛出默认异常
     *
     * @param id
     * @return
     */
    T findByIdThrow(Long id);

    /**
     * 保存或修改
     *
     * @param entity
     * @param key
     * @return
     */
    T saveOrUpdate(T entity, Long key);

    /**
     * 删除
     *
     * @param id id
     */
    void delete(Long id);

    /**
     * 查询列表
     *
     * @param params      参数
     * @param searchNames 搜索条件
     * @return 分页结果
     */
    GridBo list(GridParamsVo params, Map<String, Object> fixeds, String... searchNames);

    GridBo list(GridParamsVo params, String... searchNames);

    /**
     * 查询全部
     *
     * @param sort
     * @return
     */
    List<T> all(Sort sort);

    List<T> all();

    /**
     * 批量查询数据
     *
     * @param sort  排序
     * @param limit 查询多少条数据
     * @return
     */
    List<T> list(Sort sort, Integer limit);
}
