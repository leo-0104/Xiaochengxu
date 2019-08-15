package com.demo.huyaxiaochengxu.mapper;

import com.demo.huyaxiaochengxu.entity.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceInfoMapper {

    public List<DeviceInfo> getDeviceInfoByUid(@Param("profileUid") String profileUid); //获取主播设备清单
    public int batchInsertDeviceInfo(@Param("deviceInfoList") List<DeviceInfo> deviceInfoList); //新增设备信息
    public int deleteDeviceById(@Param("effectId") int effectId); //删除设备
}
