package com.datastorage.service.util;

import com.datastorage.models.basicalmodels.basicalconstants.IpServiceConstant;
import com.datastorage.models.basicalmodels.basicaldo.IpPoolMainDO;
import com.datastorage.models.basicalmodels.basicaldto.responsedto.IpPoolMainDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
public class ModelConvertUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ModelConvertUtil.class);

    /**
     * 固定格式的集合
     * 0 -> ip
     * 1 -> port
     * 2 -> ip地址
     * 3 -> ip供应商
     * 4 -> 是否支持https
     * 5 -> 是否支持post请求
     * 6 -> 匿名程度
     * 7 -> 速度
     * 8 -> 网站检测 ip入库时间
     * 9 -> 网站检测 ip最后有效时间
     * @param params
     * @return
     */
    //这边一个方法就应该create好 所有list controller 接受参数的时候接受一个数组 并 标记好每个对象的值
    public List<IpPoolMainDO> createIpPoolMainDOInstance(List<IpPoolMainDTO> params) {
        List<IpPoolMainDO> postModel = new LinkedList<>();
        for (IpPoolMainDTO ipPoolMainDTO : params) {
            try {
                String dataID = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                byte statusCode = IpServiceConstant.USE_CODE;
                Date dataInsertTime = new Date();
                Date dataDetectTime = null;
                postModel.add(new IpPoolMainDO(
                        dataID,
                        ipPoolMainDTO.getIpNum(),
                        ipPoolMainDTO.getIpPort(),
                        ipPoolMainDTO.getIpLocation(),
                        ipPoolMainDTO.getIpVendor(),
                        ipPoolMainDTO.getSupportHttps(),
                        ipPoolMainDTO.getSupportPost(),
                        ipPoolMainDTO.getAnonymityDegree(),
                        ipPoolMainDTO.getAccessSpeed(),
                        ipPoolMainDTO.getInsertTime(),
                        ipPoolMainDTO.getLastDetectTime(),
                        statusCode,
                        dataInsertTime,
                        dataDetectTime
                ));
            } catch (Exception e) {
                LOG.error(" combine IpPoolMainDO error , message : {} ", e.getMessage());
            }
        }
        return postModel;
    }
}
