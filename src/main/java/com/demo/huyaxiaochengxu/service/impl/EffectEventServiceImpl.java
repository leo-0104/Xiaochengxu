package com.demo.huyaxiaochengxu.service.impl;

import com.demo.huyaxiaochengxu.entity.EffectEvent;
import com.demo.huyaxiaochengxu.mapper.EffectEventMapper;
import com.demo.huyaxiaochengxu.service.EffectEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EffectEventServiceImpl implements EffectEventService {

    @Autowired
    private EffectEventMapper effectEventMapper;
    @Override
    public int batchInsertEvent(List<EffectEvent> effectEventList) {
        return effectEventMapper.batchInsertEvent(effectEventList);
    }

    @Override
    public List<EffectEvent> getEventsByUid(String uid) {
        return effectEventMapper.getEventsByUid(uid);
    }

    @Override
    public List<EffectEvent> getEventsByGroupId(String groupId) {
        return effectEventMapper.getEventsByGroupId(groupId);
    }

    @Override
    public int batchUpdateEvent(String uid) {
        return effectEventMapper.batchUpdateEvent(uid);
    }
}


