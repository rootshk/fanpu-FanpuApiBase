package tech.fanpu.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.transaction.annotation.Transactional;
import tech.fanpu.bo.GridBo;
import tech.fanpu.business.BaseBusiness;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.helper.QueryHelper;
import tech.fanpu.common.util.SystemUtil;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;
import tech.fanpu.vo.GridParamsVo;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @Title 基础逻辑实现类
 * @Package com.wecreditlife.business.impl
 * @Author porridge
 * @Date 2019-08-07 20:50
 * @Version V1.0
 */
public class BaseBusinessImpl<T> implements BaseBusiness<T> {
    @Autowired
    protected JpaRepository<T, Long> repository;
    @Resource
    QueryHelper queryHelper;

    @Override
    public T findById(Long id) {
        if (id == null) {
            return null;
        }
        return repository.findById(id).orElse(null);
    }

    @Override
    public T findByIdThrow(Long id, Supplier<SystemException> exceptionSupplier) {
        if (id == null) {
            return null;
        }
        return repository.findById(id).orElseThrow(exceptionSupplier);
    }

    @Override
    public T findByIdThrow(Long id) {
        Supplier supplier = () -> new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.RESOURCE_NOT_FOUND, ServiceCode.RESOURCE_NOT_FOUND_DEFAULT_MSG);
        return (T) findByIdThrow(id, supplier);
    }

    @Override
    public T saveOrUpdate(T entity, Long id) {
        if (id != null) {
            Supplier supplier = () -> new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.RESOURCE_NOT_FOUND, ServiceCode.RESOURCE_NOT_FOUND_DEFAULT_MSG);
            Object oldObj = findByIdThrow(id, supplier);
            SystemUtil.mergeBean(entity, oldObj);
        }
        return repository.save(entity);
    }

    /**
     * 调用某一个方法
     *
     * @param funName
     * @param object
     */
    private Object callFunction(String funName, Object object, List<Class<?>> parameterTypes, Object... obj) {
        try {
            Method method = parameterTypes == null ? object.getClass().getMethod(funName) : object.getClass().getMethod(funName, parameterTypes.toArray(new Class<?>[]{}));
            method.setAccessible(true);
            return method.invoke(object, obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.SERVER_ERROR, "setDateValue失败");
        }
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = () -> new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.RESOURCE_NOT_FOUND, ServiceCode.RESOURCE_NOT_FOUND_DEFAULT_MSG);
        repository.delete((T) findByIdThrow(id, supplier));
    }

    private Class<?> getEntityClass() {
        RepositoryMetadata metadata = AbstractRepositoryMetadata.getMetadata(repository.getClass().getInterfaces()[0]);
        return metadata.getDomainType();
    }

    @Override
    @Transactional(readOnly = true)
    public GridBo list(GridParamsVo params, Map<String, Object> fixeds, String... searchNames) {
        Class<?> entityClazz = getEntityClass();
        return queryHelper.queryGrid(params, entityClazz, fixeds, searchNames);
    }

    @Override
    @Transactional(readOnly = true)
    public GridBo list(GridParamsVo params, String... searchNames) {
        return list(params, null, searchNames);
    }


    @Override
    @Transactional(readOnly = true)
    public List<T> all(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public List<T> all() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> list(Sort sort, Integer limit) {
        return repository.findAll(PageRequest.of(0, limit, sort)).getContent();
    }

}
