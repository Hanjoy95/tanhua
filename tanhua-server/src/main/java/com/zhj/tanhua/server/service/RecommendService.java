package com.zhj.tanhua.server.service;

import com.zhj.tanhua.common.vo.PageResult;
import com.zhj.tanhua.recommend.api.RecommendUserApi;
import com.zhj.tanhua.recommend.dto.RecommendUserDto;
import com.zhj.tanhua.server.vo.RecommendUserVo;
import com.zhj.tanhua.server.vo.TodayBestVo;
import com.zhj.tanhua.user.dto.UserInfoDto;
import com.zhj.tanhua.user.dto.UserDto;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Service
public class RecommendService {

    @Autowired
    private UserService userService;

    @DubboReference(version = "1.0", url = "dubbo://127.0.0.1:19200")
    RecommendUserApi recommendUserApi;

    private static final String COMMA = ",";

    /**
     * 获取今日佳人
     *
     * @param token 用户token
     * @return TodayBestVo
     */
    @SneakyThrows
    public TodayBestVo getTodayBest(String token) {

        // 根据token查询当前登录的用户信息
        UserDto user = userService.getUserByToken(token);

        // 查询缘分值最高的推荐用户
        RecommendUserDto recommendUser = recommendUserApi.getBestRecommendUser(user.getId());

        // 查询缘分值最高的推荐用户的详细信息
        UserInfoDto userInfo = userService.getUserInfo(recommendUser.getUserId());

        // 构建今日佳人
        TodayBestVo todayBest = new TodayBestVo();
        BeanUtils.copyProperties(userInfo, todayBest);
        todayBest.setTags(Arrays.asList(userInfo.getTags().split(COMMA)));
        todayBest.setUserId(recommendUser.getUserId());
        todayBest.setFate(recommendUser.getFate().intValue());

        return todayBest;
    }

    /**
     * 获取推荐用户列表
     *
     * @param token 用户token
     * @param recommendUserVo  获取推荐用户列表参数
     * @return PageResult<TodayBestVo>
     */
    @SneakyThrows
    public PageResult<TodayBestVo> getRecommendUsers(String token, RecommendUserVo recommendUserVo) {

        // 根据token查询当前登录的用户信息
        UserDto user = userService.getUserByToken(token);

        // 查询推荐用户列表
        PageResult<RecommendUserDto> recommendUsers = recommendUserApi.getRecommendUsers(user.getId(),
                recommendUserVo.getPageNum(), recommendUserVo.getPageSize());
        Map<Long, Double> recommendUserMap = recommendUsers.getData()
                .stream().collect(Collectors.toMap(RecommendUserDto::getUserId, RecommendUserDto::getFate));

        // 查询推荐用户的详细信息
        List<UserInfoDto> userInfoDtos = userService.getUserInfos(new ArrayList<>(recommendUserMap.keySet()),
                null == recommendUserVo.getSex() ? null : recommendUserVo.getSex().getValue(),
                recommendUserVo.getAge(), recommendUserVo.getCity());

        // 构建推荐用户列表
        List<TodayBestVo> todayBests = new ArrayList<>();
        for (UserInfoDto userInfoDto : userInfoDtos) {
            TodayBestVo todayBest = new TodayBestVo();
            BeanUtils.copyProperties(userInfoDto, todayBest);
            todayBest.setTags(Arrays.asList(userInfoDto.getTags().split(COMMA)));
            todayBest.setFate(recommendUserMap.get(userInfoDto.getUserId()).intValue());
            todayBests.add(todayBest);
        }

        // 对结果集做排序，按照缘分值倒序排序
        todayBests.sort((t1, t2) -> t2.getFate() - t1.getFate());

        return PageResult.<TodayBestVo>builder()
                .total((long) todayBests.size())
                .pageNum((long) recommendUserVo.getPageNum())
                .pageSize((long) recommendUserVo.getPageSize())
                .hasNext((long) recommendUserVo.getPageNum() * recommendUserVo.getPageSize() < todayBests.size())
                .data(todayBests).build();
    }
}
