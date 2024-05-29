package cn.qfei.connect.service.impl;


import cn.qfei.connect.common.ContractVoucherParam;
import cn.qfei.connect.common.IdResult;
import cn.qfei.connect.common.R;
import cn.qfei.connect.common.SystemConstant;
import cn.qfei.connect.domain.UserConnectVoucher;
import cn.qfei.connect.service.UserConnectVoucherService;
import cn.qfei.connect.mapper.UserConnectVoucherMapper;
import cn.qfei.crm.client.CrmClient;
import cn.qfei.crm.request.*;
import cn.qfei.crm.response.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author L1729
* @description 针对表【user_connect_voucher(授权租户表)】的数据库操作Service实现
* @createDate 2024-05-22 11:06:43
*/
@Service
@Slf4j
public class UserConnectVoucherServiceImpl extends ServiceImpl<UserConnectVoucherMapper, UserConnectVoucher>
    implements UserConnectVoucherService{

    @Resource
    private UserConnectVoucherMapper voucherMapper;



    @Override
    public R<List<UserConnectVoucher>> list(String tenantId,String userId) {


        List<UserConnectVoucher> userConnectVouchers = voucherMapper.selectList(new QueryWrapper<UserConnectVoucher>().eq("is_deleted", 0));
        if(Objects.isNull(userConnectVouchers)){
            return R.data(new ArrayList<>());
        }
        List<String> voucherDatas = userConnectVouchers.stream().map(UserConnectVoucher::getVoucher).collect(Collectors.toList());
        List<String> vouchers = voucherDatas.stream().distinct().collect(Collectors.toList());
        //调用校验接口，修改凭证状态
        CrmClient client = CrmClient.getClient(null,null);
        CheckVoucherReq req = CheckVoucherReq.builder().vouchers(vouchers).build();
        List<CheckVoucherResp> check = client.voucher().check(req);
        if(Objects.nonNull(check)){
            check.forEach(checkVoucherResp -> {
                List<UserConnectVoucher> voucherList = voucherMapper.selectList(new QueryWrapper<UserConnectVoucher>().eq("voucher", checkVoucherResp.getVoucher())
                        .eq("is_deleted", 0));
                if(!CollectionUtils.isEmpty(voucherList)){
                    voucherList.forEach(voucher -> {
                        voucher.setOrgId(checkVoucherResp.getOrgId());
                        voucher.setStatus(checkVoucherResp.getVoucherStatus().equals("1"));
                        voucherMapper.updateById(voucher);
                    });
                }
            });
        }

        LambdaQueryWrapper<UserConnectVoucher> queryWrapper = Wrappers.<UserConnectVoucher>lambdaQuery()
                .eq(UserConnectVoucher::getTenantId, tenantId)
                .eq(UserConnectVoucher::getIsDeleted,false);

        //TODO --飞书正式上线时打开
//        if(!StringUtils.isBlank(userId)){
//            queryWrapper.eq(UserConnectVoucher::getUserId,userId);
//        }

        List<UserConnectVoucher> voucherList = voucherMapper.selectList(queryWrapper);
        return R.data(voucherList);

    }

    @Override
    public R add(ContractVoucherParam addVoucherParam) throws IOException {

        if(StringUtils.isEmpty(addVoucherParam.getVoucher())){
            return R.fail("凭证不能为空");
        }

        if(StringUtils.isEmpty(addVoucherParam.getUserName())){
            return R.fail("名称不能为空");
        }

        UserConnectVoucher one = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("user_name", addVoucherParam.getUserName())
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.nonNull(one)){
            return R.fail("名称已存在");
        }

        //校验凭证是否有效
        CrmClient client = CrmClient.getClient(null,null);
        List<String> vouchers = new ArrayList<>();
        vouchers.add(addVoucherParam.getVoucher());
        CheckVoucherReq req = CheckVoucherReq.builder().vouchers(vouchers).build();
        List<CheckVoucherResp> check = client.voucher().check(req);
        if(Objects.isNull(check)){
            return R.fail("凭证无效");
        }
        if(check.get(0).getVoucherStatus().equals("2")){
            return R.fail("租户到期，请联系烽火工作人员");
        }
        if(!check.get(0).getVoucherStatus().equals("1")){
            return R.fail("凭证无效");
        }

        UserConnectVoucher voucher = new UserConnectVoucher();
        BeanUtils.copyProperties(addVoucherParam,voucher);
        voucher.setOrgId(check.get(0).getOrgId());
        voucher.setStatus(true);
        voucher.setCreateTime(new Date());
        voucher.setIsDeleted(SystemConstant.isDeletedNo);
        int insert = voucherMapper.insert(voucher);
        if(insert>0){
            return R.data(IdResult.builder().id(voucher.getId()).build());
        }
        return R.fail("新增失败");
    }

    @Override
    public R update(ContractVoucherParam contractVoucher) {

        if(null == contractVoucher.getId()){
            return R.fail("id不能为空");
        }

        if(StringUtils.isEmpty(contractVoucher.getVoucher())){
            return R.fail("凭证不能为空");
        }

        if(StringUtils.isEmpty(contractVoucher.getUserName())){
            return R.fail("名称不能为空");
        }

        UserConnectVoucher one = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("user_name", contractVoucher.getUserName())
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.nonNull(one)&&!one.getId().equals(contractVoucher.getId())){
            return R.fail("名称已被其它租户使用");
        }
        //校验凭证是否有效
        CrmClient client = CrmClient.getClient(null,null);
        List<String> vouchers = new ArrayList<>();
        vouchers.add(contractVoucher.getVoucher());
        CheckVoucherReq req = CheckVoucherReq.builder().vouchers(vouchers).build();
        List<CheckVoucherResp> check = client.voucher().check(req);
        if(Objects.isNull(check)){
            return R.fail("凭证无效");
        }
        if(!check.get(0).getVoucherStatus().equals("1")){
            return R.fail("凭证无效");
        }

        UserConnectVoucher voucher = new UserConnectVoucher();
        BeanUtils.copyProperties(contractVoucher,voucher);
        voucher.setUpdateTime(new Date());
        int update = voucherMapper.updateById(voucher);
        if(update>0){
            return R.success();
        }
        return R.fail("更新失败");
    }

    @Override
    public R delete(Integer id) {
        UserConnectVoucher userConnectVoucher = voucherMapper.selectById(id);
        userConnectVoucher.setUpdateTime(new Date());
        userConnectVoucher.setIsDeleted(SystemConstant.isDeletedYes);
        int update = voucherMapper.updateById(userConnectVoucher);
        if(update>0){
            return R.success();
        }
        return R.fail("删除失败");
    }

    @Override
    public R<List<EntitiesRes>> getEnabledEntities(Integer voucherId) {
        if(null==voucherId){
            return R.fail("请求参数不能为空");
        }
        UserConnectVoucher ucv = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("id", voucherId)
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.isNull(ucv)){
            return R.fail("凭证无效");
        }

        try {

            CrmClient client1 = CrmClient.getClient(ucv.getOrgId(),null);
            NoParamReq req1 = NoParamReq.builder().build();
            List<EntitiesRes> entities = client1.entity().entities(req1);
            return R.data(entities);

        } catch (Exception e) {
            log.info("getEnabledEntities日志打印异常！ 异常信息：" + e);
        }

        return R.data(new ArrayList<>());
    }

    @Override
    public R<List<ViewListResp>> getViews(Integer voucherId, Integer entityId) {
        if(null==voucherId){
            return R.fail("请求参数不能为空");
        }
        UserConnectVoucher ucv = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("id", voucherId)
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.isNull(ucv)){
            return R.fail("凭证无效");
        }

        try {

            CrmClient client1 = CrmClient.getClient(ucv.getOrgId(),null);
            ViewListReq viewListReq = ViewListReq.builder().entityType(entityId).page(1).pageSize(10000).build();
            List<ViewListResp> viewList = client1.view().list(viewListReq);
            return R.data(viewList);

        } catch (Exception e) {
            log.info("getViews日志打印异常！ 异常信息：" + e);
        }

        return R.data(new ArrayList<>());
    }

    @Override
    public R<List<ViewFieldQueryResp>> getFields(Integer voucherId, Integer entityId, Integer viewId) {
        if(null==voucherId){
            return R.fail("请求参数不能为空");
        }
        UserConnectVoucher ucv = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("id", voucherId)
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.isNull(ucv)){
            return R.fail("凭证无效");
        }

        try {

            CrmClient client1 = CrmClient.getClient(ucv.getOrgId(),null);
            ViewFieldQueryReq queryReq = ViewFieldQueryReq.builder().viewId(Long.valueOf(viewId)).entity(entityId).build();
            List<ViewFieldQueryResp> field = client1.view().field(queryReq);
            return R.data(field);

        } catch (Exception e) {
            log.info("getFields日志打印异常！ 异常信息：" + e);
        }

        return R.data(new ArrayList<>());
    }

    @Override
    public V2EntityDetailResp sysEntityDetail(Integer voucherId, Integer entityId) {
        if(null==voucherId){
            throw new RuntimeException("请求参数不能为空");
        }
        UserConnectVoucher ucv = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("id", voucherId)
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.isNull(ucv)){
            throw new RuntimeException("凭证无效");
        }
        try {
            CrmClient client = CrmClient.getClient(ucv.getOrgId(),null);
            V2EntityDetailReq entityDetailReq = V2EntityDetailReq.builder().entityType(entityId).build();
            return client.entity().v2().detail(entityDetailReq);
        }catch (Exception e){
            log.info("sysEntityDetail日志打印异常！ 异常信息：" + e);
        }
        return null;
    }

    @Override
    public UserConnectVoucher selectOneByVoucherId(Integer voucherId) {
        return voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("id", voucherId)
                .eq("is_deleted", SystemConstant.UserYes));
    }

    @Override
    public R<Map<String,Object>> getEntityData(Integer voucherId, Integer entityId, Integer viewId,Integer page,Integer pageSize) {

        if(null==voucherId){
            return R.fail("请求参数不能为空");
        }
        UserConnectVoucher ucv = voucherMapper.selectOne(new QueryWrapper<UserConnectVoucher>().eq("id", voucherId)
                .eq("is_deleted", SystemConstant.UserYes));
        if(Objects.isNull(ucv)){
            return R.fail("凭证无效");
        }

        try {

            CrmClient client1 = CrmClient.getClient(ucv.getOrgId(),null);
            V2EntityPageReq build = V2EntityPageReq.builder().entityId(entityId.longValue()).viewId(viewId.longValue()).page(page).pageSize(pageSize).build();
            Map<String, Object> page1 = client1.entity().v2().view(build);
            return R.data(page1);

        } catch (Exception e) {
            log.info("getEntityData日志打印异常！ 异常信息：" + e);
        }

        return R.data(new HashMap<>());
    }

}




