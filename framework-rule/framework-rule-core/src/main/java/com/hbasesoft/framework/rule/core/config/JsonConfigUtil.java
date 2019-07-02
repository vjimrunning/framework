/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.rule.core.FlowComponent;

import lombok.NoArgsConstructor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年8月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.core.config <br>
 */
@NoArgsConstructor
public final class JsonConfigUtil {

    public static FlowConfig getFlowConfig(JSONObject obj) {
        return getFlowConfig(obj, new AtomicInteger(0));
    }

    @SuppressWarnings("rawtypes")
    private static FlowConfig getFlowConfig(JSONObject obj, AtomicInteger index) {
        FlowConfig config = new FlowConfig(index.incrementAndGet());

        String component = obj.getString("component");
        if (StringUtils.isNotEmpty(component)) {
            FlowComponent flowComponent = ContextHolder.getContext().getBean(component, FlowComponent.class);
            Assert.notNull(flowComponent, ErrorCodeDef.FLOW_COMPONENT_NOT_FOUND, component);
            config.setComponent(flowComponent);
        }

        String name = obj.getString("name");
        if (StringUtils.isEmpty(name)) {
            name = component;
        }
        config.setName(name);

        String version = obj.getString("version");
        if (StringUtils.isEmpty(version)) {
            version = "1.0";
        }

        JSONArray children = obj.getJSONArray("children");

        if (CollectionUtils.isNotEmpty(children)) {
            List<FlowConfig> childConfigList = new ArrayList<FlowConfig>();
            for (int i = 0, size = children.size(); i < size; i++) {
                childConfigList.add(getFlowConfig(children.getJSONObject(i), index));
            }
            config.setChildrenConfigList(childConfigList);
        }

        obj.remove("component");
        obj.remove("name");
        obj.remove("version");
        obj.remove("children");
        config.setConfigAttrMap(obj);
        return config;
    }
}
