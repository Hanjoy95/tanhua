package com.zhj.tanhua.server.service;

import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.recommend.api.RecommendUserApi;
import com.zhj.tanhua.recommend.pojo.po.RecommendUser;
import com.zhj.tanhua.server.pojo.vo.recommend.RecommendUserVo;
import com.zhj.tanhua.server.pojo.bo.recommend.TodayBestBo;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐模块的服务层
 *
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Service
public class RecommendService {

    @Autowired
    private UserService userService;

    @DubboReference(version = "1.0", url = "dubbo://127.0.0.1:19200")
    RecommendUserApi recommendUserApi;

    /**
     * 获取今日佳人
     *
     * @return TodayBestBo
     */
    @SneakyThrows
    public TodayBestBo getTodayBest() {

        // 获取当前登录用户
        User user = UserThreadLocal.get();

        // 查询缘分值最高的推荐用户
        RecommendUser recommendUser = recommendUserApi.getBestRecommendUser(user.getId());

        // 查询缘分值最高的推荐用户的详细信息
        UserInfoTo userInfo = userService.getUserInfo(recommendUser.getUserId());

        // 构建今日佳人
        TodayBestBo todayBest = new TodayBestBo();
        BeanUtils.copyProperties(userInfo, todayBest);
        todayBest.setUserId(recommendUser.getUserId());
        todayBest.setFate(recommendUser.getFate().intValue());

        return todayBest;
    }

    /**
     * 获取推荐用户列表
     *
     * @param recommendUserVo  获取推荐用户列表参数
     * @return PageResult<TodayBestBo>
     */
    @SneakyThrows
    public PageResult<TodayBestBo> getRecommendUsers(RecommendUserVo recommendUserVo) {

        // 获取当前登录用户
        User user = UserThreadLocal.get();

        // 查询推荐用户列表
        PageResult<RecommendUser> recommendUsers = recommendUserApi.getRecommendUsers(user.getId(),
                recommendUserVo.getPageNum(), recommendUserVo.getPageSize());
        Map<Long, Double> recommendUserMap = recommendUsers.getData()
                .stream().collect(Collectors.toMap(RecommendUser::getUserId, RecommendUser::getFate));

        // 查询推荐用户的详细信息
        List<UserInfoTo> userInfoTos = userService.getUserInfos(new ArrayList<>(recommendUserMap.keySet()),
                null == recommendUserVo.getSex() ? null : recommendUserVo.getSex().getValue(),
                recommendUserVo.getAge(), recommendUserVo.getCity());

        // 构建推荐用户列表
        List<TodayBestBo> todayBests = new ArrayList<>();
        for (UserInfoTo userInfoTo : userInfoTos) {
            TodayBestBo todayBest = new TodayBestBo();
            BeanUtils.copyProperties(userInfoTo, todayBest);
            todayBest.setFate(recommendUserMap.get(userInfoTo.getUserId()).intValue());
            todayBests.add(todayBest);
        }

        // 对结果集做排序，按照缘分值倒序排序
        todayBests.sort((t1, t2) -> t2.getFate() - t1.getFate());

        return PageResult.<TodayBestBo>builder()
                .total((long) todayBests.size())
                .pageNum((long) recommendUserVo.getPageNum())
                .pageSize((long) recommendUserVo.getPageSize())
                .hasNext((long) recommendUserVo.getPageNum() * recommendUserVo.getPageSize() < todayBests.size())
                .data(todayBests).build();
    }
}
