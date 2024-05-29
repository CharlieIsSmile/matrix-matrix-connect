package cn.qfei.connect.controller;

import cn.qfei.connect.common.ContractVoucherParam;
import cn.qfei.connect.common.R;
import cn.qfei.connect.domain.UserConnectVoucher;
import cn.qfei.connect.service.UserConnectVoucherService;
import cn.qfei.crm.response.EntitiesRes;
import cn.qfei.crm.response.V2EntityDetailResp;
import cn.qfei.crm.response.ViewFieldQueryResp;
import cn.qfei.crm.response.ViewListResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 授权租户控制器
 */
@RestController
@RequestMapping("/voucher")
@Slf4j
public class ConnectVoucherController {

    @Resource
    private UserConnectVoucherService voucherService;

    /**
     * 查询
     * @param tenantId
     * @param
     * @return
     */
    @GetMapping("list")
    public R<List<UserConnectVoucher>> list(@RequestParam(name = "tenantId",required = true) String tenantId,
                                            @RequestParam(name = "userId",required = false) String userId) {
        return voucherService.list(tenantId,userId);
    }

    /**
     * 新增
     * @param addVoucherParam
     * @return
     */
    @PostMapping("/add")
    public R add(@Valid @RequestBody ContractVoucherParam addVoucherParam) throws IOException {
        return voucherService.add(addVoucherParam);
    }

    /**
     * 更新
     * @param contractVoucher
     * @return
     */
    @PostMapping("/update")
    public R update(@Valid @RequestBody ContractVoucherParam contractVoucher) {
        return voucherService.update(contractVoucher);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public R delete(@RequestParam(name = "id") Integer id) {
        return voucherService.delete(id);
    }

    /**
     * 对象列表
     * @param
     * @return
     */
    @GetMapping("/getEntities")
    public R<List<EntitiesRes>> getEnabledEntities(@RequestParam(name = "voucherId") Integer voucherId) {
        return voucherService.getEnabledEntities(voucherId);
    }

    /**
     * 视图列表
     * @param
     * @param entityId
     * @return
     */
    @GetMapping("/getViews")
    public R<List<ViewListResp>> getViews(@RequestParam(name = "voucherId") Integer voucherId,
                                          @RequestParam(name = "entityId") Integer entityId) {
        return voucherService.getViews(voucherId, entityId);
    }

    /**
     * 字段列表
     * @param
     * @param entityId
     * @param viewId
     * @return
     */
    @GetMapping("/getFields")
    public R<List<ViewFieldQueryResp>> getFields(@RequestParam(name = "voucherId") Integer voucherId,
                                                 @RequestParam(name = "entityId") Integer entityId,
                                                 @RequestParam(name = "viewId") Integer viewId) {
        return voucherService.getFields(voucherId, entityId, viewId);
    }

    /**
     * 系统实体详情
     * @param voucherId
     * @param entityId
     * @return
     */
    @GetMapping("/sysEntityDetail")
    public V2EntityDetailResp sysEntityDetail(@RequestParam(name = "voucherId") Integer voucherId,
                                              @RequestParam(name = "entityId") Integer entityId) {
        return voucherService.sysEntityDetail(voucherId, entityId);
    }

    /**
     * 获取实体数据
     * @param voucherId
     * @param entityId
     * @param viewId
     * @param page
     * @return
     */
    @GetMapping("/getEntityData")
    public R<Map<String,Object>> getEntityData(@RequestParam(name = "voucherId") Integer voucherId,
                                               @RequestParam(name = "entityId") Integer entityId,
                                               @RequestParam(name = "viewId") Integer viewId,
                                               @RequestParam(name = "page") Integer page,
                                               @RequestParam(name = "pageSize") Integer pageSize) {
        return voucherService.getEntityData(voucherId, entityId, viewId, page,pageSize);
    }


}
