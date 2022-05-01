package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOPTYPE_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 李秉恩
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryType() {
        String key= CACHE_SHOPTYPE_KEY ;
        //1.从redis查询商铺缓存
        String typeListJson = stringRedisTemplate.opsForValue().get(key);
        //2.判断是否存在
        if(StrUtil.isNotBlank(typeListJson)){
            //3.存在，直接返回
            List<ShopType> typeList = JSONUtil.toList(typeListJson, ShopType.class);
            return Result.ok(typeList);
        }
        //4.不存在。根据id查询数据库
        List<ShopType> typeList = query().orderByAsc("sort").list();

        //5.数据库也不存在返回错误
        if (typeList == null) {
            return Result.fail("店铺类型不存在！");
        }
        //6.存在写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(typeList));
        //7.返回
        return Result.ok(typeList);

    }
}
