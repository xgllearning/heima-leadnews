package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)//默认只对RuntimeException有效,根据阿里规范需要对全部异常回滚
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
//面向接口编程,上传minio的组件
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //1.检查参数是否携带
        if(multipartFile!=null && multipartFile.getSize()==0){
            //此时参数无效,抛出自定义异常
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.参数合法后,获取当前用户id
        Integer userId = WmThreadLocalUtil.getUserId();
        if (userId==null){
            //说明登录出现问题,此时ThreadLocal没有用户信息
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        //3.此时参数合法,用户登录,将图片上传到minio中,需要将组件注入(添加依赖)
        //4.参数二是上传到minio后文件的名字
        //5.原始文件名
        String originalFilename = multipartFile.getOriginalFilename();
        //6.使用UUID重新生成文件名，防止文件名称重复造成文件覆盖，使用uuid产生的随机名可能会有-，因此需要把-替换为空replace("-","");
        String fileName = UUID.randomUUID().toString().replace("-","");;//dfsdfdfd.jpg
        //7.截取后缀，包括后缀.
        //8.注意：截取后缀后要记得进行处理，用户可能传递的没有后缀或者后缀不对，如果没有后缀originalFilename.lastIndexOf(".")返回的是-1
        //即需要进行判断
        if(originalFilename.lastIndexOf(".")!=-1){
            //9.此时就是存在.的时候，截取后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //10.此时取到的是符合的后缀，使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
            fileName = fileName + suffix;//dfsdfdfd.xxx
        }
        //11.执行上传文件操作，并进行异常处理
        String filePath=null;
        try {
            filePath = fileStorageService.uploadImgFile("", fileName, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}",filePath);
        } catch (IOException e) {
            //e.printStackTrace(); //打印在控制台
            log.error("上传文件失败",e);
            //12.抛出异常作用：1.事务回滚 2.通过全局异常处理器返回前端错误提示
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        //13.将用户信息和路径保存到数据库,为了代码健壮性，捕获异常处理
        try {
            WmMaterial wmMaterial = new WmMaterial();
            wmMaterial.setUserId(userId);
            wmMaterial.setUrl(filePath);
            wmMaterial.setIsCollection((short)0);
            wmMaterial.setType((short)0);
            wmMaterial.setCreatedTime(new Date());
            this.save(wmMaterial);
            //14.保存成功
            return ResponseResult.okResult(wmMaterial);
        } catch (Exception e) {
            //15.保存失败，删除图片
            fileStorageService.delete(filePath);
            throw e;
        }
    }
    /**
     * 素材列表查询
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmMaterialDto dto) {
        //1.获取当前用户id,验证用户是否登录
        Integer userId = WmThreadLocalUtil.getUserId();
        if (userId==null){//登录出现问题,抛出异常
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }
        //2.校验参数合法,不合法则设置初值
        dto.checkParam();
        //3.构造查询条件,此时是查询全部的条件
        LambdaQueryWrapper<WmMaterial> queryWrapper = Wrappers.<WmMaterial>lambdaQuery()
                //按照用户名查询
                .eq(WmMaterial::getUserId, userId)
                //根据时间倒叙显示
                .orderByDesc(WmMaterial::getCreatedTime);
        //4.判断是不是查询收藏图片
        if (dto.getIsCollection()!=null&&dto.getIsCollection()==1){//此时为查询收藏
            queryWrapper.eq(WmMaterial::getIsCollection,dto.getIsCollection());
        }
        //5.分页查询
        IPage<WmMaterial> pageParam = new Page<>(dto.getPage(),dto.getSize());
        IPage<WmMaterial> page = page(pageParam, queryWrapper);
        //6.构造返回
        ResponseResult pageResponseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        pageResponseResult.setData(page.getRecords());
        return pageResponseResult;
    }
}
