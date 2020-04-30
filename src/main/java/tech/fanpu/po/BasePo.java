package tech.fanpu.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * @Title
 * @Package tech.fanpu.po.system
 * @Author porridge
 * @Date 2019-07-29 19:11
 * @Version V1.0
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BasePo implements Serializable {

    private static final long serialVersionUID = 5035956426383124711L;

    @ApiModelProperty("创建时间")
    @CreatedDate
    private Date createdAt;

    @ApiModelProperty("上一次修改时间")
    @LastModifiedDate
    private Date updatedAt;

    public BasePo () {

    }

    public BasePo (Date createdAt, Date updatedAt) {
        super();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
