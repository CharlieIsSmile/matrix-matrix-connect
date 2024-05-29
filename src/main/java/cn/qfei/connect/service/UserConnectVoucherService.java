package cn.qfei.connect.service;

import cn.qfei.connect.common.ContractVoucherParam;
import cn.qfei.connect.common.R;
import cn.qfei.connect.domain.UserConnectVoucher;
import cn.qfei.crm.response.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author L1729
* @description 针对表【user_connect_voucher(授权租户表)】的数据库操作Service
* @createDate 2024-05-22 11:06:43
*/
public interface UserConnectVoucherService extends IService<UserConnectVoucher> {

    R<List<UserConnectVoucher>> list(String tenantId,String userId);
    R add(ContractVoucherParam addVoucherParam) throws IOException;
    R update(ContractVoucherParam contractVoucher);
    R delete(Integer id);

    R<List<EntitiesRes>> getEnabledEntities(Integer voucherId);

   R<List<ViewListResp>> getViews(Integer voucherId,Integer entityId);

    R<List<ViewFieldQueryResp>> getFields(Integer voucherId, Integer entityId, Integer viewId);

    V2EntityDetailResp sysEntityDetail(Integer voucherId, Integer entityId);

    UserConnectVoucher selectOneByVoucherId(Integer voucherId);

    R<Map<String,Object>> getEntityData(Integer voucherId, Integer entityId, Integer viewId,Integer page,Integer pageSize);

}
